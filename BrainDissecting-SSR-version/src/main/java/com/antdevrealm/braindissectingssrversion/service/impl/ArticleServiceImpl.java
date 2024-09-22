package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.article.FetchArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.comment.DisplayCommentDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CommentEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.CategoryService;
import com.jayway.jsonpath.JsonPath;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
public class ArticleServiceImpl implements ArticleService {

    private final static Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleRepository articleRepository;

    private final CategoryService categoryService;

    private final RestClient restClient;

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    private List<String> themes = List.of("neuroscience", "brain", "psychology", "human body", "medicine");

    private final Random random = new Random();

    private int currentThemeIndex;

    public ArticleServiceImpl(RestClient restClient,
                              ArticleRepository articleRepository, CategoryService categoryService,
                              ModelMapper modelMapper, UserRepository userRepository) {
        this.restClient = restClient;
        this.articleRepository = articleRepository;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

//    @Scheduled(cron = "0 0 0 * * ?") runs once a day at midnight


    @Override
    @Transactional
    public List<DisplayArticleDTO> getAllArticles() {
        return articleRepository.findAll().stream().map(this::mapToArticleDTO).toList();
    }

    @Override
    @Transactional
    @Modifying
    public boolean deleteArticle(Long articleId) {

        if(articleRepository.existsById(articleId)) {
            articleRepository.removeAllFromUsersFavourites(articleId);

            articleRepository.deleteById(articleId);
        }
        return false;
    }

    @Override
    @Modifying
    @Transactional
//    @Scheduled(cron = "*/30 * * * * ?")
    public void updateArticles() {
        logger.info("Scheduled task started at: {}", LocalTime.now());

        String currentTheme = themes.get(currentThemeIndex);
        currentThemeIndex = (currentThemeIndex + 1) % themes.size();

        List<FetchArticleDTO> fetchArticleDTOS = fetchArticles(currentTheme);

//        List<ArticleEntity> all = articleRepository.findAll();
//
//        List<ArticleEntity> withoutComments = new ArrayList<>();
//
//        for (ArticleEntity articleEntity : all) {
//            if (articleEntity.getComments().isEmpty()) {
//                withoutComments.add(articleEntity);
//            }
//        }
//
//        articleRepository.deleteAll(withoutComments);

        for (FetchArticleDTO dto : fetchArticleDTOS) {
            ArticleEntity articleEntity = mapToArticleEntity(dto);

            List<CategoryEntity> articleCategories = articleEntity.getCategories();

            Optional<CategoryEntity> optCategory = categoryService.getByName(currentTheme);

            if (optCategory.isPresent()) {
                articleCategories.add(optCategory.get());
                articleRepository.saveAndFlush(articleEntity);

                logger.info("Program in continue mode: {}", LocalTime.now());
                continue;
            }

            CategoryEntity categoryEntity = categoryService.addCategory(currentTheme);

            articleCategories.add(categoryEntity);
            articleRepository.saveAndFlush(articleEntity);

            logger.info("Scheduled task ended at: {}", LocalTime.now());
        }
    }


    // TODO: Error handling
    @Override
    public List<FetchArticleDTO> fetchArticles(String theme) {

        int pageNumber = random.nextInt(10) + 1;

        String body = restClient
                .get()
                .uri("https://doaj.org/api/v3/search/articles/" + theme + "?page=" + pageNumber + "1&pageSize=2")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        List<String> titles = JsonPath.parse(body).read("$.results[*].bibjson.title", List.class);
        List<String> abstractTexts = JsonPath.parse(body).read("$.results[*].bibjson.abstract", List.class);

        List<String> links = JsonPath.parse(body).read("$.results[*].bibjson.link[?(@.type=='fulltext')].url", List.class);

        List<FetchArticleDTO> fetchArticleDTOS = new ArrayList<>();


        if (titles.size() == abstractTexts.size()) {

            for (int i = 0; i < titles.size(); i++) {
                FetchArticleDTO fetchArticleDTO = new FetchArticleDTO();

                fetchArticleDTO.setTitle(titles.get(i))
                                .setContent(abstractTexts.get(i))
                                        .setLink(links.size() > i ? links.get(i) : "");

                fetchArticleDTOS.add(fetchArticleDTO);
            }
        }

        return fetchArticleDTOS;

    }


    public void addTheme(String theme) {
        this.themes.add(theme);

        categoryService.addCategory(theme);
    }


    // TODO: Consider changing the hard-coded themes List
    public void updateCategories() {
        themes.forEach(categoryService::addCategory);
    }

    public boolean removeTheme(Long categoryId) {
        Optional<CategoryEntity> byId = categoryService.getById(categoryId);

        if (byId.isEmpty()) {
            return false;
        }

        CategoryEntity categoryEntity = byId.get();
        themes.remove(categoryEntity.getName());

        categoryService.removeCategory(categoryId);

        return true;
    }

    @Override
    @Transactional
    public List<DisplayArticleDTO> getAllByCategory(String categoryName) {
        Optional<CategoryEntity> categoryByName = categoryService.getByName(categoryName);

        if (categoryByName.isEmpty()) {
            return new ArrayList<>();
        }

        CategoryEntity categoryEntity = categoryByName.get();

        List<ArticleEntity> articlesByCategory = categoryEntity.getArticles();

        if (articlesByCategory.isEmpty()) {
            return new ArrayList<>();
        }

        return articlesByCategory.stream().map(this::mapToArticleDTO).toList();
    }

    @Override
    @Transactional
    public List<DisplayArticleDTO> getUserFavourites(Long userId) {
        Optional<UserEntity> optUser = userRepository.findById(userId);

        if (optUser.isEmpty()) {
            return new ArrayList<>();
        }

        UserEntity userEntity = optUser.get();

        return userEntity.getFavourites().stream().map(this::mapToArticleDTO).toList();
    }


    private DisplayArticleDTO mapToArticleDTO(ArticleEntity articleEntity) {
        DisplayArticleDTO displayArticleDTO = modelMapper.map(articleEntity, DisplayArticleDTO.class);

        List<CommentEntity> comments = articleEntity.getComments();

        if(comments.isEmpty()) {
            displayArticleDTO.setComments(new ArrayList<>());
        } else {
            List<DisplayCommentDTO> displayCommentDTOList = comments.stream().map(this::mapToCommentDTO).toList();

            displayArticleDTO.setComments(displayCommentDTOList);
        }

        List<CategoryEntity> categories = articleEntity.getCategories();

        displayArticleDTO.setCategories(categories.stream().map(CategoryEntity::getName).toList());

        return displayArticleDTO;
    }

    private ArticleEntity mapToArticleEntity(FetchArticleDTO fetchArticleDTO) {

        Optional<ArticleEntity> optArticle = articleRepository.findByTitle(fetchArticleDTO.getTitle());

        return optArticle.orElseGet(() -> modelMapper.map(fetchArticleDTO, ArticleEntity.class));

    }

    private DisplayCommentDTO mapToCommentDTO(CommentEntity comment) {
        DisplayCommentDTO displayCommentDTO = new DisplayCommentDTO();

        displayCommentDTO.setId(comment.getId())
                .setContent(comment.getContent())
                .setAuthor(comment.getUser().getUsername())
                .setAuthorId(comment.getUser().getId());

        return displayCommentDTO;
    }


}

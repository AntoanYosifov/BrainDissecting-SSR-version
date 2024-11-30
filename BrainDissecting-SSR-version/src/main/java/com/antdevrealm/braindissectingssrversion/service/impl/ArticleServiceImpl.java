package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.article.FetchArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.comment.DisplayCommentDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CommentEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.CategoryService;
import com.jayway.jsonpath.JsonPath;
import org.jsoup.Jsoup;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Override
    @Transactional
    public List<DisplayArticleDTO> getAllApproved() {
        return articleRepository.findApprovedArticles().stream().map(this::mapToArticleDTO).toList();
    }

    @Override
    @Transactional
    public List<DisplayArticleDTO> getAllPending() {
        return articleRepository.findPendingArticles().stream().map(this::mapToArticleDTO).toList();
    }

    @Override
    public int countPendingArticles() {
        return articleRepository.countByStatus(Status.PENDING);
    }

    @Override
    @Transactional
    @Modifying
    public boolean deleteArticle(Long articleId) {
        if (articleRepository.existsById(articleId)) {

            Optional<ArticleEntity> byId = articleRepository.findById(articleId);

            if (byId.isPresent() && byId.get().isFavourite()) {
                articleRepository.removeAllFromUsersFavourites(articleId);
            }
            articleRepository.deleteById(articleId);

            return true;
        }
        return false;
    }

    @Override
    @Modifying
    @Transactional
//    @Scheduled(cron = "0 0 0 * * ?") runs once a day at midnight
//    @Scheduled(cron = "*/30 * * * * ?")
    public boolean updateArticles() {
        logger.info("Scheduled task started at: {}", LocalTime.now());

        List<String> themes = getThemes();

        if (themes.isEmpty()) {
            logger.warn("No themes available to fetch articles.");
            return false;
        }

        String currentTheme = themes.get(currentThemeIndex);
        currentThemeIndex = (currentThemeIndex + 1) % themes.size();

        List<FetchArticleDTO> fetchArticleDTOS = fetchArticles(currentTheme);

        for (FetchArticleDTO dto : fetchArticleDTOS) {

            Optional<ArticleEntity> byTitle = articleRepository.findByTitle(dto.getTitle());

            if (byTitle.isPresent()) {
                continue;
            }

            ArticleEntity articleEntity = mapToArticleEntity(dto);

            List<CategoryEntity> articleCategories = articleEntity.getCategories();

            Optional<CategoryEntity> optCategory = categoryService.getByName(currentTheme);

            if (optCategory.isEmpty()) {
                return false;
            }

            articleCategories.add(optCategory.get());
            articleRepository.saveAndFlush(articleEntity);

            logger.info("Program in continue mode: {}", LocalTime.now());

            logger.info("Scheduled task ended at: {}", LocalTime.now());
        }

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FetchArticleDTO> fetchArticles(String theme) {
        int pageNumber = random.nextInt(10) + 1;
        String body = restClient
                .get()
                .uri("https://doaj.org/api/v3/search/articles/" + theme + "?page=" + pageNumber + "&pageSize=10")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        List<String> titles = JsonPath.parse(body).read("$.results[*].bibjson.title", List.class);
        List<String> abstractTexts = JsonPath.parse(body).read("$.results[*].bibjson.abstract", List.class);
        List<String> links = JsonPath.parse(body).read("$.results[*].bibjson.link[?(@.type=='fulltext')].url", List.class);
        List<String> journalTitles = JsonPath.parse(body).read("$.results[*].bibjson.journal.title", List.class);

        List<FetchArticleDTO> fetchArticleDTOS = new ArrayList<>();

        if (titles.size() == abstractTexts.size()) {

            for (int i = 0; i < titles.size(); i++) {
                FetchArticleDTO fetchArticleDTO = new FetchArticleDTO();

                fetchArticleDTO.setTitle(Jsoup.parse(titles.get(i)).text())
                        .setContent(Jsoup.parse(abstractTexts.get(i)).text())
                        .setLink(links.size() > i ? Jsoup.parse(links.get(i)).text() : "")
                        .setJournalTitle(journalTitles.size() > i ? Jsoup.parse(journalTitles.get(i)).text() : "");

                fetchArticleDTOS.add(fetchArticleDTO);
            }
        }

        return fetchArticleDTOS;
    }

    @Override
    public List<String> getThemes() {
        return categoryService.getAll().stream().map(CategoryEntity::getName).toList();
    }

    @Override
    public boolean addTheme(String theme) {
        try {
            return categoryService.addCategory(theme);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    @Modifying
    public boolean removeTheme(String theme) {
        Optional<CategoryEntity> byName = categoryService.getByName(theme);

        if (byName.isEmpty()) {
            return false;
        }

        CategoryEntity categoryEntity = byName.get();

        List<ArticleEntity> allByCategoriesContaining = articleRepository.findAllByCategoriesContainingAndFavouriteIsTrue(categoryEntity);

        for (ArticleEntity articleEntity : allByCategoriesContaining) {
            articleRepository.removeAllFromUsersFavourites(articleEntity.getId());
        }

        articleRepository.deleteAllByCategoriesContaining(categoryEntity);

        return categoryService.removeCategory(categoryEntity);
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

        if (comments.isEmpty()) {
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
        ArticleEntity articleEntity = optArticle.orElseGet(() -> modelMapper.map(fetchArticleDTO, ArticleEntity.class));
        articleEntity.setStatus(Status.PENDING);

        return articleEntity;
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

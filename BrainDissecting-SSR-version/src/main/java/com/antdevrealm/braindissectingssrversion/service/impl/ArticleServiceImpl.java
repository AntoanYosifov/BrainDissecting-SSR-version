package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.article.FetchArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.comment.DisplayCommentDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CommentEntity;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.jayway.jsonpath.JsonPath;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final RestClient restClient;

    private final ArticleRepository articleRepository;

    private final ModelMapper modelMapper;

    public ArticleServiceImpl(RestClient restClient,
                              ArticleRepository articleRepository,
                              ModelMapper modelMapper) {
        this.restClient = restClient;
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public void updateArticles() {
        List<FetchArticleDTO> fetchArticleDTOS = fetchArticles();

        fetchArticleDTOS.forEach(dto -> articleRepository.save(mapToArticleEntity(dto)));
    }

    @Override
    public List<DisplayArticleDTO> getAllArticles() {
        return articleRepository.findAll().stream().map(this::mapToArticleDTO).toList();
    }


    // TODO: Error handling
    @Override
    public List<FetchArticleDTO> fetchArticles() {
        String body = restClient
                .get()
                .uri("https://doaj.org/api/search/articles/computers?page=1&pageSize=2")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        List<String> titles = JsonPath.parse(body).read("$.results[*].bibjson.title", List.class);
        List<String> abstractTexts = JsonPath.parse(body).read("$.results[*].bibjson.abstract", List.class);

        List<FetchArticleDTO> fetchArticleDTOS = new ArrayList<>();


        if(titles.size() == abstractTexts.size()) {

            for (int i = 0; i < titles.size(); i++) {
                fetchArticleDTOS.add(new FetchArticleDTO(titles.get(i), abstractTexts.get(i)));
            }
        }

        return fetchArticleDTOS;

    }

    private DisplayArticleDTO mapToArticleDTO(ArticleEntity articleEntity) {
        DisplayArticleDTO displayArticleDTO = modelMapper.map(articleEntity, DisplayArticleDTO.class);

        List<CommentEntity> comments = articleEntity.getComments();

        if(comments.isEmpty()) {
            displayArticleDTO.setComments(new ArrayList<>());

            return displayArticleDTO;
        }

        List<DisplayCommentDTO> displayCommentDTOList = comments.stream().map(this::mapToCommentDTO).toList();

        displayArticleDTO.setComments(displayCommentDTOList);

        return displayArticleDTO;
    }

    private ArticleEntity mapToArticleEntity(FetchArticleDTO fetchArticleDTO) {
        return modelMapper.map(fetchArticleDTO, ArticleEntity.class);
    }

    private DisplayCommentDTO mapToCommentDTO(CommentEntity comment) {
        DisplayCommentDTO displayCommentDTO = new DisplayCommentDTO();

        displayCommentDTO.setId(comment.getId())
                .setContent(comment.getContent())
                .setAuthor(comment.getUser().getUsername());

        return displayCommentDTO;
    }
}

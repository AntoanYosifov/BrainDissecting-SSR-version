package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.dto.article.ArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
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
        List<ArticleDTO> articleDTOS = fetchArticles();

        articleDTOS.forEach(dto -> articleRepository.save(mapToArticleEntity(dto)));
    }

    @Override
    public List<ArticleDTO> getAllArticles() {
        return articleRepository.findAll().stream().map(this::mapToArticleDTO).toList();
    }


    // TODO: Error handling
    @Override
    public List<ArticleDTO> fetchArticles() {
        String body = restClient
                .get()
                .uri("https://doaj.org/api/search/articles/computers?page=1&pageSize=2")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        List<String> titles = JsonPath.parse(body).read("$.results[*].bibjson.title", List.class);
        List<String> abstractTexts = JsonPath.parse(body).read("$.results[*].bibjson.abstract", List.class);

        List<ArticleDTO> articleDTOS = new ArrayList<>();


        if(titles.size() == abstractTexts.size()) {

            for (int i = 0; i < titles.size(); i++) {
                articleDTOS.add(new ArticleDTO(titles.get(i), abstractTexts.get(i)));
            }
        }

        return articleDTOS;

    }



    private ArticleDTO mapToArticleDTO(ArticleEntity articleEntity) {
        return modelMapper.map(articleEntity, ArticleDTO.class);
    }

    private ArticleEntity mapToArticleEntity(ArticleDTO articleDTO) {
        return modelMapper.map(articleDTO, ArticleEntity.class);
    }
}

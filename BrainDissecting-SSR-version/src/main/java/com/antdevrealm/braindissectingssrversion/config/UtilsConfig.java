package com.antdevrealm.braindissectingssrversion.config;


import com.antdevrealm.braindissectingssrversion.model.dto.article.FetchArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class UtilsConfig {

    @Bean
    public ModelMapper getModelMapper() {

        ModelMapper modelMapper = new ModelMapper();
        TypeMap<ArticleEntity, FetchArticleDTO> typeMap = modelMapper.createTypeMap(ArticleEntity.class, FetchArticleDTO.class);
        typeMap.addMappings(mapper -> mapper.skip(FetchArticleDTO::setComments));

        return modelMapper;

    }


    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }
}
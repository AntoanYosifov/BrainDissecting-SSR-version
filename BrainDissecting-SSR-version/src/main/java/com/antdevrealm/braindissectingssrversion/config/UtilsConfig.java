package com.antdevrealm.braindissectingssrversion.config;
import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.article.FetchArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.DisplayUserInfoDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
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
        TypeMap<FetchArticleDTO, ArticleEntity> fetchArticleTypeMap = modelMapper.createTypeMap(FetchArticleDTO.class, ArticleEntity.class);
        fetchArticleTypeMap.addMappings(mapper -> mapper.skip(ArticleEntity::setComments));

        TypeMap<ArticleEntity , DisplayArticleDTO> displayArticleTypeMap = modelMapper.createTypeMap(ArticleEntity.class, DisplayArticleDTO.class);
        displayArticleTypeMap.addMappings(mapper -> mapper.skip(DisplayArticleDTO::setCategories));

        TypeMap<UserEntity, DisplayUserInfoDTO> userTypeMap = modelMapper.createTypeMap(UserEntity.class, DisplayUserInfoDTO.class);
        userTypeMap.addMappings(mapper -> mapper.skip(DisplayUserInfoDTO::setRoles));

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

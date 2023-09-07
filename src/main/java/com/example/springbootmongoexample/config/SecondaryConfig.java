package com.example.springbootmongoexample.config;

import com.example.springbootmongoexample.mongoRepository2.CallLogDocRepository;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static java.util.Collections.singletonList;

/**
 * 여러개의 DataBase에 connection해야하는 경우 각 DB별로 아래와같은 config파일을 만들어 주어야한다.
 */
@Configuration
//basePackageClasses = BookDocRepository.class,
@EnableMongoRepositories(
        basePackageClasses = {
                CallLogDocRepository.class
        },//이거 옵션이 같은 위치의 패키지만 인식을 하기 때문에 여러개 적어줄 필요는 없고 그냥 해당 패키지의 아무거나 한개만 넣어줘도됨
        mongoTemplateRef = "secondaryMongoTemplate"
)
@EnableConfigurationProperties
public class SecondaryConfig {

    @Bean(name = "secondaryProperties")
    @ConfigurationProperties(prefix = "mongodb.secondary")
    public MongoProperties secondaryProperties() {
        return new MongoProperties();
    }

    @Bean(name = "secondaryMongoClient")
    public MongoClient mongoClient(@Qualifier("secondaryProperties") MongoProperties mongoProperties) {

        MongoCredential credential = MongoCredential
                .createCredential(mongoProperties.getUsername(), mongoProperties.getAuthenticationDatabase(), mongoProperties.getPassword());

        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder
                        .hosts(singletonList(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()))))
                .credential(credential)
                .build());
    }

    @Bean(name = "secondaryMongoDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(
            @Qualifier("secondaryMongoClient") MongoClient mongoClient,
            @Qualifier("secondaryProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase());
    }

    @Bean(name = "secondaryMongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("secondaryMongoDBFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }

    /*@Bean("secondaryConvertingSet")
    public MappingMongoConverter convertingSet(MongoDatabaseFactory factory, MongoMappingContext ctx,
                                               BeanFactory beanFactory) {
        DbRefResolver resolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(resolver, ctx);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));  //여기가 실질적으로 _class 컬럼을 제거하기 위한 핵심부분
        return converter;
    }*/
}
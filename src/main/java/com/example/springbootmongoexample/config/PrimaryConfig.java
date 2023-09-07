package com.example.springbootmongoexample.config;

import com.example.springbootmongoexample.mongoRepository.BoardDocRepository;
import com.example.springbootmongoexample.mongoRepository.UserDocRepository;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static java.util.Collections.singletonList;

/**
 * 여러개의 DataBase에 connection해야하는 경우 각 DB별로 아래와같은 config파일을 만들어 주어야한다.
 */
@Configuration
/*
더 많은 데이터베이스 구성을 추가할 것이므로 여기에서 빈이 @Primary 여야 합니다 . 그렇지 않으면 앞서 논의한 고유성 제약 조건에 빠지게 됩니다.
여러 EntityManager 를 매핑할 때 JPA에서도 동일한 작업을 수행합니다 . 마찬가지로 @EnableMongoRepositories에 Mongotemplate 에 대한 참조가 필요합니다 .
*/
//basePackageClasses = BoardDocRepository.class, repository를 사용하려면 설정해주어야하는 옵션 (없으면 template은 사용가능하지만 repository는 사용할 수 없다.
@EnableMongoRepositories(
        //Repository를 사용하고 싶으면 아래와 같은 방식으로 해당되는 Repository들을 나열해 주어야한다.
        basePackageClasses = {
            BoardDocRepository.class,
            UserDocRepository.class
        },//이거 옵션이 같은 위치의 패키지만 인식을 하기 때문에 여러개 적어줄 필요는 없고 그냥 해당 패키지의 아무거나 한개만 넣어줘도됨
        //위에처럼 여려개를 적는게 의미가 없음
        mongoTemplateRef = "primaryMongoTemplate")

@EnableConfigurationProperties
public class PrimaryConfig {
    /**
     * 사용할 프로퍼티에 대한 정보
     */
    @Bean(name = "primaryProperties")
    @ConfigurationProperties(prefix = "mongodb.primary")
    @Primary
    public MongoProperties primaryProperties() {
        return new MongoProperties();
    }

    /**
     * 엑세스 권한 부여를 위한 인중
     */
    @Bean(name = "primaryMongoClient")
    public MongoClient mongoClient(@Qualifier("primaryProperties") MongoProperties mongoProperties) {

        MongoCredential credential = MongoCredential
                .createCredential(mongoProperties.getUsername(), mongoProperties.getAuthenticationDatabase(), mongoProperties.getPassword());

        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder
                        .hosts(singletonList(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()))))
                .credential(credential)
                .build());
    }

    //TODO 최신 릴리스에서 제안한 대로 연결 문자열에서 MongoTemplate 을 만드는 대신 SimpleMongoClientDatabaseFactory 를 사용하고 있습니다.
    @Primary
    @Bean(name = "primaryMongoDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(
            @Qualifier("primaryMongoClient") MongoClient mongoClient,
            @Qualifier("primaryProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase());
    }

    @Primary
    @Bean(name = "primaryMongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("primaryMongoDBFactory") MongoDatabaseFactory primaryMongoDBFactory) {
        /*DbRefResolver resolver = new DefaultDbRefResolver(primaryMongoDBFactory);
        MappingMongoConverter converter = new MappingMongoConverter(resolver, new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(primaryMongoDBFactory, converter);*/
        return new MongoTemplate(primaryMongoDBFactory);
    }
    /*@Bean(name = "primaryConvertingSet")
    public MappingMongoConverter primaryConvertingSet(
            @Qualifier("primaryMongoDBFactory") MongoDatabaseFactory primaryMongoDBFactory,
            MongoMappingContext ctx
    ) {
        DbRefResolver resolver = new DefaultDbRefResolver(primaryMongoDBFactory);
        MappingMongoConverter converter = new MappingMongoConverter(resolver, ctx);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));  //여기가 실질적으로 _class 컬럼을 제거하기 위한 핵심부분
        return converter;
    }*/

    /*@Bean(name = "primaryMappingMongoConverter")
    public MappingMongoConverter primaryMappingMongoConverter() {

    }*/

}

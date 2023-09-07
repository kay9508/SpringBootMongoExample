package com.example.springbootmongoexample.config;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig {

    /**
     * 몽고템플릿에서는 데이터를 매핑 할 때 주어진 클래스<T> 의 패키지와 이름을 넣도록 기본설정이 되어 있습니다.
     *
     * MongoTemplate를 사용해서 insert를 하게되면 기본적으로
     * _class라는 컬럼이 생성되는데 이를 사용하지 않게 하려면 아래와같은
     * 컨버팅 세팅을 해주어야한다.
     */
    @Bean
    public MappingMongoConverter convertingSet(MongoDatabaseFactory factory,
                                               MongoMappingContext ctx,
                                               MongoCustomConversions conversions
    ) {
//        DbRefResolver resolver = new DefaultDbRefResolver(factory);
//        MappingMongoConverter converter = new MappingMongoConverter(resolver, ctx);
//        converter.setTypeMapper(new DefaultMongoTypeMapper(null));  //여기가 실질적으로 _class 컬럼을 제거하기 위한 핵심부분

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, ctx);
        mappingConverter.setCustomConversions(conversions);
        return mappingConverter;
    }


    //아래는 원래 형태  [https://lts0606.tistory.com/671]
    //이렇게만 사용해도 DB하나 쓸땐 문제가 안되는데...
    /*@Bean
    public MappingMongoConverter 컨버팅설정(MongoDatabaseFactory factory, MongoMappingContext ctx,
                                       BeanFactory beanFactory) {
        DbRefResolver resolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(resolver, ctx);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));  //요기!
        return converter;
    }*/
}

//위의 방식을 사용하면 하나의 DB를 사용할때만 적용되고 여러개의 DB를 사용할 때는 적용이 안된다.

//TODO 일단 작업하는 서비스에서는 insert를 목적으로 하는 것이 아니라 Load만을 목적으로 하기 때문에 넘어가고 나중에 다시 확인해보자
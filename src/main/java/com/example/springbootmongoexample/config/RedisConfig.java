package com.example.springbootmongoexample.config;

import com.example.springbootmongoexample.redisRepository.TestRedisRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(
        basePackageClasses = TestRedisRepository.class,
        redisTemplateRef = "redisTemplate")
//@EnableRedisRepositories
@EnableConfigurationProperties
public class RedisConfig {

//    private final RedisProperties redisProperties;
    /*@Value("${spring.redis.port}")
    private int redisPort;
    @Value("${spring.redis.host}")
    private String redisHost;*/

    @Bean(name = "redisProperties")
    @ConfigurationProperties(prefix = "spring.redis")
    @Primary
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean("redisConnectionFactory")
//    @Bean
    public RedisConnectionFactory redisConnectionFactory(@Qualifier("redisProperties") RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisProperties.getHost());
//        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisProperties.getPort());
//        redisConfiguration.setPort(redisPort);
//        redisConfiguration.setDatabase(redisProperties.getDatabase());
        redisConfiguration.setDatabase(0);
        return new LettuceConnectionFactory(redisConfiguration);
//        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean("redisTemplate")
    /*@Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }*/

//    @Bean
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //일반적인 key:value의 경우 시리얼라이저
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Hash를 사용할 경우 시리얼라이저
        //redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        //모든 경우
        //redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}

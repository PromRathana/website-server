package com.cambofreelance.websiteservice.configs;

import com.cambofreelance.websiteservice.logger.dto.ResponseCodeDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;

    /**
     * Reactive Redis Template
     */
    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
        ReactiveRedisConnectionFactory connectionFactory) {

        return new ReactiveRedisTemplate<>(
            connectionFactory,
            RedisSerializationContext.string());
    }

    /**
     * Main Redis Template
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
        RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = objectMapper.copy();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        GenericJackson2JsonRedisSerializer serializer =
            new GenericJackson2JsonRedisSerializer(mapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }

    /**
     * Spring Cache Manager (used by @Cacheable)
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer =
            new GenericJackson2JsonRedisSerializer(mapper);

        RedisCacheConfiguration config =
            RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()))
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                )
                .entryTtl(Duration.ofMinutes(5));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }

    /**
     * Redis template for ResponseCodeDto
     */
    @Bean
    public RedisTemplate<String, ResponseCodeDto> responseCodeRedisTemplate(
        RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, ResponseCodeDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = objectMapper.copy();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Jackson2JsonRedisSerializer<ResponseCodeDto> serializer =
            new Jackson2JsonRedisSerializer<>(mapper, ResponseCodeDto.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
}
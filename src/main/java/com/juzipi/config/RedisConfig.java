package com.juzipi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 创建redis
 *
 * @author juzipi
 * @date 2024/7/28 19:56
 */
@Configuration
public class RedisConfig {
	
	@Value("${spring.redis.host}")
	private String host;
	
	@Value("${spring.redis.port}")
	private int port;
	
	/**
	 * 配置 Redis 连接工厂
	 *
	 * @return
	 */
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		// 连接 redis 配置
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
		return new LettuceConnectionFactory(configuration);
	}
	
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		//创建 RedisTemplate 实例
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		//设置连接工厂
		template.setConnectionFactory(connectionFactory);
		//设置默认的序列化器为 GenericJackson2JsonRedisSerializer,用于序列化键和值为 Json 格式
		template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		
		template.setKeySerializer(new StringRedisSerializer());
		//设置值的序列化器为  GenericJackson2JsonRedisSerializer
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}
}

//package com.sunlands.deskmate.config;
//
//import com.sunlands.deskmate.service.impl.MessageServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.listener.PatternTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
//
//
///**
// * @author lishuai
// *
// */
//@Configuration
//public class RedisMQConfig {
//
//    @Autowired
//    private RedisConnectionFactory factory;
//
//    @Autowired
//    private MessageServiceImpl messageServiceImpl;
//
//    @Value("${queue}")
//    private String queue;
//
//    @Bean
//    RedisMessageListenerContainer container(MessageListenerAdapter messageListenerAdapter) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(factory);
//        container.addMessageListener(messageListenerAdapter, new PatternTopic(queue));
//        return container;
//    }
//
//    @Bean
//    MessageListenerAdapter messageListenerAdapter() {
//        return new MessageListenerAdapter(messageServiceImpl, "receiveMessage");
//    }
//}

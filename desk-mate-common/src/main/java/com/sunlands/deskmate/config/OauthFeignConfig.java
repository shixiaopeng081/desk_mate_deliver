package com.sunlands.deskmate.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * @author liude
 */
@Configuration
@EnableConfigurationProperties
@Slf4j
public class OauthFeignConfig {

    @Bean
    @ConfigurationProperties(prefix = "security.oauth2.client")
    public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        ClientCredentialsResourceDetails client = clientCredentialsResourceDetails();
        try {
            log.debug("oauth2FeignRequestInterceptor={}", new ObjectMapper().writeValueAsString(client));
        } catch (JsonProcessingException e) {
            log.warn("oauth2FeignRequestInterceptor", e);
        }
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), client);
    }

    @Bean
    public OAuth2RestTemplate clientCredentialsRestTemplate() {
        ClientCredentialsResourceDetails client = clientCredentialsResourceDetails();
        try {
            log.debug("clientCredentialsRestTemplate={}", new ObjectMapper().writeValueAsString(client));
        } catch (JsonProcessingException e) {
            log.warn("clientCredentialsRestTemplate", e);
        }
        return new OAuth2RestTemplate(client);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            try {
                String message = new ObjectMapper().readValue(response.body().asReader(), Map.class).get("message").toString();
                return new MyFeignException(response.status(), message);
            } catch (Exception e) {
                return FeignException.errorStatus(methodKey, response);
            }
        };
    }

    @SuppressWarnings("WeakerAccess")
    public static class MyFeignException extends FeignException {
        private MyFeignException(int status, String message) {
            super(status, message);
        }
    }


    /**
     * 避免在应用关闭的时候产生: Error creating bean with name 'eurekaAutoServiceRegistration': Singleton bean creation not allowed
     */
    @Component
    public static class FeignBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

        private static final String EUREKA_AUTO_SERVICE_REGISTRATION = "eurekaAutoServiceRegistration";
        private static final String FEIGN_CONTEXT = "feignContext";

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
            if (containsBeanDefinition(beanFactory, FEIGN_CONTEXT, EUREKA_AUTO_SERVICE_REGISTRATION)) {
                BeanDefinition bd = beanFactory.getBeanDefinition(FEIGN_CONTEXT);
                bd.setDependsOn(EUREKA_AUTO_SERVICE_REGISTRATION);
            }
        }

        private boolean containsBeanDefinition(ConfigurableListableBeanFactory beanFactory, String... beans) {
            return Arrays.stream(beans).allMatch(beanFactory::containsBeanDefinition);
        }
    }
}

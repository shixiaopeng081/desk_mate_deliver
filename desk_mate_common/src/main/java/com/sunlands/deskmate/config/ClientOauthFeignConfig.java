package com.sunlands.deskmate.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * 当接口需要client oauth认证的时候, 使用此配置从配置文件中加载clientId和clientSecret, 自动换取token并加入feign
 *
 * @author guoyang
 */
@Configuration
@EnableConfigurationProperties
@Slf4j
public class ClientOauthFeignConfig extends FeignConfig {

    @Bean
    @ConfigurationProperties(prefix = "security.oauth2.client")
    public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        ClientCredentialsResourceDetails client = clientCredentialsResourceDetails();
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), client);
    }

    @Bean
    public OAuth2RestTemplate clientCredentialsRestTemplate() {
        ClientCredentialsResourceDetails client = clientCredentialsResourceDetails();
        return new OAuth2RestTemplate(client);
    }

}

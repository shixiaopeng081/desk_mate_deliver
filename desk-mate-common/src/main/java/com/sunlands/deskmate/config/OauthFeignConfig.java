package com.sunlands.deskmate.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.*;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.feign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.netflix.feign.ribbon.LoadBalancerFeignClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

/**
 * @author anjl
 * 使用feign时，使用此配置
 */
@Configuration
@EnableConfigurationProperties
@Slf4j
public class OauthFeignConfig {

    /**-----------以下配置是为了本地调试使用，测试和正式环境将${eureka.debug}设置为false即可----------**/

    @Value("${eureka.debug:false}")
    private Boolean debug;
    @Value("${eureka.debug-url:null}")
    private String debugUrl;

    @Bean
    public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory, SpringClientFactory clientFactory) {
        return new LoadFeignBalancerFeignClient(new DefaultClient(null, null), cachingFactory, clientFactory);
    }


    class LoadFeignBalancerFeignClient implements Client {
        private LoadBalancerFeignClient loadBalancerFeignClient;

        public LoadFeignBalancerFeignClient(Client delegate, CachingSpringLoadBalancerFactory lbClientFactory, SpringClientFactory clientFactory) {
            loadBalancerFeignClient = new LoadBalancerFeignClient(delegate, lbClientFactory, clientFactory);
        }

        @Override
        public Response execute(Request request, Request.Options options) throws IOException {
            URI asUri = URI.create(request.url());
            String clientName = asUri.getHost();
            DefaultClient delegate = (DefaultClient) loadBalancerFeignClient.getDelegate();
            delegate.setClientName(clientName);
            return loadBalancerFeignClient.execute(request, options);
        }
    }


    class DefaultClient extends Client.Default implements Client {
        private String clientName;

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public DefaultClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
            super(sslContextFactory, hostnameVerifier);
        }

        @Override
        public Response execute(Request request, Request.Options options) throws IOException {
            if(debug){
                URI asUri = URI.create(request.url());
                String url = String.format("%s/%s%s?%s", debugUrl, clientName, asUri.getPath(), asUri.getQuery());
                request = Request.create(request.method(), url, request.headers(), request.body(), request.charset());
            }
            return super.execute(request, options);
        }
    }

    /**--------------------end--------------------------**/

    /**
     * 系统访问有oauth验证的服务时，通过注入一个ClientCredentialsResourceDetails用于换区token
     * @return ClientCredentialsResourceDetails
     */
    @Bean
    @ConfigurationProperties(prefix = "security.oauth2.client")
    public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

    /**
     * 给feign访问添加一个OAuth2FeignRequestInterceptor，用于在header中放入token
     * @return RequestInterceptor
     */
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

    /**
     * feign调用出错后处理
     * @return ErrorDecoder
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            try {
                String message = new ObjectMapper().readValue(response.body().asReader(), Map.class).get("message").toString();
                log.error("response response = {}", response);
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

    @Bean
    Logger.Level feignLoggerLevel() {
        //这里记录所有，根据实际情况选择合适的日志level
        return Logger.Level.FULL;
    }
}

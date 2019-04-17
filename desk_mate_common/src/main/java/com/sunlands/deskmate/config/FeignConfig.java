package com.sunlands.deskmate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import feign.FeignException;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.netflix.feign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

/**
 * 使用feignClient的时候, 传递所有Header(主要是Authorization)
 * 写在这里, 对所有的feignClient起作用
 *
 * @author liude
 */
@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor headerInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (Objects.isNull(attributes)) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();

            if (headerNames != null) {

                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    if ("Authorization".equalsIgnoreCase(name)) {
                        String values = request.getHeader(name);
                        requestTemplate.header(name, values);
                    }
                }
            }

        };
    }

    /**
     * Feign在DateConvert之前就初始化了， 会导致convert不全
     * 加上这个可以准备一些DateConverter
     *
     * @return Contract
     */
    @Bean
    public Contract feignContract() {
        return new SpringMvcContract();
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

    public static class MyFeignException extends FeignException {
        private MyFeignException(int status, String message) {
            super(status, message);
        }
    }


    /**
     * 解决应用关闭时候产生的异常信息
     * org.springframework.beans.factory.BeanCreationNotAllowedException: Error creating bean with name 'eurekaAutoServiceRegistration': Singleton bean creation not allowed while singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)
     * ...其实也不是很严重的问题
     */
    @Component
    public static class FeignBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

        private static final String FEIGN_CONTEXT = "feignContext";
        private static final String EUREKA_AUTO_SERVICE_REGISTRATION = "eurekaAutoServiceRegistration";

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

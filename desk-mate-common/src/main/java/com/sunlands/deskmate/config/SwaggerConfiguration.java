package com.sunlands.deskmate.config;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <p>swagger配置</p>
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author zl
 * @version 1.0
 * @package: com.sunlands.zlcx.mpmsgservice.config
 * @date Created in 2017/11/30 15:42
 * @copyright: Copyright (c) founders
 */
@EnableSwagger2
@Configuration
@ConfigurationProperties(prefix = "swagger")
@Setter
public class SwaggerConfiguration {

    @Value("${swagger.title}")
    String title;

    @Value("${swagger.description}")
    String description;

    @Value("${swagger.basePackage}")
    String basePackage;

    @Value("${swagger.version}")
    String version;

    @Value("${swagger.contact.name}")
    String contactName;

    @Value("${swagger.contact.email}")
    String contactEmail;

    @Bean
    public Docket myApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
//                .paths(PathSelectors.any())

                .build()
                ;
    }

    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .contact(new Contact(contactName, null, contactEmail))
                .description(description)
                .version(version)
                .build();
    }

}

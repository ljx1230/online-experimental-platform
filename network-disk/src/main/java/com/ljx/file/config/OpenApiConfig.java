package com.ljx.file.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @Author: ljx
 * @Date: 2023/12/20 17:06
 */
@Configuration
public class OpenApiConfig {
    @Bean(value = "indexApi")
    public Docket indexApi() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("网站前端接口分组").apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ljx.file.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("工程实践子项目网盘API文档")
                .description("工程实践子项目")
                .version("1.0")
                .build();
    }
}

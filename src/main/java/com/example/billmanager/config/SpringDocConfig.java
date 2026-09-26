package com.example.billmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "项目API文档",
        version = "1.0",
        description = "SpringBoot项目接口文档"
))
public class SpringDocConfig {

    @Bean
    public GroupedOpenApi billGroup() {
        return GroupedOpenApi.builder()
                .group("账单分组")
                .pathsToMatch("/api/bills/**")
                .build();
    }

    @Bean
    public GroupedOpenApi categoryGroup() {
        return GroupedOpenApi.builder()
                .group("分类分组")
                .pathsToMatch("/api/categories/**")
                .build();
    }
}

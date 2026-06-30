package com.example.store.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI storeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Store 商品管理系统 API")
                        .description("电商秒杀场景接口文档")
                        .version("v1.0")
                        .contact(new Contact().name("你的名字"))
                );
    }
}
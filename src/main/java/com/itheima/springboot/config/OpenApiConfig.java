package com.itheima.springboot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * 自定义OpenAPI文档信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("用户管理系统API文档")  //文档标题
                        .version("1.0.0")             //版本
                        .description("SpringBoot4 + SpringDoc 接口文档") //描述
                        .contact(new Contact()
                                .name("开发人员")
                                .email("test@xxx.com")
                        )
                );
    }
}

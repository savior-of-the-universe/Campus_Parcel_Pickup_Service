package com.team.admin.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
    // Temporarily disabled pagination interceptor due to version compatibility
    // @Bean
    // public MybatisPlusInterceptor mybatisPlusInterceptor() {
    //     MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    //     interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    //     return interceptor;
    // }
}

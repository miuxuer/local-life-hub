package com.llh.config;

import com.llh.interceptor.JwtTokenAdminInterceptor;
import com.llh.interceptor.JwtTokenUserInterceptor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/login")
                .excludePathPatterns("user/shop/status");

    }

    // 给Knife4j标题定义名称，可以省略
    @Bean
    public OpenAPI customOpenAPI() {
        log.info("准备生成接口文档...");
        return new OpenAPI()
                .info(new Info()
                        .title("多商户本地生活服务平台接口文档")
                        .version("2.0")
                        .description("多商户本地生活服务平台接口文档"));
    }

    /**
     * 管理端接口分组（扫描 com.llh.controller.admin 包）
     */
    @Bean
    public GroupedOpenApi adminApi() {
        log.info("准备生成管理端接口文档...");
        return GroupedOpenApi.builder()
                .group("管理端接口")
                .packagesToScan("com.llh.controller.admin")
                .build();
    }

    /**
     * 用户端接口分组（扫描 com.llh.controller.user 包）
     */
    @Bean
    public GroupedOpenApi userApi() {
        log.info("准备生成用户端接口文档...");
        return GroupedOpenApi.builder()
                .group("用户端接口")
                .packagesToScan("com.llh.controller.user")
                .build();
    }

}

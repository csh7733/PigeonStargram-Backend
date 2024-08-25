package com.pigeon_stargram.sns_clone.config;

import com.pigeon_stargram.sns_clone.config.auth.resolver.LoginUserArgumentResolver;
import com.pigeon_stargram.sns_clone.config.auth.resolver.NewUserEmailArgumentResolver;
import com.pigeon_stargram.sns_clone.config.auth.config.SessionExpiredInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    private final LoginUserArgumentResolver loginUserArgumentResolver;
    private final NewUserEmailArgumentResolver newUserEmailArgumentResolver;

    private final SessionExpiredInterceptor sessionExpiredInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserArgumentResolver);
        argumentResolvers.add(newUserEmailArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE","PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionExpiredInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/session/**")
                .excludePathPatterns("/api/test/not-login")
                .excludePathPatterns("/api/notifications/**")
                .excludePathPatterns("/error/**")
                .excludePathPatterns("/api/errors/**");

    }
}
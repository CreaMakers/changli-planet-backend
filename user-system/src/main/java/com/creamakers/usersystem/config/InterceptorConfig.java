package com.creamakers.usersystem.config;

import com.creamakers.usersystem.common.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;

    private static final String[] EXCLUDE_PATHS = {
            "/app/users/sessions",
            "/app/users/sessions/*",
            "/app/users",
            "/app/users/availability/**",
            "/app/users/auth/verification-code/register",
            "/app/users/auth/verification-code/login",
            "/app/users/auth/verification-code/forget-password",
            "/app/users/apk",
            "/app/users/register",
            "/app/users/me/avatar",
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .excludePathPatterns(EXCLUDE_PATHS)
                .addPathPatterns("/**")
                .order(10);
    }
}

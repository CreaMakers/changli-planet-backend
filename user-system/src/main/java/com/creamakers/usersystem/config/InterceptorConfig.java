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
            "/app/users/session",
            "/app/users",
            "/app/users/availability",
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

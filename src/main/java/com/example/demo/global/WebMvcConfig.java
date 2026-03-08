package com.example.demo.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.dir}")
    private String fileDir; // 예: /Users/you/qeat/uploads

    @Value("${file.url-prefix:/uploads}")
    private String urlPrefix; // 예: /uploads

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /uploads/** 로 요청이 오면
        registry.addResourceHandler(urlPrefix + "/**")
                // file.dir 폴더에서 파일을 찾아 내려준다
                .addResourceLocations("file:" + fileDir + "/");
    }
}

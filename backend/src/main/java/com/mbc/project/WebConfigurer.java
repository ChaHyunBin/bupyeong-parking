package com.mbc.project;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



// 웹과 연동 
@Configuration 
public class WebConfigurer implements WebMvcConfigurer {

	
	// 백엔드에 접속할 수 있는 권한 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            
                .allowedOriginPatterns("*") 
                // 모든 HTTP 메서드(데이터 주고받기, 수정, 삭제)를 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 쿠키나 인증 헤더를 주고받을 수 있도록 허용
                .allowCredentials(true);
    }
}

package com.sm_four_idiot.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI 설정 클래스
 * - API 문서 자동화
 * - JWT 인증 헤더 설정
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정
     * - API 기본 정보
     * - JWT Bearer 인증 스키마 등록
     */
    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("TOEST API")
                        .description("TOEST (Toeic + Quest) 백엔드 API 문서")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
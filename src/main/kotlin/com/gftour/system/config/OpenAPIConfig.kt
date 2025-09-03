package com.gftour.system.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAPIConfig {
    
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Good Feel Tour 여행사 업무 관리 시스템 API")
                    .description("""
                        Good Feel Tour 여행사 업무 관리 시스템의 REST API 문서입니다.
                        
                        ## 주요 기능
                        - **인증**: JWT 기반 사용자 인증 및 권한 관리
                        - **파일 관리**: 신규등록, 검색, CRUD 작업 (REF No 자동 생성)
                        - **문서 관리**: 10개 문서 타입 관리 (견적서→수배서→청구서 워크플로우)
                        - **고객 관리**: 고객명단 관리 (이름, 나이, 여권번호)
                        - **AGT 관리**: AGT 정보 등록 및 관리
                        - **정산 관리**: 정산보고서, 수수료 계산, 영수증 출력
                        - **가이드 관리**: 가이드 지침서, 단체행동계획서
                        
                        ## 워크플로우
                        문서 승인 순서: 견적서(1) → 수배서(2) → 청구서(3) → 고객확인(4) → 가이드지침서(5)
                        
                        ## 자동 계산
                        - REF No: GF + YYYYMMDD + 3자리 순번
                        - Sub Total: 판매송금합계 - 수취송금합계 - 운용비용합계
                    """.trimIndent())
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("GFTour Development Team")
                            .email("support@gftour.com")
                    )
            )
            .addSecurityItem(
                SecurityRequirement()
                    .addList("Bearer Authentication")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "Bearer Authentication",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("JWT 토큰을 입력하세요 (Bearer 접두사 제외)")
                    )
            )
    }
}
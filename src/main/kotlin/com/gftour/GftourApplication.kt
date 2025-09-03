package com.gftour

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Good Feel Tour 여행사 업무 관리 시스템 메인 애플리케이션
 * 
 * 시스템 기능:
 * - File 검색, 신규등록, AGT 관리 
 * - 고객명단, 단체행동지침서, 정산보고서
 * - 문서 워크플로우 (견적서→수배서→청구서→관광확인→가이드지침서)
 */
@SpringBootApplication
class GftourApplication

fun main(args: Array<String>) {
    runApplication<GftourApplication>(*args)
}
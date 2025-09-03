package com.gftour.repository

import com.gftour.domain.User
import com.gftour.domain.UserStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

/**
 * 사용자 데이터 접근 인터페이스
 */
interface UserRepository : JpaRepository<User, Long> {
    
    /**
     * 이메일로 사용자 조회
     */
    fun findByEmail(email: String): Optional<User>
    
    /**
     * 이메일 존재 여부 확인
     */
    fun existsByEmail(email: String): Boolean
    
    /**
     * 사용자 상태별 조회
     */
    fun findByStatus(status: UserStatus, pageable: Pageable): Page<User>
    
    /**
     * 이름으로 사용자 검색 (부분 매칭)
     */
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name% AND u.status = :status")
    fun findByNameContainingAndStatus(
        @Param("name") name: String, 
        @Param("status") status: UserStatus, 
        pageable: Pageable
    ): Page<User>
    
    /**
     * 활성 사용자 수 조회
     */
    fun countByStatus(status: UserStatus): Long
}
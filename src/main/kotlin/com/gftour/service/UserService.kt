package com.gftour.service

import com.gftour.domain.User
import com.gftour.domain.UserRole
import com.gftour.domain.UserStatus
import com.gftour.exception.*
import com.gftour.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 서비스
 * 사용자 등록, 수정, 삭제, 조회 및 인증 관련 비즈니스 로직을 처리
 */
@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    /**
     * 사용자 등록
     * 
     * @param email 이메일 (중복 검사 수행)
     * @param name 이름
     * @param password 비밀번호 (암호화됨)
     * @param phone 전화번호 (선택사항)
     * @return 등록된 사용자 정보
     * @throws DuplicateEmailException 이메일이 이미 존재하는 경우
     */
    fun registerUser(
        email: String,
        name: String,
        password: String,
        phone: String? = null
    ): User {
        logger.info("사용자 등록 시도: email={}", email)
        
        // 이메일 중복 검사
        if (userRepository.existsByEmail(email)) {
            logger.warn("이메일 중복 등록 시도: {}", email)
            throw DuplicateEmailException("이미 등록된 이메일입니다: $email")
        }
        
        // 비밀번호 암호화
        val encodedPassword = passwordEncoder.encode(password)
        
        val user = User(
            email = email,
            name = name,
            password = encodedPassword,
            phone = phone,
            status = UserStatus.ACTIVE,
            role = UserRole.USER
        )
        
        val savedUser = userRepository.save(user)
        logger.info("사용자 등록 완료: id={}, email={}", savedUser.id, savedUser.email)
        
        return savedUser
    }

    /**
     * 사용자 인증
     * 
     * @param email 이메일
     * @param password 비밀번호
     * @return 인증된 사용자 정보
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws InvalidPasswordException 비밀번호가 일치하지 않는 경우
     * @throws UserNotActiveException 비활성화된 사용자인 경우
     */
    @Transactional(readOnly = true)
    fun authenticateUser(email: String, password: String): User {
        logger.info("사용자 인증 시도: email={}", email)
        
        val user = userRepository.findByEmail(email)
            .orElseThrow { UserNotFoundException("사용자를 찾을 수 없습니다: $email") }
        
        if (!passwordEncoder.matches(password, user.password)) {
            logger.warn("잘못된 비밀번호 시도: email={}", email)
            throw InvalidPasswordException("잘못된 비밀번호입니다")
        }
        
        if (!user.isActive()) {
            logger.warn("비활성 사용자 로그인 시도: email={}, status={}", email, user.status)
            throw UserNotActiveException("비활성화된 사용자입니다")
        }
        
        logger.info("사용자 인증 성공: id={}, email={}", user.id, user.email)
        return user
    }

    /**
     * 사용자 조회 (ID)
     */
    @Transactional(readOnly = true)
    fun getUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { UserNotFoundException("사용자를 찾을 수 없습니다: ID $id") }
    }

    /**
     * 사용자 조회 (이메일)
     */
    @Transactional(readOnly = true)
    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow { UserNotFoundException("사용자를 찾을 수 없습니다: $email") }
    }

    /**
     * 모든 사용자 조회 (페이징)
     */
    @Transactional(readOnly = true)
    fun getAllUsers(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }

    /**
     * 상태별 사용자 조회
     */
    @Transactional(readOnly = true)
    fun getUsersByStatus(status: UserStatus, pageable: Pageable): Page<User> {
        return userRepository.findByStatus(status, pageable)
    }

    /**
     * 이름으로 사용자 검색
     */
    @Transactional(readOnly = true)
    fun searchUsersByName(name: String, pageable: Pageable): Page<User> {
        return userRepository.findByNameContainingAndStatus(name, UserStatus.ACTIVE, pageable)
    }

    /**
     * 비밀번호 변경
     * 
     * @param userId 사용자 ID
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws InvalidPasswordException 현재 비밀번호가 일치하지 않는 경우
     */
    fun changePassword(userId: Long, currentPassword: String, newPassword: String): User {
        logger.info("비밀번호 변경 시도: userId={}", userId)
        
        val user = getUserById(userId)
        
        if (!passwordEncoder.matches(currentPassword, user.password)) {
            logger.warn("잘못된 현재 비밀번호: userId={}", userId)
            throw InvalidPasswordException("현재 비밀번호가 일치하지 않습니다")
        }
        
        val encodedNewPassword = passwordEncoder.encode(newPassword)
        user.changePassword(encodedNewPassword)
        
        val updatedUser = userRepository.save(user)
        logger.info("비밀번호 변경 완료: userId={}", userId)
        
        return updatedUser
    }

    /**
     * 사용자 상태 변경
     * 
     * @param userId 사용자 ID
     * @param newStatus 새로운 상태
     * @param adminUserId 관리자 사용자 ID (권한 검사용)
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws UnauthorizedException 관리자 권한이 없는 경우
     */
    fun changeUserStatus(userId: Long, newStatus: UserStatus, adminUserId: Long): User {
        logger.info("사용자 상태 변경 시도: userId={}, newStatus={}, adminUserId={}", 
                   userId, newStatus, adminUserId)
        
        // 관리자 권한 검사
        val admin = getUserById(adminUserId)
        if (!admin.isAdmin()) {
            logger.warn("관리자 권한 없음: adminUserId={}", adminUserId)
            throw UnauthorizedException("관리자 권한이 필요합니다")
        }
        
        val user = getUserById(userId)
        user.changeStatus(newStatus)
        
        val updatedUser = userRepository.save(user)
        logger.info("사용자 상태 변경 완료: userId={}, newStatus={}", userId, newStatus)
        
        return updatedUser
    }

    /**
     * 사용자 삭제 (논리 삭제 - 비활성화)
     * 
     * @param userId 사용자 ID
     * @param adminUserId 관리자 사용자 ID (권한 검사용)
     */
    fun deleteUser(userId: Long, adminUserId: Long): User {
        logger.info("사용자 삭제 시도: userId={}, adminUserId={}", userId, adminUserId)
        
        return changeUserStatus(userId, UserStatus.INACTIVE, adminUserId)
    }

    /**
     * 이메일 중복 검사
     */
    @Transactional(readOnly = true)
    fun isEmailExists(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    /**
     * 활성 사용자 수 조회
     */
    @Transactional(readOnly = true)
    fun getActiveUserCount(): Long {
        return userRepository.countByStatus(UserStatus.ACTIVE)
    }
}
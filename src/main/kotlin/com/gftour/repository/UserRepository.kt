package com.gftour.repository

import com.gftour.entity.User
import com.gftour.entity.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    
    fun findByEmailAndIsActive(email: String, isActive: Boolean): User?
    
    fun findByEmail(email: String): User?
    
    fun findByRole(role: UserRole): List<User>
    
    fun findByIsActive(isActive: Boolean): List<User>
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    fun findActiveUsersByRole(@Param("role") role: UserRole): List<User>
    
    @Query("SELECT u FROM User u WHERE u.fullName ILIKE %:name% AND u.isActive = true")
    fun searchByName(@Param("name") name: String): List<User>
    
    @Query("SELECT u FROM User u WHERE u.department = :department AND u.isActive = true")
    fun findByDepartment(@Param("department") department: String): List<User>
    
    @Query("SELECT u FROM User u WHERE u.territory = :territory AND u.role = 'AGENT' AND u.isActive = true")
    fun findAgentsByTerritory(@Param("territory") territory: String): List<User>
}
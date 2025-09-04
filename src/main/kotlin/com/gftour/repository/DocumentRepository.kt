package com.gftour.repository

import com.gftour.entity.Document
import com.gftour.entity.DocumentStatus
import com.gftour.entity.DocumentType
import com.gftour.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface DocumentRepository : JpaRepository<Document, Long> {
    
    fun findByCreatedBy(user: User, pageable: Pageable): Page<Document>
    
    fun findByAssignedTo(user: User, pageable: Pageable): Page<Document>
    
    fun findByStatus(status: DocumentStatus, pageable: Pageable): Page<Document>
    
    fun findByType(type: DocumentType, pageable: Pageable): Page<Document>
    
    @Query("SELECT d FROM Document d WHERE d.title ILIKE %:keyword% OR d.content ILIKE %:keyword%")
    fun searchByKeyword(@Param("keyword") keyword: String, pageable: Pageable): Page<Document>
    
    @Query("SELECT d FROM Document d WHERE d.customerName ILIKE %:customerName%")
    fun findByCustomerName(@Param("customerName") customerName: String, pageable: Pageable): Page<Document>
    
    @Query("SELECT d FROM Document d WHERE d.tourCode = :tourCode")
    fun findByTourCode(@Param("tourCode") tourCode: String): List<Document>
    
    @Query("SELECT d FROM Document d WHERE d.status = 'PENDING_REVIEW'")
    fun findPendingApproval(): List<Document>
    
    @Query("SELECT d FROM Document d WHERE d.createdAt BETWEEN :startDate AND :endDate")
    fun findByDateRange(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime,
        pageable: Pageable
    ): Page<Document>
    
    @Query("""
        SELECT d FROM Document d 
        WHERE (:status IS NULL OR d.status = :status)
        AND (:type IS NULL OR d.type = :type)
        AND (:assignedTo IS NULL OR d.assignedTo = :assignedTo)
        AND (:createdBy IS NULL OR d.createdBy = :createdBy)
    """)
    fun findWithFilters(
        @Param("status") status: DocumentStatus?,
        @Param("type") type: DocumentType?,
        @Param("assignedTo") assignedTo: User?,
        @Param("createdBy") createdBy: User?,
        pageable: Pageable
    ): Page<Document>
}
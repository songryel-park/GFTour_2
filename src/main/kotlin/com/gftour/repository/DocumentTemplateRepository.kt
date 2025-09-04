package com.gftour.repository

import com.gftour.entity.DocumentTemplate
import com.gftour.entity.DocumentType
import com.gftour.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DocumentTemplateRepository : JpaRepository<DocumentTemplate, Long> {
    
    fun findByDocumentType(documentType: DocumentType): List<DocumentTemplate>
    
    fun findByIsActive(isActive: Boolean): List<DocumentTemplate>
    
    fun findByDocumentTypeAndIsActive(documentType: DocumentType, isActive: Boolean): List<DocumentTemplate>
    
    fun findByCreatedBy(user: User): List<DocumentTemplate>
    
    @Query("SELECT dt FROM DocumentTemplate dt WHERE dt.name ILIKE %:name% AND dt.isActive = true")
    fun searchByName(@Param("name") name: String): List<DocumentTemplate>
}
package com.gftour.service

import com.gftour.entity.*
import com.gftour.repository.DocumentRepository
import com.gftour.repository.DocumentTemplateRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class DocumentService(
    private val documentRepository: DocumentRepository,
    private val documentTemplateRepository: DocumentTemplateRepository
) {

    fun getTotalDocuments(): Long {
        return documentRepository.count()
    }

    fun getPendingApprovalsCount(): Long {
        return documentRepository.findPendingApproval().size.toLong()
    }

    fun getRecentDocuments(limit: Int): List<Document> {
        val pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending())
        return documentRepository.findAll(pageable).content
    }

    fun getAllDocuments(): List<Document> {
        return documentRepository.findAll(Sort.by("createdAt").descending())
    }

    fun createDocument(
        title: String,
        content: String,
        type: DocumentType,
        createdBy: User,
        assignedTo: User? = null,
        customerName: String? = null,
        customerEmail: String? = null,
        customerPhone: String? = null,
        totalAmount: BigDecimal? = null,
        tourCode: String? = null,
        destination: String? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        numberOfPassengers: Int? = null
    ): Document {
        val document = Document(
            title = title,
            content = content,
            type = type,
            createdBy = createdBy,
            assignedTo = assignedTo,
            customerName = customerName,
            customerEmail = customerEmail,
            customerPhone = customerPhone,
            totalAmount = totalAmount,
            tourCode = tourCode,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            numberOfPassengers = numberOfPassengers
        )
        return documentRepository.save(document)
    }

    fun updateDocumentStatus(documentId: Long, status: DocumentStatus, approvedBy: User? = null): Document? {
        val document = documentRepository.findById(documentId).orElse(null)
        return document?.let {
            val updatedDocument = document.copy(
                status = status,
                updatedAt = LocalDateTime.now(),
                approvedAt = if (status == DocumentStatus.APPROVED) LocalDateTime.now() else null,
                approvedBy = if (status == DocumentStatus.APPROVED) approvedBy else null
            )
            documentRepository.save(updatedDocument)
        }
    }

    fun searchDocuments(keyword: String, pageable: Pageable): Page<Document> {
        return documentRepository.searchByKeyword(keyword, pageable)
    }

    fun getDocumentsByUser(user: User, pageable: Pageable): Page<Document> {
        return documentRepository.findByCreatedBy(user, pageable)
    }

    fun getAssignedDocuments(user: User, pageable: Pageable): Page<Document> {
        return documentRepository.findByAssignedTo(user, pageable)
    }

    fun getDocumentsByStatus(status: DocumentStatus, pageable: Pageable): Page<Document> {
        return documentRepository.findByStatus(status, pageable)
    }

    fun getDocumentsByType(type: DocumentType, pageable: Pageable): Page<Document> {
        return documentRepository.findByType(type, pageable)
    }

    // Template methods
    fun getAllTemplates(): List<DocumentTemplate> {
        return documentTemplateRepository.findByIsActive(true)
    }

    fun getTemplatesByType(type: DocumentType): List<DocumentTemplate> {
        return documentTemplateRepository.findByDocumentTypeAndIsActive(type, true)
    }

    fun createTemplate(
        name: String,
        templateContent: String,
        documentType: DocumentType,
        createdBy: User,
        description: String? = null,
        variables: Map<String, String> = emptyMap()
    ): DocumentTemplate {
        val template = DocumentTemplate(
            name = name,
            templateContent = templateContent,
            documentType = documentType,
            createdBy = createdBy,
            description = description,
            variables = variables
        )
        return documentTemplateRepository.save(template)
    }

    fun createDocumentFromTemplate(templateId: Long, variables: Map<String, String>, createdBy: User): Document? {
        val template = documentTemplateRepository.findById(templateId).orElse(null)
        return template?.let {
            var content = template.templateContent
            variables.forEach { (key, value) ->
                content = content.replace("{{$key}}", value)
            }

            Document(
                title = variables["title"] ?: "Document from ${template.name}",
                content = content,
                type = template.documentType,
                createdBy = createdBy
            ).let { document ->
                documentRepository.save(document)
            }
        }
    }
}
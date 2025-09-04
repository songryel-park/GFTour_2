package com.gftour.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "document_templates")
data class DocumentTemplate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(columnDefinition = "TEXT")
    val templateContent: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val documentType: DocumentType,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    val createdBy: User,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val updatedAt: LocalDateTime? = null,
    
    @Column
    val description: String? = null,
    
    // Template variables for replacement
    @ElementCollection
    @CollectionTable(
        name = "template_variables",
        joinColumns = [JoinColumn(name = "template_id")]
    )
    @MapKeyColumn(name = "variable_name")
    @Column(name = "variable_description")
    val variables: Map<String, String> = emptyMap()
)
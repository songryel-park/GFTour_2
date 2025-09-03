package com.gftour.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * 가이드 지침서 엔티티
 * 단체행동계획서 및 가이드 정보 관리
 */
@Entity
@Table(name = "guide_instructions")
data class GuideInstruction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_record_id")
    val fileRecord: FileRecord,
    
    @NotBlank(message = "가이드명은 필수입니다")
    @Size(max = 100)
    val guideName: String,
    
    @Size(max = 20)
    val guidePhone: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val travelSchedule: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val safetyRules: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val precautions: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val emergencyContact: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val specialInstructions: String? = null,
    
    @Enumerated(EnumType.STRING)
    val status: GuideInstructionStatus = GuideInstructionStatus.DRAFT,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    val createdBy: User,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class GuideInstructionStatus {
    DRAFT,      // 작성중
    FINALIZED,  // 확정
    DISTRIBUTED // 배포됨
}
package com.gftour.system.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "guides")
data class Guide(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_record_id", nullable = false)
    val fileRecord: FileRecord? = null,
    
    @Column(name = "guide_name", nullable = false, length = 100)
    val guideName: String = "",
    
    @Column(name = "guide_phone", length = 20)
    val guidePhone: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val schedule: String? = null,
    
    @Column(name = "safety_rules", columnDefinition = "TEXT")
    val safetyRules: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val precautions: String? = null,
    
    @Column(name = "group_action_plan", columnDefinition = "TEXT")
    val groupActionPlan: String? = null,
    
    @Column(name = "meeting_point", length = 200)
    val meetingPoint: String? = null,
    
    @Column(name = "emergency_contact", length = 20)
    val emergencyContact: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: GuideStatus = GuideStatus.DRAFT,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class GuideStatus {
    DRAFT,      // 초안
    PREPARED,   // 준비됨
    APPROVED,   // 승인됨
    COMPLETED   // 완료됨
}
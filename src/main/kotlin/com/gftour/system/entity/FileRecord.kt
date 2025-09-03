package com.gftour.system.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Entity
@Table(name = "file_records")
data class FileRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    
    @Column(unique = true, nullable = false, length = 20)
    val refNo: String = "",
    
    @NotBlank(message = "여행지는 필수입니다")
    @Column(nullable = false, length = 100)
    val destination: String = "",
    
    @NotBlank(message = "담당자는 필수입니다")
    @Column(nullable = false, length = 50)
    val manager: String = "",
    
    @Column(length = 100)
    val agency: String? = null,
    
    @Column(name = "travel_start_date")
    val travelStartDate: LocalDateTime? = null,
    
    @Column(name = "travel_end_date")
    val travelEndDate: LocalDateTime? = null,
    
    @Column(name = "customer_count")
    val customerCount: Int = 0,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: FileStatus = FileStatus.DRAFT,
    
    @Column(columnDefinition = "TEXT")
    val notes: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    val createdBy: User? = null,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @OneToMany(mappedBy = "fileRecord", cascade = [CascadeType.ALL], orphanRemoval = true)
    val documents: MutableList<Document> = mutableListOf(),
    
    @OneToMany(mappedBy = "fileRecord", cascade = [CascadeType.ALL], orphanRemoval = true)
    val customers: MutableList<Customer> = mutableListOf()
)

enum class FileStatus {
    DRAFT,      // 신규
    IN_PROGRESS, // 진행중
    COMPLETED,   // 완료
    CANCELLED    // 취소
}
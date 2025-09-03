package com.gftour.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Pattern
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 파일 레코드 엔티티
 * 신규등록 데이터 관리 (REF No, File CODE, 고객정보, 여행일정)
 */
@Entity
@Table(name = "file_records")
data class FileRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @NotBlank(message = "REF No는 필수입니다")
    @Size(max = 50)
    @Column(unique = true)
    val refNo: String,
    
    @NotBlank(message = "File CODE는 필수입니다")
    @Size(max = 20)
    val fileCode: String,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    val customer: Customer,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id") 
    val tour: Tour,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agt_id")
    val agt: AGT,
    
    val paxCount: Int = 1,
    
    val departureDate: LocalDate,
    val returnDate: LocalDate,
    
    @Enumerated(EnumType.STRING)
    val status: FileStatus = FileStatus.NEW,
    
    @Size(max = 1000)
    val remarks: String? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class FileStatus {
    NEW, IN_PROGRESS, COMPLETED, CANCELLED
}
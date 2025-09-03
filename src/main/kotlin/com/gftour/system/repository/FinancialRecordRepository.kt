package com.gftour.system.repository

import com.gftour.system.entity.FinancialRecord
import com.gftour.system.entity.FileRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface FinancialRecordRepository : JpaRepository<FinancialRecord, Long> {
    fun findByFileRecord(fileRecord: FileRecord): FinancialRecord?
    fun findByFileRecordId(fileRecordId: Long): FinancialRecord?
    
    @Query("SELECT f FROM FinancialRecord f WHERE " +
           "f.createdAt BETWEEN :startDate AND :endDate")
    fun findByDateRange(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<FinancialRecord>
}
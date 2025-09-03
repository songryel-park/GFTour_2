package com.gftour.system.repository

import com.gftour.system.entity.FileRecord
import com.gftour.system.entity.FileStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FileRecordRepository : JpaRepository<FileRecord, Long> {
    fun findByRefNo(refNo: String): FileRecord?
    
    fun findByStatus(status: FileStatus): List<FileRecord>
    
    @Query("SELECT f FROM FileRecord f WHERE " +
           "(:refNo IS NULL OR f.refNo LIKE %:refNo%) AND " +
           "(:destination IS NULL OR f.destination LIKE %:destination%) AND " +
           "(:manager IS NULL OR f.manager LIKE %:manager%) AND " +
           "(:status IS NULL OR f.status = :status)")
    fun searchFiles(
        @Param("refNo") refNo: String?,
        @Param("destination") destination: String?,
        @Param("manager") manager: String?,
        @Param("status") status: FileStatus?,
        pageable: Pageable
    ): Page<FileRecord>
    
    fun existsByRefNo(refNo: String): Boolean
}
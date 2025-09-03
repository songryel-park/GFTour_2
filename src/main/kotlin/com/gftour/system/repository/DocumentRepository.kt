package com.gftour.system.repository

import com.gftour.system.entity.Document
import com.gftour.system.entity.DocumentType
import com.gftour.system.entity.FileRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocumentRepository : JpaRepository<Document, Long> {
    fun findByFileRecordAndType(fileRecord: FileRecord, type: DocumentType): Document?
    fun findByFileRecord(fileRecord: FileRecord): List<Document>
    fun findByFileRecordId(fileRecordId: Long): List<Document>
}
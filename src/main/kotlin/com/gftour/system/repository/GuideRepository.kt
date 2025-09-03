package com.gftour.system.repository

import com.gftour.system.entity.Guide
import com.gftour.system.entity.FileRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GuideRepository : JpaRepository<Guide, Long> {
    fun findByFileRecord(fileRecord: FileRecord): Guide?
    fun findByFileRecordId(fileRecordId: Long): Guide?
    fun findByGuideName(guideName: String): List<Guide>
}
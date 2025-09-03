package com.gftour.system.repository

import com.gftour.system.entity.AGT
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AGTRepository : JpaRepository<AGT, Long> {
    fun findByName(name: String): AGT?

    @Query("SELECT a FROM AGT a WHERE " +
           "(:name IS NULL OR a.name LIKE %:name%) AND " +
           "(:region IS NULL OR a.region LIKE %:region%) AND " +
           "(:country IS NULL OR a.country LIKE %:country%)")
    fun searchAGTs(
        @Param("name") name: String?,
        @Param("region") region: String?,
        @Param("country") country: String?
    ): List<AGT>
}
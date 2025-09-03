package com.gftour.system.repository

import com.gftour.system.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {
    fun findByPassportNumber(passportNumber: String): Customer?
    fun findByFileRecordId(fileRecordId: Long): List<Customer>
    
    @Query("SELECT c FROM Customer c WHERE " +
           "(:name IS NULL OR c.name LIKE %:name%) AND " +
           "(:passportNumber IS NULL OR c.passportNumber LIKE %:passportNumber%)")
    fun searchCustomers(
        @Param("name") name: String?,
        @Param("passportNumber") passportNumber: String?
    ): List<Customer>
}
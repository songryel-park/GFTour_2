package com.gftour.repository

import com.gftour.entity.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun findByActiveTrue(): List<User>
}

@Repository
interface TourRepository : JpaRepository<Tour, Long> {
    fun findByStatus(status: TourStatus): List<Tour>
    fun findByStartDateBetween(startDate: LocalDate, endDate: LocalDate): List<Tour>
    fun findByDestinationContainingIgnoreCase(destination: String): List<Tour>
}

@Repository
interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByCustomer(customer: Customer): List<Booking>
    fun findByTour(tour: Tour): List<Booking>
    fun findByStatus(status: BookingStatus): List<Booking>
}

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<Customer>
    fun findByPhone(phone: String): Customer?
    fun findByEmail(email: String): Customer?
    fun findByCustomerType(customerType: CustomerType): List<Customer>
}

@Repository
interface FileRecordRepository : JpaRepository<FileRecord, Long> {
    fun findByRefNo(refNo: String): FileRecord?
    fun findByFileCode(fileCode: String): List<FileRecord>
    fun findByCustomer(customer: Customer): List<FileRecord>
    fun findByAgt(agt: AGT): List<FileRecord>
    fun findByStatus(status: FileStatus): List<FileRecord>
    fun findByDepartureDateBetween(startDate: LocalDate, endDate: LocalDate): List<FileRecord>
    
    @Query("SELECT fr FROM FileRecord fr WHERE " +
           "(:refNo IS NULL OR fr.refNo LIKE %:refNo%) AND " +
           "(:customerName IS NULL OR fr.customer.name LIKE %:customerName%) AND " +
           "(:status IS NULL OR fr.status = :status)")
    fun searchFileRecords(
        @Param("refNo") refNo: String?,
        @Param("customerName") customerName: String?,
        @Param("status") status: FileStatus?,
        pageable: Pageable
    ): Page<FileRecord>
}

@Repository
interface AGTRepository : JpaRepository<AGT, Long> {
    fun findByAgtCode(agtCode: String): AGT?
    fun findByNameContainingIgnoreCase(name: String): List<AGT>
    fun findByStatus(status: AGTStatus): List<AGT>
    fun findByContactPerson(contactPerson: String): List<AGT>
}

@Repository
interface DocumentRepository : JpaRepository<Document, Long> {
    fun findByFileRecord(fileRecord: FileRecord): List<Document>
    fun findByDocumentType(documentType: DocumentType): List<Document>
    fun findByStatus(status: DocumentStatus): List<Document>
    fun findByCreatedBy(user: User): List<Document>
    fun findByFileRecordAndDocumentType(fileRecord: FileRecord, documentType: DocumentType): Document?
    
    @Query("SELECT d FROM Document d WHERE d.fileRecord = :fileRecord ORDER BY " +
           "CASE d.documentType " +
           "WHEN 'QUOTATION' THEN 1 " +
           "WHEN 'ALLOCATION' THEN 2 " +
           "WHEN 'INVOICE' THEN 3 " +
           "WHEN 'TOUR_CONFIRMATION' THEN 4 " +
           "WHEN 'GUIDE_INSTRUCTION' THEN 5 " +
           "END")
    fun findByFileRecordOrderByWorkflow(fileRecord: FileRecord): List<Document>
}

@Repository
interface FinancialRecordRepository : JpaRepository<FinancialRecord, Long> {
    fun findByFileRecord(fileRecord: FileRecord): FinancialRecord?
    fun findByPaymentStatus(status: PaymentStatus): List<FinancialRecord>
    
    @Query("SELECT SUM(fr.subTotal) FROM FinancialRecord fr WHERE fr.paymentStatus = :status")
    fun sumSubTotalByPaymentStatus(@Param("status") status: PaymentStatus): Optional<java.math.BigDecimal>
    
    @Query("SELECT SUM(fr.unpaidAmount) FROM FinancialRecord fr WHERE fr.paymentStatus IN ('PENDING', 'PARTIAL')")
    fun getTotalUnpaidAmount(): Optional<java.math.BigDecimal>
}

@Repository
interface GuideInstructionRepository : JpaRepository<GuideInstruction, Long> {
    fun findByFileRecord(fileRecord: FileRecord): GuideInstruction?
    fun findByGuideName(guideName: String): List<GuideInstruction>
    fun findByStatus(status: GuideInstructionStatus): List<GuideInstruction>
    fun findByCreatedBy(user: User): List<GuideInstruction>
}
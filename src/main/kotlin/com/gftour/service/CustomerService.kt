package com.gftour.service

import com.gftour.entity.*
import com.gftour.repository.CustomerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 고객 서비스
 * 고객 정보 관리 및 여행 이력 관리
 */
@Service
@Transactional
class CustomerService(
    private val customerRepository: CustomerRepository
) {
    private val logger = LoggerFactory.getLogger(CustomerService::class.java)
    
    /**
     * 새로운 고객 등록
     */
    fun createCustomer(
        name: String,
        phone: String? = null,
        email: String? = null,
        address: String? = null,
        birthDate: LocalDate? = null,
        customerType: CustomerType = CustomerType.REGULAR,
        notes: String? = null
    ): Customer {
        logger.info("고객 등록 시작 - 이름: $name")
        
        // 전화번호 중복 검사 (전화번호가 있는 경우)
        phone?.let { phoneNumber ->
            if (customerRepository.findByPhone(phoneNumber) != null) {
                throw IllegalArgumentException("이미 등록된 전화번호입니다: $phoneNumber")
            }
        }
        
        // 이메일 중복 검사 (이메일이 있는 경우)
        email?.let { emailAddress ->
            if (customerRepository.findByEmail(emailAddress) != null) {
                throw IllegalArgumentException("이미 등록된 이메일입니다: $emailAddress")
            }
        }
        
        val customer = Customer(
            name = name,
            phone = phone,
            email = email,
            address = address,
            birthDate = birthDate,
            customerType = customerType,
            notes = notes
        )
        
        val saved = customerRepository.save(customer)
        logger.info("고객 등록 완료 - ID: ${saved.id}, 이름: ${saved.name}")
        
        return saved
    }
    
    /**
     * 고객 정보 수정
     */
    fun updateCustomer(
        id: Long,
        name: String? = null,
        phone: String? = null,
        email: String? = null,
        address: String? = null,
        birthDate: LocalDate? = null,
        customerType: CustomerType? = null,
        notes: String? = null
    ): Customer {
        val existing = customerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("고객을 찾을 수 없습니다: $id") }
        
        // 전화번호 중복 검사 (변경하려는 경우)
        phone?.let { phoneNumber ->
            if (phoneNumber != existing.phone) {
                val existingCustomer = customerRepository.findByPhone(phoneNumber)
                if (existingCustomer != null && existingCustomer.id != id) {
                    throw IllegalArgumentException("이미 등록된 전화번호입니다: $phoneNumber")
                }
            }
        }
        
        // 이메일 중복 검사 (변경하려는 경우)
        email?.let { emailAddress ->
            if (emailAddress != existing.email) {
                val existingCustomer = customerRepository.findByEmail(emailAddress)
                if (existingCustomer != null && existingCustomer.id != id) {
                    throw IllegalArgumentException("이미 등록된 이메일입니다: $emailAddress")
                }
            }
        }
        
        val updated = existing.copy(
            name = name ?: existing.name,
            phone = phone ?: existing.phone,
            email = email ?: existing.email,
            address = address ?: existing.address,
            birthDate = birthDate ?: existing.birthDate,
            customerType = customerType ?: existing.customerType,
            notes = notes ?: existing.notes,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("고객 정보 수정 완료 - ID: ${updated.id}, 이름: ${updated.name}")
        return customerRepository.save(updated)
    }
    
    /**
     * 고객 조회 by ID
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): Customer? {
        return customerRepository.findById(id).orElse(null)
    }
    
    /**
     * 고객 이름으로 검색
     */
    @Transactional(readOnly = true)
    fun searchByName(name: String): List<Customer> {
        return customerRepository.findByNameContainingIgnoreCase(name)
    }
    
    /**
     * 전화번호로 고객 조회
     */
    @Transactional(readOnly = true)
    fun findByPhone(phone: String): Customer? {
        return customerRepository.findByPhone(phone)
    }
    
    /**
     * 이메일로 고객 조회
     */
    @Transactional(readOnly = true)
    fun findByEmail(email: String): Customer? {
        return customerRepository.findByEmail(email)
    }
    
    /**
     * 고객 타입별 조회
     */
    @Transactional(readOnly = true)
    fun findByCustomerType(customerType: CustomerType): List<Customer> {
        return customerRepository.findByCustomerType(customerType)
    }
    
    /**
     * VIP 고객 목록 조회
     */
    @Transactional(readOnly = true)
    fun getVipCustomers(): List<Customer> {
        return customerRepository.findByCustomerType(CustomerType.VIP)
    }
    
    /**
     * 기업 고객 목록 조회
     */
    @Transactional(readOnly = true)
    fun getCorporateCustomers(): List<Customer> {
        return customerRepository.findByCustomerType(CustomerType.CORPORATE)
    }
    
    /**
     * 전체 고객 목록 조회
     */
    @Transactional(readOnly = true)
    fun getAllCustomers(): List<Customer> {
        return customerRepository.findAll()
    }
    
    /**
     * 고객을 VIP로 승급
     */
    fun promoteToVip(id: Long): Customer {
        val existing = customerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("고객을 찾을 수 없습니다: $id") }
        
        if (existing.customerType == CustomerType.VIP) {
            throw IllegalStateException("이미 VIP 고객입니다")
        }
        
        val updated = existing.copy(
            customerType = CustomerType.VIP,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("고객 VIP 승급 - ID: ${updated.id}, 이름: ${updated.name}")
        return customerRepository.save(updated)
    }
    
    /**
     * 고객 삭제
     */
    fun deleteCustomer(id: Long) {
        val customer = customerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("고객을 찾을 수 없습니다: $id") }
        
        // TODO: 여행 이력이 있는 고객은 삭제하지 않고 비활성화 처리하는 것을 고려
        
        logger.info("고객 삭제 - ID: ${customer.id}, 이름: ${customer.name}")
        customerRepository.delete(customer)
    }
    
    /**
     * 생일인 고객 목록 조회 (오늘 생일)
     */
    @Transactional(readOnly = true)
    fun getTodayBirthdayCustomers(): List<Customer> {
        val today = LocalDate.now()
        return customerRepository.findAll().filter { customer ->
            customer.birthDate?.let { birthDate ->
                birthDate.monthValue == today.monthValue && birthDate.dayOfMonth == today.dayOfMonth
            } == true
        }
    }
    
    /**
     * 특정 월의 생일인 고객 목록 조회
     */
    @Transactional(readOnly = true)
    fun getBirthdayCustomersByMonth(month: Int): List<Customer> {
        if (month < 1 || month > 12) {
            throw IllegalArgumentException("월은 1-12 사이여야 합니다: $month")
        }
        
        return customerRepository.findAll().filter { customer ->
            customer.birthDate?.monthValue == month
        }
    }
    
    /**
     * 전화번호 중복 검사
     */
    @Transactional(readOnly = true)
    fun isPhoneExists(phone: String): Boolean {
        return customerRepository.findByPhone(phone) != null
    }
    
    /**
     * 이메일 중복 검사
     */
    @Transactional(readOnly = true)
    fun isEmailExists(email: String): Boolean {
        return customerRepository.findByEmail(email) != null
    }
}
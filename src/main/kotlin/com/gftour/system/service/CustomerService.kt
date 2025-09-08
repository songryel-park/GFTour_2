package com.gftour.system.service

import com.gftour.system.dto.CustomerCreateRequest
import com.gftour.system.dto.CustomerDto
import com.gftour.system.dto.CustomerUpdateRequest
import com.gftour.system.entity.Customer
import com.gftour.system.repository.CustomerRepository
import com.gftour.system.repository.FileRecordRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val fileRecordRepository: FileRecordRepository
) {
    
    fun createCustomer(request: CustomerCreateRequest): CustomerDto {
        // Check if passport number already exists
        val existingCustomer = customerRepository.findByPassportNumber(request.passportNumber)
        if (existingCustomer != null) {
            throw IllegalArgumentException("이미 등록된 여권번호입니다")
        }
        
        // Validate file record if provided
        val fileRecord = request.fileRecordId?.let { fileRecordId ->
            fileRecordRepository.findById(fileRecordId)
                .orElseThrow { IllegalArgumentException("파일 레코드를 찾을 수 없습니다") }
        }
        
        val customer = Customer(
            name = request.name,
            age = request.age,
            passportNumber = request.passportNumber,
            fileRecord = fileRecord
        )
        
        val savedCustomer = customerRepository.save(customer)
        return toDto(savedCustomer)
    }
    
    fun updateCustomer(id: Long, request: CustomerUpdateRequest): CustomerDto {
        val customer = customerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("고객을 찾을 수 없습니다") }
        
        // Check if passport number is being changed and if it conflicts
        if (request.passportNumber != customer.passportNumber) {
            val existingCustomer = customerRepository.findByPassportNumber(request.passportNumber)
            if (existingCustomer != null && existingCustomer.id != id) {
                throw IllegalArgumentException("이미 등록된 여권번호입니다")
            }
        }
        
        val updatedCustomer = customer.copy(
            name = request.name,
            age = request.age,
            passportNumber = request.passportNumber,
            updatedAt = LocalDateTime.now()
        )
        
        val savedCustomer = customerRepository.save(updatedCustomer)
        return toDto(savedCustomer)
    }
    
    fun getCustomer(id: Long): CustomerDto {
        val customer = customerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("고객을 찾을 수 없습니다") }
        return toDto(customer)
    }
    
    fun getAllCustomers(): List<CustomerDto> {
        val customers = customerRepository.findAll()
        return customers.map { toDto(it) }
    }
    
    fun getCustomersByFileRecord(fileRecordId: Long): List<CustomerDto> {
        val customers = customerRepository.findByFileRecordId(fileRecordId)
        return customers.map { toDto(it) }
    }
    
    fun searchCustomers(name: String?, passportNumber: String?): List<CustomerDto> {
        val customers = customerRepository.searchCustomers(name, passportNumber)
        return customers.map { toDto(it) }
    }
    
    fun deleteCustomer(id: Long) {
        if (!customerRepository.existsById(id)) {
            throw IllegalArgumentException("고객을 찾을 수 없습니다")
        }
        customerRepository.deleteById(id)
    }
    
    fun getCustomerTravelHistory(id: Long): List<CustomerDto> {
        val customer = customerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("고객을 찾을 수 없습니다") }
        
        // Find all customers with the same passport number (travel history)
        val travelHistory = customerRepository.findByPassportNumber(customer.passportNumber)
        return if (travelHistory != null) listOf(toDto(travelHistory)) else emptyList()
    }
    
    fun getTotalCustomerCount(): Long {
        return customerRepository.count()
    }
    
    private fun toDto(customer: Customer): CustomerDto {
        return CustomerDto(
            id = customer.id,
            name = customer.name,
            age = customer.age,
            passportNumber = customer.passportNumber,
            fileRecordId = customer.fileRecord?.id
        )
    }
}
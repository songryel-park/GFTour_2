package com.gftour.service

import com.gftour.entity.*
import com.gftour.repository.UserRepository
import com.gftour.repository.DocumentRepository
import com.gftour.repository.DocumentTemplateRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Component
class DataInitializationService(
    private val userRepository: UserRepository,
    private val documentRepository: DocumentRepository,
    private val documentTemplateRepository: DocumentTemplateRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (userRepository.count() == 0L) {
            initializeData()
        }
    }

    private fun initializeData() {
        // Create users
        val admin = createUser(
            "admin@gftour.com", "System Administrator", "admin123", UserRole.ADMIN,
            phone = "+1-555-0100", department = "IT"
        )
        
        val manager = createUser(
            "manager@gftour.com", "Travel Manager", "manager123", UserRole.MANAGER,
            phone = "+1-555-0200", department = "Operations"
        )
        
        val agent1 = createUser(
            "agent@gftour.com", "John Smith", "agent123", UserRole.AGENT,
            phone = "+1-555-0301", department = "Sales", 
            commissionRate = 3.5, territory = "North America"
        )
        
        val agent2 = createUser(
            "agent2@gftour.com", "Sarah Johnson", "agent123", UserRole.AGENT,
            phone = "+1-555-0302", department = "Sales", 
            commissionRate = 4.0, territory = "Europe"
        )
        
        val viewer = createUser(
            "viewer@gftour.com", "Guest Viewer", "viewer123", UserRole.VIEWER,
            phone = "+1-555-0400", department = "Support"
        )

        // Create document templates
        createTourPackageTemplate(admin)
        createInvoiceTemplate(admin)
        createQuotationTemplate(admin)
        createBookingConfirmationTemplate(admin)
        createItineraryTemplate(admin)

        // Create sample documents
        createSampleDocuments(admin, manager, agent1, agent2)

        println("Demo data initialized successfully!")
        println("Login credentials:")
        println("Admin: admin@gftour.com / admin123")
        println("Manager: manager@gftour.com / manager123") 
        println("Agent: agent@gftour.com / agent123")
        println("Viewer: viewer@gftour.com / viewer123")
    }

    private fun createUser(
        email: String, fullName: String, password: String, role: UserRole,
        phone: String? = null, department: String? = null,
        commissionRate: Double? = null, territory: String? = null
    ): User {
        val user = User(
            email = email,
            fullName = fullName,
            password = passwordEncoder.encode(password),
            role = role,
            phone = phone,
            department = department,
            commissionRate = commissionRate,
            territory = territory
        )
        return userRepository.save(user)
    }

    private fun createTourPackageTemplate(createdBy: User) {
        val template = DocumentTemplate(
            name = "Standard Tour Package",
            templateContent = """
                <h2>{{packageTitle}}</h2>
                
                <h3>Tour Details</h3>
                <p><strong>Destination:</strong> {{destination}}</p>
                <p><strong>Duration:</strong> {{duration}}</p>
                <p><strong>Departure Date:</strong> {{departureDate}}</p>
                <p><strong>Return Date:</strong> {{returnDate}}</p>
                
                <h3>Package Includes</h3>
                <ul>
                    <li>Round-trip airfare</li>
                    <li>{{accommodationType}} accommodation</li>
                    <li>Daily breakfast</li>
                    <li>Local transportation</li>
                    <li>Professional tour guide</li>
                    <li>{{includedActivities}}</li>
                </ul>
                
                <h3>Pricing</h3>
                <p><strong>Package Price:</strong> ${'$'}{{packagePrice}} per person</p>
                <p><strong>Group Discount:</strong> {{groupDiscount}}</p>
                
                <h3>Terms and Conditions</h3>
                <p>{{termsAndConditions}}</p>
                
                <p><strong>Contact:</strong> {{agentName}} - {{agentEmail}} - {{agentPhone}}</p>
            """.trimIndent(),
            documentType = DocumentType.TOUR_PACKAGE,
            createdBy = createdBy,
            description = "Standard template for tour packages",
            variables = mapOf(
                "packageTitle" to "Tour package title",
                "destination" to "Travel destination",
                "duration" to "Tour duration",
                "departureDate" to "Departure date",
                "returnDate" to "Return date",
                "accommodationType" to "Type of accommodation",
                "includedActivities" to "Activities included in package",
                "packagePrice" to "Price per person",
                "groupDiscount" to "Group discount information",
                "termsAndConditions" to "Terms and conditions",
                "agentName" to "Agent name",
                "agentEmail" to "Agent email",
                "agentPhone" to "Agent phone"
            )
        )
        documentTemplateRepository.save(template)
    }

    private fun createInvoiceTemplate(createdBy: User) {
        val template = DocumentTemplate(
            name = "Travel Invoice",
            templateContent = """
                <div style="text-align: center;">
                    <h1>GFTour Travel Agency</h1>
                    <h2>INVOICE</h2>
                </div>
                
                <p><strong>Invoice #:</strong> {{invoiceNumber}}</p>
                <p><strong>Date:</strong> {{invoiceDate}}</p>
                
                <h3>Bill To:</h3>
                <p>{{customerName}}<br>
                {{customerAddress}}<br>
                {{customerPhone}}<br>
                {{customerEmail}}</p>
                
                <h3>Services</h3>
                <table border="1" style="width: 100%; border-collapse: collapse;">
                    <tr style="background-color: #f2f2f2;">
                        <th style="padding: 8px;">Description</th>
                        <th style="padding: 8px;">Quantity</th>
                        <th style="padding: 8px;">Unit Price</th>
                        <th style="padding: 8px;">Total</th>
                    </tr>
                    <tr>
                        <td style="padding: 8px;">{{serviceDescription}}</td>
                        <td style="padding: 8px;">{{quantity}}</td>
                        <td style="padding: 8px;">${'$'}{{unitPrice}}</td>
                        <td style="padding: 8px;">${'$'}{{totalAmount}}</td>
                    </tr>
                </table>
                
                <div style="text-align: right; margin-top: 20px;">
                    <p><strong>Subtotal: ${'$'}{{subtotal}}</strong></p>
                    <p><strong>Tax ({{taxRate}}%): ${'$'}{{taxAmount}}</strong></p>
                    <p style="font-size: 1.2em;"><strong>Total: ${'$'}{{grandTotal}}</strong></p>
                </div>
                
                <h3>Payment Information</h3>
                <p>Payment due within 30 days of invoice date.</p>
                <p>Payment methods: Cash, Check, Credit Card, Bank Transfer</p>
                
                <p><strong>Agent:</strong> {{agentName}} - {{agentEmail}}</p>
            """.trimIndent(),
            documentType = DocumentType.INVOICE,
            createdBy = createdBy,
            description = "Standard invoice template for travel services",
            variables = mapOf(
                "invoiceNumber" to "Invoice number",
                "invoiceDate" to "Invoice date",
                "customerName" to "Customer name",
                "customerAddress" to "Customer address",
                "customerPhone" to "Customer phone",
                "customerEmail" to "Customer email",
                "serviceDescription" to "Service description",
                "quantity" to "Quantity",
                "unitPrice" to "Unit price",
                "totalAmount" to "Total amount",
                "subtotal" to "Subtotal",
                "taxRate" to "Tax rate",
                "taxAmount" to "Tax amount",
                "grandTotal" to "Grand total",
                "agentName" to "Agent name",
                "agentEmail" to "Agent email"
            )
        )
        documentTemplateRepository.save(template)
    }

    private fun createQuotationTemplate(createdBy: User) {
        val template = DocumentTemplate(
            name = "Travel Quotation",
            templateContent = """
                <h2>Travel Quotation</h2>
                <p><strong>Quote #:</strong> {{quoteNumber}}</p>
                <p><strong>Date:</strong> {{quoteDate}}</p>
                <p><strong>Valid Until:</strong> {{validUntil}}</p>
                
                <h3>Customer Information</h3>
                <p>{{customerName}}<br>{{customerEmail}}<br>{{customerPhone}}</p>
                
                <h3>Proposed Itinerary</h3>
                <p><strong>Destination:</strong> {{destination}}</p>
                <p><strong>Travel Dates:</strong> {{travelDates}}</p>
                <p><strong>Number of Travelers:</strong> {{numberOfTravelers}}</p>
                
                <h3>Package Details</h3>
                {{packageDetails}}
                
                <h3>Pricing Summary</h3>
                <p><strong>Base Price:</strong> ${'$'}{{basePrice}}</p>
                <p><strong>Additional Services:</strong> ${'$'}{{additionalServices}}</p>
                <p><strong>Total Price:</strong> ${'$'}{{totalPrice}}</p>
                
                <h3>Terms</h3>
                <ul>
                    <li>Quote valid for {{validityPeriod}} days</li>
                    <li>50% deposit required to confirm booking</li>
                    <li>Final payment due {{finalPaymentDays}} days before travel</li>
                    <li>Cancellation policy applies</li>
                </ul>
                
                <p>For questions or to book this package, please contact:<br>
                {{agentName}} - {{agentEmail}} - {{agentPhone}}</p>
            """.trimIndent(),
            documentType = DocumentType.QUOTATION,
            createdBy = createdBy,
            description = "Standard quotation template for travel services"
        )
        documentTemplateRepository.save(template)
    }

    private fun createBookingConfirmationTemplate(createdBy: User) {
        val template = DocumentTemplate(
            name = "Booking Confirmation",
            templateContent = """
                <h2>Booking Confirmation</h2>
                <p><strong>Confirmation #:</strong> {{confirmationNumber}}</p>
                <p><strong>Booking Date:</strong> {{bookingDate}}</p>
                
                <h3>Customer Details</h3>
                <p>{{customerName}}<br>{{customerEmail}}<br>{{customerPhone}}</p>
                
                <h3>Trip Details</h3>
                <p><strong>Destination:</strong> {{destination}}</p>
                <p><strong>Departure:</strong> {{departureDate}} from {{departureLocation}}</p>
                <p><strong>Return:</strong> {{returnDate}} to {{returnLocation}}</p>
                <p><strong>Number of Travelers:</strong> {{numberOfTravelers}}</p>
                
                <h3>Accommodation</h3>
                <p><strong>Hotel:</strong> {{hotelName}}</p>
                <p><strong>Room Type:</strong> {{roomType}}</p>
                <p><strong>Check-in:</strong> {{checkinDate}}</p>
                <p><strong>Check-out:</strong> {{checkoutDate}}</p>
                
                <h3>Payment Summary</h3>
                <p><strong>Total Cost:</strong> ${'$'}{{totalCost}}</p>
                <p><strong>Amount Paid:</strong> ${'$'}{{amountPaid}}</p>
                <p><strong>Balance Due:</strong> ${'$'}{{balanceDue}}</p>
                
                <h3>Important Information</h3>
                <ul>
                    <li>Please bring valid passport/ID</li>
                    <li>Check-in opens {{checkinTime}}</li>
                    <li>Contact us 48 hours before departure</li>
                    <li>Travel insurance recommended</li>
                </ul>
                
                <p>Your travel agent: {{agentName}} - {{agentEmail}} - {{agentPhone}}</p>
            """.trimIndent(),
            documentType = DocumentType.BOOKING_CONFIRMATION,
            createdBy = createdBy,
            description = "Booking confirmation template"
        )
        documentTemplateRepository.save(template)
    }

    private fun createItineraryTemplate(createdBy: User) {
        val template = DocumentTemplate(
            name = "Detailed Itinerary",
            templateContent = """
                <h2>{{tripTitle}} - Detailed Itinerary</h2>
                <p><strong>Trip Dates:</strong> {{tripDates}}</p>
                <p><strong>Traveler(s):</strong> {{travelerNames}}</p>
                
                {{dayByDayItinerary}}
                
                <h3>Important Contacts</h3>
                <p><strong>Emergency Contact:</strong> {{emergencyContact}}</p>
                <p><strong>Local Guide:</strong> {{localGuide}}</p>
                <p><strong>Your Travel Agent:</strong> {{agentName}} - {{agentPhone}}</p>
                
                <h3>What to Bring</h3>
                <ul>
                    <li>Valid passport and travel documents</li>
                    <li>Comfortable walking shoes</li>
                    <li>Weather-appropriate clothing</li>
                    <li>Camera and chargers</li>
                    <li>Personal medications</li>
                    <li>{{additionalItems}}</li>
                </ul>
                
                <h3>Travel Tips</h3>
                {{travelTips}}
            """.trimIndent(),
            documentType = DocumentType.ITINERARY,
            createdBy = createdBy,
            description = "Detailed itinerary template for trips"
        )
        documentTemplateRepository.save(template)
    }

    private fun createSampleDocuments(admin: User, manager: User, agent1: User, agent2: User) {
        // Sample tour package
        val tourPackage = Document(
            title = "European Grand Tour - 14 Days",
            content = "<h2>European Grand Tour - 14 Days</h2><p>Experience the best of Europe in this comprehensive 14-day tour...</p>",
            type = DocumentType.TOUR_PACKAGE,
            createdBy = agent1,
            assignedTo = manager,
            status = DocumentStatus.APPROVED,
            customerName = "Robert & Maria Johnson",
            customerEmail = "robert.johnson@email.com",
            customerPhone = "+1-555-1234",
            totalAmount = BigDecimal("4500.00"),
            tourCode = "EUR-GT-001",
            destination = "France, Italy, Germany, Spain",
            startDate = LocalDateTime.now().plusDays(30),
            endDate = LocalDateTime.now().plusDays(44),
            numberOfPassengers = 2,
            approvedBy = manager,
            approvedAt = LocalDateTime.now().minusDays(1)
        )
        documentRepository.save(tourPackage)

        // Sample invoice
        val invoice = Document(
            title = "Invoice - European Grand Tour",
            content = "<h1>INVOICE</h1><p>Invoice for European Grand Tour services...</p>",
            type = DocumentType.INVOICE,
            createdBy = agent1,
            status = DocumentStatus.APPROVED,
            customerName = "Robert & Maria Johnson",
            customerEmail = "robert.johnson@email.com",
            totalAmount = BigDecimal("4500.00"),
            paidAmount = BigDecimal("2250.00"),
            tourCode = "EUR-GT-001",
            approvedBy = manager,
            approvedAt = LocalDateTime.now().minusHours(6)
        )
        documentRepository.save(invoice)

        // Sample quotation
        val quotation = Document(
            title = "Quote - Asian Adventure Tour",
            content = "<h2>Asian Adventure Tour Quote</h2><p>Customized tour package for Thailand and Vietnam...</p>",
            type = DocumentType.QUOTATION,
            createdBy = agent2,
            status = DocumentStatus.PENDING_REVIEW,
            customerName = "David Chen",
            customerEmail = "david.chen@email.com",
            customerPhone = "+1-555-5678",
            totalAmount = BigDecimal("3200.00"),
            tourCode = "ASIA-ADV-002",
            destination = "Thailand, Vietnam",
            startDate = LocalDateTime.now().plusDays(45),
            endDate = LocalDateTime.now().plusDays(56),
            numberOfPassengers = 1
        )
        documentRepository.save(quotation)

        // Sample booking confirmation
        val booking = Document(
            title = "Booking Confirmation - Caribbean Cruise",
            content = "<h2>Booking Confirmation</h2><p>Your Caribbean cruise has been confirmed...</p>",
            type = DocumentType.BOOKING_CONFIRMATION,
            createdBy = agent1,
            status = DocumentStatus.APPROVED,
            customerName = "Sarah & Mike Williams",
            customerEmail = "sarah.williams@email.com",
            customerPhone = "+1-555-9876",
            totalAmount = BigDecimal("2800.00"),
            paidAmount = BigDecimal("2800.00"),
            tourCode = "CAR-CRU-003",
            destination = "Caribbean Islands",
            startDate = LocalDateTime.now().plusDays(60),
            endDate = LocalDateTime.now().plusDays(67),
            numberOfPassengers = 2,
            approvedBy = manager,
            approvedAt = LocalDateTime.now().minusHours(2)
        )
        documentRepository.save(booking)

        // Sample expense report
        val expenseReport = Document(
            title = "Monthly Expense Report - March 2024",
            content = "<h2>Agent Expense Report</h2><p>Monthly expenses for March 2024...</p>",
            type = DocumentType.EXPENSE_REPORT,
            createdBy = agent2,
            assignedTo = manager,
            status = DocumentStatus.PENDING_REVIEW,
            totalAmount = BigDecimal("850.00")
        )
        documentRepository.save(expenseReport)
    }
}
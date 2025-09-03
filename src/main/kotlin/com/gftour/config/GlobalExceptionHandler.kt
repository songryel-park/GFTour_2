package com.gftour.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.resource.NoResourceFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DataAccessException
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest

/**
 * Global exception handler for the Good Feel Tour application.
 * Provides centralized exception handling and user-friendly error responses.
 */
@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(
        ex: Exception,
        request: HttpServletRequest
    ): ModelAndView {
        logger.error("Unexpected error occurred: ${ex.message}", ex)
        
        val modelAndView = ModelAndView("error/500")
        modelAndView.addObject("error", "Internal Server Error")
        modelAndView.addObject("message", "An unexpected error occurred. Please try again later.")
        modelAndView.addObject("path", request.requestURI)
        return modelAndView
    }

    /**
     * Handle entity not found exceptions
     */
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(
        ex: EntityNotFoundException,
        request: HttpServletRequest
    ): ModelAndView {
        logger.warn("Entity not found: ${ex.message}")
        
        val modelAndView = ModelAndView("error/404")
        modelAndView.addObject("error", "Not Found")
        modelAndView.addObject("message", ex.message ?: "The requested resource was not found.")
        modelAndView.addObject("path", request.requestURI)
        return modelAndView
    }

    /**
     * Handle access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        ex: AccessDeniedException,
        request: HttpServletRequest
    ): ModelAndView {
        logger.warn("Access denied: ${ex.message}")
        
        val modelAndView = ModelAndView("error/403")
        modelAndView.addObject("error", "Access Denied")
        modelAndView.addObject("message", "You don't have permission to access this resource.")
        modelAndView.addObject("path", request.requestURI)
        return modelAndView
    }

    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(MethodArgumentNotValidException::class, BindException::class)
    fun handleValidationException(
        ex: Exception,
        request: HttpServletRequest
    ): ModelAndView {
        logger.warn("Validation error: ${ex.message}")
        
        val modelAndView = ModelAndView("error/400")
        modelAndView.addObject("error", "Validation Error")
        modelAndView.addObject("message", "Please check your input and try again.")
        modelAndView.addObject("path", request.requestURI)
        return modelAndView
    }

    /**
     * Handle database constraint violations
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        ex: DataIntegrityViolationException,
        request: HttpServletRequest
    ): ModelAndView {
        logger.error("Data integrity violation: ${ex.message}", ex)
        
        val modelAndView = ModelAndView("error/400")
        modelAndView.addObject("error", "Data Conflict")
        modelAndView.addObject("message", "The operation conflicts with existing data.")
        modelAndView.addObject("path", request.requestURI)
        return modelAndView
    }

    /**
     * Handle database access exceptions
     */
    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(
        ex: DataAccessException,
        request: HttpServletRequest
    ): ModelAndView {
        logger.error("Database access error: ${ex.message}", ex)
        
        val modelAndView = ModelAndView("error/500")
        modelAndView.addObject("error", "Database Error")
        modelAndView.addObject("message", "A database error occurred. Please try again later.")
        modelAndView.addObject("path", request.requestURI)
        return modelAndView
    }

    /**
     * Handle file upload size exceeded exceptions
     */
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceededException(
        ex: MaxUploadSizeExceededException,
        request: HttpServletRequest
    ): ModelAndView {
        logger.warn("File upload size exceeded: ${ex.message}")
        
        val modelAndView = ModelAndView("error/400")
        modelAndView.addObject("error", "File Too Large")
        modelAndView.addObject("message", "The uploaded file is too large. Maximum size is 10MB.")
        modelAndView.addObject("path", request.requestURI)
        return modelAndView
    }

    /**
     * Handle resource not found exceptions (404)
     */
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(
        ex: NoResourceFoundException,
        request: HttpServletRequest
    ): ModelAndView {
        logger.warn("Resource not found: ${request.requestURI}")
        
        val modelAndView = ModelAndView("error/404")
        modelAndView.addObject("error", "Page Not Found")
        modelAndView.addObject("message", "The requested page could not be found.")
        modelAndView.addObject("path", request.requestURI)
        return modelAndView
    }
}
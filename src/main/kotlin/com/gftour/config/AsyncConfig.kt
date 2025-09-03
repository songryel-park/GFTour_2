package com.gftour.config

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

/**
 * Async configuration for Good Feel Tour application.
 * Configures thread pool for asynchronous processing including file uploads.
 */
@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer {

    private val logger = LoggerFactory.getLogger(AsyncConfig::class.java)

    /**
     * Configure the async executor for the application
     */
    @Bean("taskExecutor")
    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        
        // Core thread pool settings
        executor.corePoolSize = 2
        executor.maxPoolSize = 10
        executor.queueCapacity = 25
        
        // Thread naming
        executor.setThreadNamePrefix("GfTour-Async-")
        
        // Rejection policy when queue is full
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        
        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(20)
        
        executor.initialize()
        
        logger.info("Async executor configured with core pool size: {}, max pool size: {}, queue capacity: {}",
            executor.corePoolSize, executor.maxPoolSize, executor.queueCapacity)
        
        return executor
    }

    /**
     * Configure exception handler for async methods
     */
    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return AsyncUncaughtExceptionHandler { ex, method, params ->
            logger.error("Async method execution failed - Method: {}, Parameters: {}, Exception: {}", 
                method.name, params.contentToString(), ex.message, ex)
        }
    }
}
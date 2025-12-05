package com.pega.pegarules.pub.presence

import spock.lang.Specification

/**
 * Comprehensive test coverage for PresenceException class
 * Target: 100% coverage for all constructors
 */
class PresenceExceptionCoverageTest extends Specification {

    def "test PresenceException default constructor"() {
        when: "Creating instance with no arguments"
        def exception = new PresenceException()
        
        then: "Instance should be created successfully"
        exception != null
        exception instanceof Exception
        exception instanceof PresenceException
    }

    def "test PresenceException constructor with String message"() {
        when: "Creating instance with message"
        def message = "Test presence error message"
        def exception = new PresenceException(message)
        
        then: "Instance should be created with correct message"
        exception != null
        exception instanceof Exception
        exception instanceof PresenceException
        exception.getMessage() == message
        exception.cause == null
    }

    def "test PresenceException constructor with empty String message"() {
        when: "Creating instance with empty message"
        def exception = new PresenceException("")
        
        then: "Instance should be created with empty message"
        exception != null
        exception instanceof Exception
        exception instanceof PresenceException
        exception.getMessage() == ""
        exception.cause == null
    }

    def "test PresenceException constructor with String message and Throwable cause"() {
        when: "Creating instance with both message and cause"
        def message = "Test error with cause"
        def cause = new Exception("Root cause")
        def exception = new PresenceException(message, cause)
        
        then: "Instance should be created with both message and cause"
        exception != null
        exception instanceof Exception
        exception instanceof PresenceException
        exception.getMessage() == message
        exception.cause == cause
        exception.cause instanceof Exception
        exception.cause.getMessage() == "Root cause"
    }

    def "test PresenceException constructor with empty message and cause"() {
        when: "Creating instance with empty message and cause"
        def cause = new Exception("Root cause")
        def exception = new PresenceException("", cause)
        
        then: "Instance should be created with empty message and cause"
        exception != null
        exception instanceof Exception
        exception instanceof PresenceException
        exception.getMessage() == ""
        exception.cause == cause
    }

    def "test PresenceException constructor with null message and cause"() {
        when: "Creating instance with null message and cause"
        def cause = new Exception("Root cause")
        def exception = new PresenceException(null as String, cause)
        
        then: "Instance should be created with null message and cause"
        exception != null
        exception instanceof Exception
        exception instanceof PresenceException
        exception.cause == cause
    }

    def "test PresenceException constructor with message and null cause"() {
        when: "Creating instance with message and null cause"
        def message = "Test error message"
        def exception = new PresenceException(message, null as Throwable)
        
        then: "Instance should be created with message and null cause"
        exception != null
        exception instanceof Exception
        exception instanceof PresenceException
        exception.getMessage() == message
        exception.cause == null
    }

    def "test PresenceException constructor with both null parameters"() {
        when: "Creating instance with null message and cause"
        def exception = new PresenceException(null as String, null as Throwable)
        
        then: "Instance should be created with null values"
        exception != null
        exception instanceof Exception
        exception instanceof PresenceException
    }

    def "test PresenceException chaining with different exception types as cause"() {
        when: "Creating instance with different cause types"
        def illegalArgumentCause = new IllegalArgumentException("Invalid argument")
        def exception1 = new PresenceException("Error with IllegalArgumentException", illegalArgumentCause)
        
        then: "Should handle different cause types correctly"
        exception1.cause == illegalArgumentCause
        exception1.cause instanceof IllegalArgumentException
        
        when: "Creating instance with custom exception as cause"
        def customCause = new PresenceException("Nested presence error")
        def exception2 = new PresenceException("Error with nested PresenceException", customCause)
        
        then: "Should handle PresenceException as cause"
        exception2.cause == customCause
        exception2.cause instanceof PresenceException
    }

    def "test PresenceException inheritance chain"() {
        when: "Creating instance and checking inheritance chain"
        def cause = new Exception("Root cause")
        def exception = new PresenceException("Test error", cause)
        
        then: "Should have proper inheritance"
        exception instanceof PresenceException
        exception instanceof Exception
        exception instanceof Throwable
        exception.fillInStackTrace() != null
    }

    def "test PresenceException with various message lengths"() {
        when: "Testing various message lengths"
        def longMessage = "This is a very long error message that contains multiple details about the presence system failure and should be preserved exactly as provided"
        def shortMessage = "X"
        
        def exception1 = new PresenceException(longMessage)
        def exception2 = new PresenceException(shortMessage)
        
        then: "All message lengths should be preserved"
        exception1.getMessage() == longMessage
        exception2.getMessage() == shortMessage
    }
}

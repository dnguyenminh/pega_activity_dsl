package com.pega.pegarules.pub

import spock.lang.Specification

class PRAppRuntimeExceptionTest extends Specification {

    def "PRAppRuntimeException constructors and methods"() {
        given:
        def ruleSetName = "MyRuleSet"
        def reason = 123.45
        def message = "Test runtime exception"
        def cause = new Exception("Root cause")

        when:
        def exception1 = new PRAppRuntimeException(ruleSetName, reason, message)
        def exception2 = new PRAppRuntimeException(ruleSetName, reason, message, cause)

        then:
        // Test basic properties
        exception1 instanceof RuntimeException
        exception2 instanceof RuntimeException
        exception1.message == message
        exception2.message == message
        exception1.ruleSetName == ruleSetName
        exception2.ruleSetName == ruleSetName
        exception1.reason == reason
        exception2.reason == reason

        // Test cause
        exception1.cause == null
        exception2.cause == cause

        // Test getter methods
        exception1.getRuleSetName() == ruleSetName
        exception2.getRuleSetName() == ruleSetName
        exception1.getReason() == reason
        exception2.getReason() == reason

        // Test toString method
        def toString1 = exception1.toString()
        def toString2 = exception2.toString()

        toString1.contains("PRAppRuntimeException")
        toString1.contains(message)
        toString1.contains("ruleset=${ruleSetName}")
        toString1.contains("reason=${reason}")

        toString2.contains("PRAppRuntimeException")
        toString2.contains(message)
        toString2.contains("ruleset=${ruleSetName}")
        toString2.contains("reason=${reason}")
    }

    def "PRAppRuntimeException with null values"() {
        given:
        def ruleSetName = null
        def reason = 0.0
        def message = "null message" // Use a non-null message to avoid constructor ambiguity

        when:
        def exception = new PRAppRuntimeException(ruleSetName, reason, message)

        then:
        exception instanceof RuntimeException
        exception.ruleSetName == null
        exception.reason == 0.0
        exception.message == message
        exception.getRuleSetName() == null
        exception.getReason() == 0.0

        // toString should still work with null values
        def toStringResult = exception.toString()
        toStringResult.contains("PRAppRuntimeException")
    }

    def "PRAppRuntimeException with negative reason"() {
        given:
        def ruleSetName = "TestRuleSet"
        def reason = -999.99
        def message = "Negative reason test"

        when:
        def exception = new PRAppRuntimeException(ruleSetName, reason, message)

        then:
        exception instanceof RuntimeException
        exception.reason == reason
        exception.getReason() == reason
        exception.message == message
    }
}

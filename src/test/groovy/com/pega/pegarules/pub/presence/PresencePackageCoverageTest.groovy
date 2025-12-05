package com.pega.pegarules.pub.presence

import spock.lang.Specification

/**
 * Test coverage for PresenceException and PresenceService classes.
 */
class PresencePackageCoverageTest extends Specification {

    def "PresenceException default constructor"() {
        when:
        def exception = new PresenceException()

        then:
        exception != null
        exception.message == null
        exception.cause == null
    }

    def "PresenceException constructor with message"() {
        when:
        def exception = new PresenceException("Test message")

        then:
        exception != null
        exception.message == "Test message"
        exception.cause == null
    }

    def "PresenceException constructor with message and cause"() {
        given:
        def cause = new RuntimeException("Original cause")

        when:
        def exception = new PresenceException("Test message", cause)

        then:
        exception != null
        exception.message == "Test message"
        exception.cause == cause
    }

    def "PresenceException constructor with cause"() {
        given:
        def cause = new RuntimeException("Original cause")

        when:
        def exception = new PresenceException(cause)

        then:
        exception != null
        exception.message != null
        exception.cause == cause
    }

    def "PresenceService interface exists"() {
        expect:
        PresenceService.class.isInterface()
    }

    def "PresenceService methods are declared"() {
        when:
        def methods = PresenceService.declaredMethods.findAll { !it.synthetic }

        then:
        methods.size() == 4
        methods*.name.containsAll([
            "setAttributes",
            "setAttributesWithoutOverwrite", 
            "setCurrentRequestorStateAsDisconnected",
            "clearCustomAttributes"
        ])
    }

    def "ControlsInfo enum exists and has values"() {
        when:
        def controlEditOptions = com.pega.pegarules.pub.runtime.ControlsInfo.ControlEditOption.values()

        then:
        controlEditOptions.size() == 3
        controlEditOptions*.name().containsAll(["EDITABLE", "READONLY", "HIDDEN"])
    }
}

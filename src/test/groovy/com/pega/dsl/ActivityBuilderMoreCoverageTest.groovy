package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderMoreCoverageTest extends Specification {

    def "Test callActivity with forced rehydration failure"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        def params = [
                '__force_rehydration_failure__': true,
                'param1'                       : { -> 'value1' }
        ]

        when:
        builder.callActivity("MyActivity", params)

        then:
        def step = activity.getSteps().last()
        step.getMethod() == 'Call'
        step.getParameters()['activity'] == 'MyActivity'
        step.getParameters()['param1'] instanceof Closure
    }

    def "Test parseStringAndMapArgs with various inputs"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        def result1 = builder.parseStringAndMapArgs(null)
        then:
        result1 == null

        when:
        def result2 = builder.parseStringAndMapArgs()
        then:
        result2 == null

        when:
        def result3 = builder.parseStringAndMapArgs(123)
        then:
        result3 == null

        when:
        def result4 = builder.parseStringAndMapArgs("test", "not a map")
        then:
        result4 == null

        when:
        def result5 = builder.parseStringAndMapArgs("test", null)
        then:
        result5.string == "test"
        result5.map == [:]

        when:
        def result6 = builder.parseStringAndMapArgs("test", [key: "value"])
        then:
        result6.string == "test"
        result6.map == [key: "value"]
    }

    def "Test description with different delegate"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        def otherDelegate = new Object()

        when:
        PegaDslCore.CURRENT_DELEGATE.set(otherDelegate)
        builder.description("test description")

        then:
        activity.getDescription() == null

        and:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "Test propertySet with different delegate"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        def otherDelegate = new Object()

        when:
        PegaDslCore.CURRENT_DELEGATE.set(otherDelegate)
        builder.propertySet("prop", "val")

        then:
        activity.getSteps().isEmpty()

        and:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "Test step with different delegate"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        def otherDelegate = new Object()

        when:
        PegaDslCore.CURRENT_DELEGATE.set(otherDelegate)
        builder.step("test")

        then:
        activity.getSteps().isEmpty()

        and:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }
}
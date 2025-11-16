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

    def "Test queueVarargs with activity name and map parameters to cover test marker"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        def params = [param1: "value1", param2: "value2"]

        when:
        builder.queueVarargs("MyQueuedActivity", params)

        then:
        builder.__test_marker_269__ == true
        def step = activity.getSteps().last()
        step.getMethod() == 'Queue'
        step.getParameters()['Activity'] == 'MyQueuedActivity'
        step.getParameters()['param1'] == 'value1'
        step.getParameters()['param2'] == 'value2'
    }

    def "description when delegate is this sets description"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        PegaDslCore.CURRENT_DELEGATE.set(builder)
        def result = builder.description("hello world")

        then:
        activity.getDescription() == "hello world"
        result == builder

        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "propertySet(String,String) when delegate is this creates Property-Set step"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        PegaDslCore.CURRENT_DELEGATE.set(builder)
        builder.propertySet("myProp", "myVal")

        then:
        activity.getSteps().size() == 1
        def step = activity.getSteps().last()
        step.getMethod() == 'Property-Set'
        step.getParameters()['PropertyName'] == 'myProp'
        step.getParameters()['PropertyValue'] == 'myVal'

        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "parseStringAndMapArgs explicit two-arg shapes"() {
        given:
        def builder = new ActivityBuilder(new Activity())

        when:
        def r1 = builder.&parseStringAndMapArgs(['s', [a:1]] as Object[])
        def r2 = builder.&parseStringAndMapArgs(['s', 'x'] as Object[])

        then:
        r1.string == 's'
        r1.map == [a:1]
        r2 == null
    }

    def "description(Map) when delegate is this sets description"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        PegaDslCore.CURRENT_DELEGATE.set(builder)
        def result = builder.description("hello map", [p:1])

        then:
        activity.getDescription() == "hello map"
        result == builder

        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "parseStringAndMapArgs multiple shapes to exercise >=2 branch"() {
        given:
        def builder = new ActivityBuilder(new Activity())

        when:
        def a1 = builder.&parseStringAndMapArgs(['s'] as Object[])
        def a2 = builder.&parseStringAndMapArgs(['s', [x:1]] as Object[])
        def a3 = builder.&parseStringAndMapArgs(['s', null] as Object[])
        def a4 = builder.&parseStringAndMapArgs(['s', 123] as Object[])
        def a5 = builder.&parseStringAndMapArgs(['s', [x:1], 2] as Object[])

        then:
        a1.string == 's'
        a2.string == 's'
        a3.string == 's'
        a4 == null
        a5.string == 's'
    }
}
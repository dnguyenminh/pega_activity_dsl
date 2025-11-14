package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderPropertySetTest extends Specification {

    def "propertySet with property and LinkedHashMap"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        def params = new LinkedHashMap()
        params.put("param1", "value1")

        when:
        builder.propertySet("testProperty", params)

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Property-Set'
        step.parameters['PropertyName'] == 'testProperty'
        step.parameters['param1'] == 'value1'
    }

    def "propertySet with just LinkedHashMap"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        def params = new LinkedHashMap()
        params.put("param1", "value1")

        when:
        builder.propertySet(params)

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Property-Set'
        step.parameters['param1'] == 'value1'
    }
}

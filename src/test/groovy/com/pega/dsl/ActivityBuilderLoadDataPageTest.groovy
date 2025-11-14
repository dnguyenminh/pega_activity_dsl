package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderLoadDataPageTest extends Specification {

    def "loadDataPage with varargs"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.loadDataPage("TestDataPage", [param1: "value1"])

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Load-DataPage'
        step.parameters['DataPageName'] == 'TestDataPage'
        step.parameters['param1'] == 'value1'
    }
}

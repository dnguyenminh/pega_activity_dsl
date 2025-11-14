package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderOtherMethodsTest extends Specification {

    def "showPage method"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.showPage("TestPage", "XML")

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Show-Page'
        step.parameters['PageName'] == 'TestPage'
        step.parameters['Format'] == 'XML'
    }

    def "branch method"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.branch("TestActivity", "true")

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Branch'
        step.parameters['Activity'] == 'TestActivity'
        step.parameters['Condition'] == 'true'
    }

    def "logMessage method"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.logMessage("Test Message", "DEBUG")

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Log-Message'
        step.parameters['Message'] == 'Test Message'
        step.parameters['Level'] == 'DEBUG'
    }

    def "commit method"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.commit()

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Commit'
    }

    def "rollback method"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.rollback()

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Rollback'
    }

    def "step method"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.step("CustomStep") {
            parameters["param1"] = "value1"
        }

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'CustomStep'
        step.parameters["param1"] == "value1"
    }

    def "setStatus method"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.setStatus("FAIL")

        then:
        activity.status == "FAIL"
    }

    def "setAvailable method"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.setAvailable(false)

        then:
        activity.isAvailable == false
    }

    def "connectREST with invalid arguments"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.connectREST("MyConnector", "invalid")

        then:
        activity.steps.size() == 0
    }
}

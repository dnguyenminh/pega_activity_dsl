package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderConnectorsTest extends Specification {

    def "connectREST with varargs"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.connectREST("TestConnector", [param1: "value1"])

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Connect-REST'
        step.parameters['connector'] == 'TestConnector'
        step.parameters['param1'] == 'value1'
    }

    def "connectSOAP with varargs"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.connectSOAP("TestConnector", [param1: "value1"])

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Connect-SOAP'
        step.parameters['connector'] == 'TestConnector'
        step.parameters['param1'] == 'value1'
    }

    def "connectREST with varargs and non-map second argument should return this"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        def result = builder.connectREST("TestConnector", 123)

        then:
        result == builder // Should return the builder itself
        activity.steps.size() == 0 // No step should be added
    }

    def "connectREST with single string argument should create step with empty map"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.connectREST("SingleStringConnector")

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Connect-REST'
        step.parameters['connector'] == 'SingleStringConnector'
        step.parameters.size() == 1 // Only 'connector' parameter
    }
}

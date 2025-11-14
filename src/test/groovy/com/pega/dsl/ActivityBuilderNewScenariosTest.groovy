package com.pega.dsl

import com.pega.pegarules.pub.database.Database
import com.pega.pegarules.pub.runtime.PublicAPI
import spock.lang.Specification

class ActivityBuilderNewScenariosTest extends Specification {

    def "Test add comment with different inputs"() {
        given:
        def activity = new Activity()
        def database = Mock(Database)
        def tools = Mock(PublicAPI)
        tools.getDatabase() >> database
        def builder = new ActivityBuilder(activity)

        when:
        builder.addComment(null)
        then:
        builder.getActivity().steps.last().getComment() == null

        when:
        builder.addComment("")
        then:
        activity.getSteps().last().getComment() == ""

        when:
        builder.addComment("   ")
        then:
        activity.getSteps().last().getComment() == "   "
    }

    def "Test property remove with different inputs"() {
        given:
        def activity = new Activity()
        def database = Mock(Database)
        def tools = Mock(PublicAPI)
        tools.getDatabase() >> database
        def builder = new ActivityBuilder(activity)

        when:
        builder.propertyRemove("pyWorkPage")
        then:
        def step = builder.getActivity().getSteps().last()
        step.getMethod() == "Property-Remove"
        step.getStepPage() == "pyWorkPage"
    }

    def "Test wait with different inputs"() {
        given:
        def activity = new Activity()
        def database = Mock(Database)
        def tools = Mock(PublicAPI)
        tools.getDatabase() >> database
        def builder = new ActivityBuilder(activity)

        when:
        builder.waitSeconds(1)
        then:
        def step = builder.getActivity().getSteps().last()
        step.getMethod() == "Wait"
        step.getProperties()["Seconds"] == "1"

        when:
        builder.waitSeconds(0)
        step = builder.getActivity().getSteps().last()
        then:
        step.getMethod() == "Wait"
        step.getProperties()["Seconds"] == "0"

        when:
        builder.waitSeconds(-1)
        step = builder.getActivity().getSteps().last()
        then:
        step.getMethod() == "Wait"
        step.getProperties()["Seconds"] == "-1"
    }

    def "Test queueVarargs with a map parameter to cover line 59"() {
        given:
        def activity = new Activity()
        def database = Mock(Database)
        def tools = Mock(PublicAPI)
        tools.getDatabase() >> database
        def builder = new ActivityBuilder(activity)

        when:
        builder.queueVarargs("ActivityName", [param1: "value1"])
        then:
        builder.__test_marker_269__ == true
        def step = builder.getActivity().getSteps().last()
        step.getMethod() == "Queue"
        step.parameters['Activity'] == "ActivityName"
        step.parameters['param1'] == "value1"
    }
}
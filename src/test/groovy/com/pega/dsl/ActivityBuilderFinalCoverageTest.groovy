package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderFinalCoverageTest extends Specification {

    def "connectREST with non-map second arg should not create a step"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when:
        builder.connectREST("MyConnector", "notAMap")

        then:
        // parseStringAndMapArgs should return null for this shape and no step is added
        activity.getSteps().isEmpty()
    }

    def "description(String,Map) when delegate is different does nothing"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        def other = new Object()

        when:
        PegaDslCore.CURRENT_DELEGATE.set(other)
        builder.description("should not set", [p:1])

        then:
        activity.getDescription() == null

        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }
}

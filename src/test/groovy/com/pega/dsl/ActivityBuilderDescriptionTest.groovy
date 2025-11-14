package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderDescriptionTest extends Specification {

    def "description with LinkedHashMap"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        def params = new LinkedHashMap()

        when:
        builder.description("Test Description", params)

        then:
        activity.getDescription() == "Test Description"
    }
}

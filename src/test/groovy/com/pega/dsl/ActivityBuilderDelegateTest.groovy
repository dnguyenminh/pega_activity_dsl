package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderDelegateTest extends Specification {

    def "description method does nothing when delegate is different"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        def otherDelegate = new Object()

        and:
        PegaDslCore.CURRENT_DELEGATE.set(otherDelegate)

        when:
        builder.description("test description")

        then:
        activity.getDescription() == null
        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "description when delegate is this"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        PegaDslCore.CURRENT_DELEGATE.set(builder)

        when:
        builder.description("Test Description")

        then:
        activity.description == "Test Description"
        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }
}

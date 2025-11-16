package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderForceBranchesTest extends Specification {
    def "exercise delegate guard combinations and parse shapes to hit remaining branches"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)

        when: "delegate is removed -> should perform builder actions"
        PegaDslCore.CURRENT_DELEGATE.remove()
        builder.description("d1")
        builder.propertySet("p1","v1")

        and: "delegate is set to another object -> defensive return"
        PegaDslCore.CURRENT_DELEGATE.set(new Object())
        builder.description("d2")
        builder.propertySet("p2","v2")

        and: "delegate is set to builder itself -> should perform builder actions"
        PegaDslCore.CURRENT_DELEGATE.set(builder)
        builder.description("d3")
        builder.propertySet("p3","v3")

        and: "call parseStringAndMapArgs with various two+ arg shapes"
        def r1 = builder.&parseStringAndMapArgs(['s', [a:1], 3] as Object[])
        def r2 = builder.&parseStringAndMapArgs(['s', [a:1]] as Object[])
        def r3 = builder.&parseStringAndMapArgs(['s', null] as Object[])
        def r4 = builder.&parseStringAndMapArgs(['s', 123] as Object[])

        then:
        // We only assert minimal expectations so this test is primarily for exercising branches
        activity.getDescription() == 'd3'
        activity.getSteps().find { it.parameters['PropertyName'] == 'p3' } != null
        r1.string == 's'
        r2.string == 's'
        r3.string == 's'
        r4 == null

        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }
}

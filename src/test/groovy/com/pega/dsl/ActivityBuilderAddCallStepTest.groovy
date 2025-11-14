package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderAddCallStepTest extends Specification {

    def "addCallStep rehydrates closure successfully"() {
        given:
        def activity = new com.pega.dsl.Activity()
        def b = new com.pega.dsl.ActivityBuilder(activity)
        def closure = { -> parameters['__rehydrated_marker'] = true }

        when:
        b.addCallStep('CallAdd', [hook: closure])

        then:
        activity.steps.size() == 1
        def stored = activity.steps[0].parameters['hook']
        stored instanceof Closure
        stored.call()
        activity.steps[0].parameters['__rehydrated_marker'] == true
    }

    def "addCallStep rehydration fallback on forced failure"() {
        given:
        def activity = new com.pega.dsl.Activity()
        def b = new com.pega.dsl.ActivityBuilder(activity)
        def closure = { -> 123 }

        when:
        b.addCallStep('CallAddFail', [hook: closure, '__force_rehydration_failure__': true])

        then:
        activity.steps.size() == 1
        activity.steps[0].parameters['hook'] == closure
    }

}
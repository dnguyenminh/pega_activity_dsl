package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class AdditionalActivitySpec extends Specification {

    def "empty activity has sensible defaults"() {
        when:
        def act = activity('EmptyTest') { }

        then:
        act.name == 'EmptyTest'
        act.type == 'Activity'
        act.steps != null
        act.steps.isEmpty()
        act.localVariables != null
        act.localVariables.isEmpty()
    }

    def "mixed step types preserve insertion order"() {
        when:
        def act = activity('OrderTest') {
            propertySet '.A', '1'
            callActivity 'ProcessA'
            propertySet '.B', '2'
        }

        then:
        act.steps.size() == 3
        act.steps[0].method == 'Property-Set'
        act.steps[1].method == 'Call'
        act.steps[2].method == 'Property-Set'

        act.steps[0].parameters['PropertyName'] == '.A'
        act.steps[0].parameters['PropertyValue'] == '1'
        act.steps[1].parameters['activity'] == 'ProcessA'
        act.steps[2].parameters['PropertyName'] == '.B'
        act.steps[2].parameters['PropertyValue'] == '2'
    }

    def "propertySet overloads (String,Map) and propertySet(Map) work as expected"() {
        when:
        def act = activity('PropOverloads') {
            propertySet '.X', 'V1'
            propertySet '.Y', [PropertyValue: 'V2']
            propertySet(PropertyName: '.Z', PropertyValue: 'V3')
        }

        then:
        act.steps.size() == 3
        act.steps[0].method == 'Property-Set'
        act.steps[0].parameters['PropertyName'] == '.X'
        act.steps[0].parameters['PropertyValue'] == 'V1'

        act.steps[1].method == 'Property-Set'
        act.steps[1].parameters['PropertyName'] == '.Y'
        act.steps[1].parameters['PropertyValue'] == 'V2'

        act.steps[2].method == 'Property-Set'
        act.steps[2].parameters['PropertyName'] == '.Z'
        act.steps[2].parameters['PropertyValue'] == 'V3'
    }

    def "callActivity accepts closure param rehydrated to step delegate"() {
        when:
        def act = activity('CallWithClosure') {
            callActivity 'ProcX', [onComplete: { parameter 'Result', 'OK' }]
        }

        then:
        act.steps.size() == 1
        def step = act.steps[0]
        step.method == 'Call'
        step.parameters['activity'] == 'ProcX'
        step.parameters['onComplete'] instanceof Closure

        when: // invoke the stored closure to exercise rehydration
        def closure = step.parameters['onComplete'] as Closure
        // the closure was rehydrated to the step as delegate when created; calling it should add parameters to the step
        closure.call()

        then:
        step.parameters['Result'] == 'OK'
    }

    def "step closure sets parameters, condition, transition and iterate flag"() {
        when:
        def act = activity('StepClosureTest') {
            step('Custom-Step') {
                parameter 'P1', 123
                this.when('.Flag == true')
                transitionTo '99'
                iterate()
            }
        }

        then:
        act.steps.size() == 1
        def s = act.steps[0]
        s.method == 'Custom-Step'
        s.parameters['P1'] == 123
        s.condition == '.Flag == true'
        s.transition == '99'
        s.isIterate
    }
}

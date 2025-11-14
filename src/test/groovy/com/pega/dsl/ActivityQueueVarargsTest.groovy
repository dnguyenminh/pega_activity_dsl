package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class ActivityQueueVarargsTest extends Specification {
    def "queue with two arguments (String, Map) creates Queue step"() {
        when:
        def a = activity('TestQueueActivity') {
            queue 'UncoveredQueue', [foo:'bar']
        }

        then:
        a.steps.find { it.method == 'Queue' && it.parameters['Activity'] == 'UncoveredQueue' && it.parameters['foo'] == 'bar' } != null
    }

    def "direct call to builder.queue(varargs) exercises queueVarargs branch"() {
        given:
        def a = new Activity()
        def builder = new ActivityBuilder(a)

        when:
        // Call the varargs entrypoint explicitly so the queueVarargs branch (and test marker) is exercised
        builder.queueVarargs('UncoveredQueueDirect', [foo:'bar'])
        
        then:
        // Confirm the Queue step was added
        a.steps.find { it.method == 'Queue' && it.parameters['Activity'] == 'UncoveredQueueDirect' && it.parameters['foo'] == 'bar' } != null
        // Confirm the queueVarargs branch executed (marker set)
        builder.__test_marker_269__ == true
    }
}
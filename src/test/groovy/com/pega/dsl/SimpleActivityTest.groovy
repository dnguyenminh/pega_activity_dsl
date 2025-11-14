package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

/**
 * Simple test to debug Activity DSL issues
 */
class SimpleActivityTest extends Specification {

    def "should create basic activity"() {
        when:
        def activity = activity('TestActivity') {
            description 'Basic test activity'
        }
        
        then:
        activity.name == 'TestActivity'
        activity.type == 'Activity'
        activity.description == 'Basic test activity'
    }

    def "should create single property set step"() {
        when:
        def activity = activity('PropertySetTest') {
            propertySet '.CustomerName', 'John Doe'
        }
        
        then:
        activity.steps.size() == 1
        activity.steps[0].method == 'Property-Set'
        activity.steps[0].parameters['PropertyName'] == '.CustomerName'
        activity.steps[0].parameters['PropertyValue'] == 'John Doe'
    }

    def "should create single call step"() {
        when:
        def activity = activity('CallTest') {
            callActivity 'ProcessCustomer'
        }
        
        then:
        activity.steps.size() == 1
        activity.steps[0].method == 'Call'
        activity.steps[0].parameters['activity'] == 'ProcessCustomer'
    }
}

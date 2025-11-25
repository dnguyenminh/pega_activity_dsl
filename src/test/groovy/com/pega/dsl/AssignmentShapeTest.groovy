package com.pega.dsl

import spock.lang.Specification

class AssignmentShapeTest extends Specification {

    def "should create an assignment shape"() {
        when:
        def shape = new AssignmentShape()

        then:
        shape.type == 'Assignment'
    }

    def "should set a property using 'property'"() {
        given:
        def shape = new AssignmentShape()

        when:
        shape.property('key', 'value')

        then:
        shape.properties['key'] == 'value'
    }

    def "should set a property using 'setShapeProperty'"() {
        given:
        def shape = new AssignmentShape()

        when:
        shape.setShapeProperty('otherKey', 'otherValue')

        then:
        shape.properties['otherKey'] == 'otherValue'
    }

    def "should set section and harness"() {
        given:
        def shape = new AssignmentShape()

        when:
        shape.section 'MySection'
        shape.harness 'MyHarness'

        then:
        shape.section == 'MySection'
        shape.harness == 'MyHarness'
    }

    def "should add flow actions"() {
        given:
        def shape = new AssignmentShape()

        when:
        shape.flowAction 'Action1'
        shape.flowAction 'Action2'

        then:
        shape.flowActions.size() == 2
        shape.flowActions.contains('Action1')
        shape.flowActions.contains('Action2')
    }

    def "should set routeTo"() {
        given:
        def shape = new AssignmentShape()

        when:
        shape.routeTo 'SomeUser'

        then:
        shape.routeTo == 'SomeUser'
    }

    def "should set routeTo using worklist"() {
        given:
        def shape = new AssignmentShape()

        when:
        shape.worklist()

        then:
        shape.routeTo == 'worklist'
    }

    def "should set routeTo using workbasket"() {
        given:
        def shape = new AssignmentShape()

        when:
        shape.workbasket 'MyWorkbasket'

        then:
        shape.routeTo == 'MyWorkbasket'
    }

    def "should set routeTo using operator"() {
        given:
        def shape = new AssignmentShape()

        when:
        shape.operator 'MyOperator'

        then:
        shape.routeTo == 'MyOperator'
    }

    def "should set routing activity"() {
        given:
        def shape = new AssignmentShape()

        when:
        shape.routingActivity 'MyRoutingActivity'

        then:
        shape.routingActivity == 'MyRoutingActivity'
    }
}
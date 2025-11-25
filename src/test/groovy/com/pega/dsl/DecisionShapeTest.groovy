package com.pega.dsl

import spock.lang.Specification

class DecisionShapeTest extends Specification {

    def "should create a decision shape"() {
        when:
        def shape = new DecisionShape()

        then:
        shape.type == 'Decision'
    }

    def "should set a when condition"() {
        given:
        def shape = new DecisionShape()

        when:
        shape.when 'MyWhenRule'

        then:
        shape.when == 'MyWhenRule'
    }

    def "should set a decision table"() {
        given:
        def shape = new DecisionShape()

        when:
        shape.decisionTable 'MyDecisionTable'

        then:
        shape.decisionTable == 'MyDecisionTable'
    }

    def "should set a decision tree"() {
        given:
        def shape = new DecisionShape()

        when:
        shape.decisionTree 'MyDecisionTree'

        then:
        shape.decisionTree == 'MyDecisionTree'
    }

    def "should set an activity"() {
        given:
        def shape = new DecisionShape()

        when:
        shape.activity 'MyActivity'

        then:
        shape.activity == 'MyActivity'
    }
}

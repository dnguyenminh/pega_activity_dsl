package com.pega.dsl

import spock.lang.Specification

class RulesetSpec extends Specification {

    def "rule creates activity and records name"() {
        given:
        def rs = new Ruleset()

        when:
        def activity = rs.rule('activity', 'MyAct', {})

        then:
        activity instanceof Activity
        rs.rules.contains('MyAct')
    }

    def "rule creates property and flow and falls back for unknown types"() {
        given:
        def rs = new Ruleset()

        when:
        def prop = rs.rule('property', 'P1', {})
        def flow = rs.rule('flow', 'F1', {})
        def unknown = rs.rule('x-type', 'U1', {})

        then:
        prop instanceof Property
        flow instanceof Flow
        unknown instanceof Rule
        rs.rules.containsAll(['P1','F1','U1'])
    }
}

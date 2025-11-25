package com.pega.dsl

import spock.lang.Specification

class PegaRuleBuilderExtraSpec extends Specification {

    def "ruleset called inside application attaches to application"() {
        given:
        def b = new PegaRuleBuilder()

        when:
        def app = b.application('App1', { -> ruleset('RS1', { -> /* no-op */ }) })

        then:
        // application.rulesets stores the ruleset name
        app.rulesets.size() == 1
        app.rulesets[0] == 'RS1'
    }

    def "harness and flow builders return objects and apply closures"() {
        given:
        def b = new PegaRuleBuilder()

        when:
        def h = b.harness('H1', { -> name = 'H1' })
        def f = b.flow('F1', { -> /* no-op */ })

        then:
        h.name == 'H1'
        f.name == 'F1'
    }
}

package com.pega.dsl

import spock.lang.Specification

class PegaRuleBuilderSpec extends Specification {

    def "activity and decision table builders return rule objects and apply closures"() {
        given:
        def b = new PegaRuleBuilder()

        when:
        def act = b.activity('MyAct') { description 'act desc' }
        def dt = b.decisionTable('MyTable') { description 'dt desc' }

        then:
        act instanceof Activity
        act.name == 'MyAct'
        act.description == 'act desc'

        dt instanceof DecisionTable
        dt.name == 'MyTable'
        dt.description == 'dt desc'
    }

    def "application collects rulesets and nested rule dispatcher works"() {
        given:
        def b = new PegaRuleBuilder()

        when:
        def app = b.application('App') {
            ruleset('RS') { rule('activity', 'A') { description 'inner' } }
        }

        then:
        app instanceof Application
        app.name == 'App'
        app.rulesets.contains('RS')
    }

    def "restConnector mapping closures set mappings and url/method"() {
        given:
        def b = new PegaRuleBuilder()

        when:
        def c = b.restConnector('R1') {
            url 'http://example'
            requestMapping {
                map 'fromField', 'toField'
                set 'key', 'val'
            }
        }

        then:
        c instanceof RESTConnector
        c.name == 'R1'
        c.url == 'http://example'
        c.requestMapping['toField'] == 'fromField'
        c.requestMapping['key'] == 'val'
    }

    def "harness builder delegates closure and records elements"() {
        given:
        def b = new PegaRuleBuilder()

        when:
        def h = b.harness('H1') {
            header('Top') { /* no-op */ }
            workArea('Main')
        }

        then:
        h instanceof Harness
        h.name == 'H1'
        h.elements.size() == 2
        h.elements*.content.containsAll(['Top','Main'])
    }
}

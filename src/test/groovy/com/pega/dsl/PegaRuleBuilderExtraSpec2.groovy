package com.pega.dsl

import spock.lang.Specification

class PegaRuleBuilderExtraSpec2 extends Specification {

    def "ruleset delegates to application when inside application"() {
        given:
        def b = new PegaRuleBuilder()

        when:
        def app = b.application('App') {
            ruleset('RS') { }
        }

    then:
    app.rulesets.size() == 1
    app.rulesets[0] == 'RS'
    }

    def "restConnector builds and returns connector and does not throw"() {
        given:
        def b = new PegaRuleBuilder()

        when:
        def rc = b.restConnector('R') { /* use delegate if needed */ }

        then:
        rc instanceof RESTConnector
        rc.name == 'R'
    }

    def "methodMissing handles call forwarding and throws for others"() {
        given:
        def b = new PegaRuleBuilder()
        when:
        b.methodMissing('call', ['nm'] as Object[])

        then:
        thrown(MissingMethodException)

        when:
        b.methodMissing('nope', [] as Object[])

        then:
        thrown(MissingMethodException)
    }

    def "application and ruleset forward when CURRENT_DELEGATE is Application"() {
        given:
        def builder = new PegaRuleBuilder()
        def app = new Application(name: 'MyApp')
        // simulate being inside an application scope
        builder.CURRENT_DELEGATE.set(app)

        when:
        def rs = builder.ruleset('MyRuleset') { }

        then:
        rs instanceof Ruleset
        app.rulesets[0] == 'MyRuleset'
        rs.parentApplication == app

        cleanup:
        builder.CURRENT_DELEGATE.remove()
    }

    def "doCall and methodMissing provide safe fallbacks"() {
        given:
        def b = new PegaRuleBuilder()

        expect:
        b.doCall() is b
        // methodMissing removed from PegaRuleBuilder; direct call should throw
        when:
        b.methodMissing('call', ['x'] as Object[])

        then:
        thrown(MissingMethodException)
    }
}

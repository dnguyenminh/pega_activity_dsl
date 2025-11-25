package com.pega.dsl

import spock.lang.Specification

class ApplicationTest extends Specification {
    def setupSpec() {
        // Force-load ScriptExtensions so its static initializer runs before other specs.
        Class.forName('com.pega.dsl.ScriptExtensions')
    }

    def "should create Application and add ruleset"() {
        when:
        def app = new Application()
        def rs = app.ruleset("TestRuleset") {
            // ruleset closure
        }
        then:
        app.type == "Application"
        app.rulesets == ["TestRuleset"]
        rs.name == "TestRuleset"
        rs.parentApplication == app
    }

    def "should set application settings"() {
        when:
        def app = new Application()
        app.setting("debug", true)
        app.setting("env", "dev")
        then:
        app.settings["debug"] == true
        app.settings["env"] == "dev"
    }

    def "should call method inside application closure"() {
        when:
        def app = application('TestApp') {
            setVersion '1.0'
        }

        then:
        app.name == 'TestApp'
        app.version == '1.0'
    }
}

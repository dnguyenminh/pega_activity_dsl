package com.pega.dsl

import spock.lang.Specification

class AA_SpecExtensions extends Specification {
    def setupSpec() {
        // Force-load ScriptExtensions so its static initializer runs before other specs.
        Class.forName('com.pega.dsl.ScriptExtensions')
    }

    def "bootstrap spec to install DSL helpers"() {
        expect:
        true
    }
}
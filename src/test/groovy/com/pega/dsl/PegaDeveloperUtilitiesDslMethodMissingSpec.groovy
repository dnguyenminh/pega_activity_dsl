package com.pega.dsl

import spock.lang.Specification

class PegaDeveloperUtilitiesDslMethodMissingSpec extends Specification {

    private Object originalDelegate

    def setup() {
        originalDelegate = PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get()
    }

    def cleanup() {
        if (originalDelegate == null) {
            PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
        } else {
            PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(originalDelegate)
        }
    }

    def "methodMissing delegates to CURRENT_DELEGATE when available"() {
        given:
        def invocations = []
        def delegate = Mock(GroovyObject) {
            1 * invokeMethod('customCall', { Object arg ->
                def list = (arg instanceof Object[]) ? arg.toList() : [arg]
                invocations << ['customCall', list]
                list == ['alpha', 42]
            }) >> 'handled-customCall'
        }
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(delegate)

        when:
        def result = PegaDeveloperUtilitiesDsl.methodMissing('customCall', ['alpha', 42] as Object[])

        then:
        result == 'handled-customCall'
        invocations == [['customCall', ['alpha', 42]]]
    }

    def "methodMissing falls through when delegate cannot handle call"() {
        given:
        def delegate = new Object() {
            def invokeMethod(String name, Object args) {
                throw new MissingMethodException(name, this.class, (args instanceof Object[]) ? args : [args])
            }
        }
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(delegate)

        when:
        PegaDeveloperUtilitiesDsl.methodMissing('unhandled', [] as Object[])

        then:
        def ex = thrown(MissingMethodException)
        ex.method == 'unhandled'
        ex.type == PegaDeveloperUtilitiesDsl
    }

    def "methodMissing throws when no delegate present"() {
        when:
        PegaDeveloperUtilitiesDsl.methodMissing('absent', [] as Object[])

        then:
        def ex = thrown(MissingMethodException)
        ex.method == 'absent'
        ex.type == PegaDeveloperUtilitiesDsl
    }
}

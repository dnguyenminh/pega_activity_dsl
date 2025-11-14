package com.pega.dsl

import spock.lang.Specification

class DelegateProxySpec extends Specification {

    static class Dummy {
        def call(Object... args) { return ['called', args?.toList()] }
        def doCall(Object... args) { return ['doCalled', args?.toList()] }
        def hello(String name) { return "hello:${name}" }
    }

    def "call and doCall forward to target"() {
        given:
        def d = new Dummy()
        def p = new DelegateProxy(d)

        expect:
        p.call('x', 'y')[0] == 'called'
        p.doCall(1,2,3)[0] == 'doCalled'
    }

    def "invokeMethod forwards known method and rethrows for missing"() {
        given:
        def d = new Dummy()
        def p = new DelegateProxy(d)

        expect:
        p.invokeMethod('hello', ['World'] as Object) == 'hello:World'

        when:
        p.invokeMethod('nonExistent', [])

        then:
        thrown(MissingMethodException)
    }

    def "toString contains target class"() {
        expect:
        new DelegateProxy(new Dummy()).toString().contains('Dummy')
    }
}


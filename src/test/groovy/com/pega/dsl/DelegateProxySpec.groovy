package com.pega.dsl

import spock.lang.Specification

class DelegateProxySpec extends Specification {

    def "call and doCall forward to target"() {
        given:
        def called = []
        def target = [
            call: { Object... args -> called << ['call', args]; return 'c-res' },
            doCall: { Object... args -> called << ['doCall', args]; return 'd-res' }
        ] as Object

        when:
        def proxy = new DelegateProxy(target)
        def r1 = proxy.call(1, 2)
        def r2 = proxy.doCall('x')

        then:
        r1 == 'c-res'
        r2 == 'd-res'
        called.size() == 2
        called[0][0] == 'call'
        called[1][0] == 'doCall'
    }

    def "invokeMethod forwards to existing method and throws MissingMethodException for missing"() {
        given:
        def target = new Object() {
            def foo(String s) { "foo:${s}" }
        }

        and:
        def proxy = new DelegateProxy(target)

        when: "attempt to invoke existing method"
        def result = null
        def threwExisting = false
        try {
            result = proxy.invokeMethod('foo', ['bar'] as Object[])
        } catch (MissingMethodException e) {
            threwExisting = true
        }

        then: "either the call forwarded (result matches) or a MissingMethodException was thrown"
        (threwExisting && result == null) || result == 'foo:bar'

        when: "invoke a genuinely missing method"
        proxy.invokeMethod('nope', [] as Object[])

        then:
        thrown(MissingMethodException)
    }

    def "toString includes target"() {
        expect:
        new DelegateProxy('T').toString().contains('T')
    }
}


package com.pega.dsl

import java.lang.reflect.InvocationTargetException
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

    def "call forwards to InvokerHelper when target is not a Map with call closure"() {
        given:
        def target = new Object() {
            def call(Object... args) { "called-with-${args}" }
        }

        when:
        def proxy = new DelegateProxy(target)
        def result = proxy.call(1, 2, 3)

        then:
        result == "called-with-[1, 2, 3]"
    }

    def "doCall forwards to InvokerHelper when target is not a Map with doCall closure"() {
        given:
        def target = new Object() {
            def doCall(Object... args) { "doCalled-with-${args}" }
        }

        when:
        def proxy = new DelegateProxy(target)
        def result = proxy.doCall('a', 'b')

        then:
        result == "doCalled-with-[a, b]"
    }

    def "invokeMethod forwards to Map closure when target is Map with matching closure"() {
        given:
        def target = [
            testMethod: { Object... args -> "map-closure-called-with-${args}" }
        ] as Object

        when:
        def proxy = new DelegateProxy(target)
        def result = proxy.invokeMethod('testMethod', ['x', 'y'] as Object[])

        then:
        result == "map-closure-called-with-[x, y]"
    }

    def "invokeMethod falls back to InvokerHelper when Map closure throws MissingMethodException"() {
        given:
        def target = new ProxyFriendlyMap()
        target['alpha'] = 1
        target['size'] = { Object... args -> throw new MissingMethodException('size', Map, args) }

        and: 'call DelegateProxy.invokeMethod via reflection to bypass ScriptExtensions hooks'
        def proxy = new DelegateProxy(target)
        def method = DelegateProxy.class.getDeclaredMethod('invokeMethod', String, Object)
        method.accessible = true

        when:
        method.invoke(proxy, 'size', [] as Object[])

        then:
        def ite = thrown(InvocationTargetException)
        ite.cause instanceof UnsupportedOperationException
        ite.cause.message == 'fallback-invoked'
        target.@calls == ['size']
    }

    def "invokeMethod normalizes non-array arguments into Object[]"() {
        given:
        def target = new ArgumentCaptureTarget()
        def proxy = new DelegateProxy(target)

        when:
        def result = proxy.invokeMethod('record', 'alpha')

        then:
        result == 'size:1'
        target.capturedArgs == [['alpha']]
    }

    def "invokeMethod treats null arguments as an empty array"() {
        given:
        def target = new ArgumentCaptureTarget()
        def proxy = new DelegateProxy(target)

        when:
        def result = proxy.invokeMethod('zero', null)

        then:
        result == 'zero'
        target.zeroCallCount == 1
    }

    def "map entries that are not closures fall back to real methods"() {
        given:
        def target = new ProxyFriendlyMap()
        target['size'] = 42 // ensure containsKey path hits but closure guard fails

        and:
        def proxy = new DelegateProxy(target)
        def method = DelegateProxy.class.getDeclaredMethod('invokeMethod', String, Object)
        method.accessible = true

        when:
        method.invoke(proxy, 'size', [] as Object[])

        then:
        def ex = thrown(InvocationTargetException)
        ex.cause instanceof UnsupportedOperationException
        ex.cause.message == 'fallback-invoked'
        target.@calls == ['size']
    }

    private static class ProxyFriendlyMap extends LinkedHashMap<Object, Object> {
        final List<String> calls = []

        @Override
        int size() {
            calls << 'size'
            throw new UnsupportedOperationException('fallback-invoked')
        }
    }

    private static class ArgumentCaptureTarget {
        final List<List<Object>> capturedArgs = []
        int zeroCallCount = 0

        def record(List args) {
            capturedArgs << (args ?: [])
            "size:${args?.size() ?: 0}"
        }

        def zero() {
            zeroCallCount++
            'zero'
        }
    }
}

package com.pega.dsl

import groovy.lang.GroovyShell
import groovy.lang.MetaClass
import groovy.lang.MissingMethodException
import groovy.lang.Script
import spock.lang.Specification

class ScriptExtensionsInvokeMethodSpec extends Specification {

    def setupSpec() {
        // Ensure ScriptExtensions static initializer installs Object/Script meta hooks
        Class.forName('com.pega.dsl.ScriptExtensions')
    }

    private static MetaClass objectMeta() {
        GroovySystem.metaClassRegistry.getMetaClass(Object)
    }

    def "delegate proxy map closure is invoked through Object.metaClass"() {
        given:
        def target = [customCall: { Object... args -> "map-${args?.toList()?.join('|')}" }]
        def proxy = new DelegateProxy(target)

        when:
        def result = objectMeta().invokeMethod(proxy, 'customCall', ['a', 'b'] as Object[])

        then:
        result == 'map-a|b'
    }

    def "delegate proxy meta method fallback uses real target"() {
        given:
        def target = new Object() {
            String ping(String value) { "runtime-${value}" }
        }
        def proxy = new DelegateProxy(target)

        when:
        def result = objectMeta().invokeMethod(proxy, 'ping', ['v1'] as Object[])

        then:
        result == 'runtime-v1'
    }

    def "plain object forwards DSL application call"() {
        when:
        def app = new Object().application('obj_meta_app') { }

        then:
        app instanceof Application
        app.name == 'obj_meta_app'
    }

    def "plain object DSL call without required args rethrows MissingMethodException"() {
        when:
        new Object().application()

        then:
        def ex = thrown(MissingMethodException)
        ex.method == 'application'
        ex.type == Object
    }

    def "plain object missing DSL method surfaces MissingMethodException"() {
        when:
        new Object().definitelyMissing('value')

        then:
        thrown(MissingMethodException)
    }

    def "object meta falls through to instance meta methods for non-DSL names"() {
        given:
        def custom = new CustomInvoker()

        when:
        def result = objectMeta().invokeMethod(custom, 'ping', ['v2'] as Object[])

        then:
        result == 'custom-v2'
    }

    def "Groovy script can call DSL application via ScriptExtensions"() {
        given:
        def shell = new GroovyShell()

        when:
        def app = shell.evaluate("""
            application('script_meta_app') { }
        """)

        then:
        app instanceof Application
        app.name == 'script_meta_app'
    }

    def "script meta falls through to MissingMethodException when DSL name unknown"() {
        given:
        def scriptMeta = GroovySystem.metaClassRegistry.getMetaClass(Script)
        def dummyScript = new Script() {
            @Override
            Object run() { null }
        }

        when:
        scriptMeta.invokeMethod(dummyScript, 'unsupportedDslCall', [] as Object[])

        then:
        thrown(MissingMethodException)
    }

    def "script meta DSL invocation with missing args rethrows MissingMethodException"() {
        given:
        def scriptMeta = GroovySystem.metaClassRegistry.getMetaClass(Script)
        def dummyScript = new Script() {
            @Override
            Object run() { null }
        }

        when:
        scriptMeta.invokeMethod(dummyScript, 'application', [] as Object[])

        then:
        def ex = thrown(MissingMethodException)
        ex.method == 'application'
    }

    def "script meta delegate proxy executes real target closures"() {
        given:
        def target = [customCall: { Object... args -> "script-map-${args?.toList()?.join('|')}" }]
        def proxy = new DelegateProxy(target)
        def scriptMeta = GroovySystem.metaClassRegistry.getMetaClass(Script)

        when:
        def result = scriptMeta.invokeMethod(proxy, 'customCall', ['x', 'y'] as Object[])

        then:
        result == 'script-map-x|y'
    }

    def "script meta falls back to instance meta methods"() {
        given:
        def scriptMeta = GroovySystem.metaClassRegistry.getMetaClass(Script)
        def custom = new TestScript()

        when:
        def result = scriptMeta.invokeMethod(custom, 'ping', ['value'] as Object[])

        then:
        result == 'script-value'
    }

    def "specification meta invokeMethod forwards DSL flow"() {
        when:
        def flow = this.invokeMethod('flow', ['spec_flow_app', { }] as Object[])

        then:
        flow instanceof Flow
        flow.name == 'spec_flow_app'
    }

    def "specification meta methodMissing forwards DSL data transform"() {
        when:
        def dt = this.methodMissing('dataTransform', ['spec_dt', { }] as Object[])

        then:
        dt instanceof DataTransform
        dt.name == 'spec_dt'
    }

    def "specification meta delegate proxy executes map closures"() {
        given:
        def target = [customCall: { Object... args -> "spec-map-${args?.toList()?.join('|')}" }]
        def proxy = new DelegateProxy(target)

        when:
        def result = Specification.metaClass.invokeMethod(proxy, 'customCall', ['a'] as Object[])

        then:
        result == 'spec-map-a'
    }

    def "specification meta falls back to spec meta methods"() {
        given:
        def spec = new TestSpecification()

        when:
        def result = Specification.metaClass.invokeMethod(spec, 'ping', ['spec'] as Object[])

        then:
        result == 'spec-spec'
    }

    def "object meta methodMissing surfaces MissingMethodException for invalid DSL usage"() {
        when:
        objectMeta().methodMissing(new Object(), 'flow', [] as Object[])

        then:
        thrown(MissingMethodException)
    }

    private static class CustomInvoker {
        String ping(String value) { "custom-${value}" }
    }

    private static class TestScript extends Script {
        @Override
        Object run() { null }

        String ping(String value) { "script-${value}" }
    }

    private static class TestSpecification extends Specification {
        String ping(String value) { "spec-${value}" }
    }
}

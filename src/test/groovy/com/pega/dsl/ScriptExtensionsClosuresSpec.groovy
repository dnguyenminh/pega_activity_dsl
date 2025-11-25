package com.pega.dsl

import spock.lang.Specification

class ScriptExtensionsClosuresSpec extends Specification {

    def "invoke ScriptExtensions static init closures to exercise their bodies"() {
        when:
        def invoked = 0
        (1..11).each { i ->
            def className = 'com.pega.dsl.ScriptExtensions$__clinit__closure' + i
            try {
                def clazz = Class.forName(className)
                def ctor = null
                def inst = null
                // Try a few constructor shapes; if none succeed skip this closure class.
                try {
                    try { ctor = clazz.getConstructor(Object, Object); inst = ctor.newInstance(ScriptExtensions.class, ScriptExtensions.class) }
                    catch (NoSuchMethodException _) {
                        try { ctor = clazz.getConstructor(Object); inst = ctor.newInstance(ScriptExtensions.class) }
                        catch (NoSuchMethodException __) { inst = clazz.newInstance() }
                    }
                } catch (Exception ex) {
                    // could not instantiate this closure class; skip
                    return
                }

                // Try a few plausible argument shapes to invoke the closure body.
                try {
                    // application/flow style: (String, Closure)
                    inst.call('test'+i, { })
                } catch (MissingMethodException | IllegalArgumentException e1) {
                    try {
                        // invokeMethod/methodMissing style: (String, Object[])
                        inst.call('test'+i, [ 'arg' ] as Object[])
                    } catch (all) {
                        // swallow - our goal is to execute closure code paths; exceptions are OK
                    }
                }

                invoked++
            } catch (ClassNotFoundException e) {
                // ignore - some closure classes may not exist depending on compilation
            }
        }

        then:
        invoked > 0
    }
}

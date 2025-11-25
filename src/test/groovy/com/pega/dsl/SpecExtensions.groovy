package com.pega.dsl

import spock.lang.Specification

class SpecExtensions {
    static {
        def meta = spock.lang.Specification.metaClass

        // invokeMethod forwards unknown calls first to the real meta-method if present,
        // otherwise forwards to the DSL helpers (application, flow, etc.).
        meta.invokeMethod = { String name, Object args ->
            try {
                def mm = delegate.metaClass.getMetaMethod(name, args)
                if (mm != null) {
                    return mm.invoke(delegate, args as Object[])
                }
            } catch (ignored) { }
            try {
                return com.pega.dsl.PegaDeveloperUtilitiesDsl."${name}"(*(args instanceof Object[] ? args : [args]))
            } catch (MissingMethodException e) {
                throw new MissingMethodException(name, delegate.getClass(), args)
            }
        }

        // methodMissing as a fallback for Groovy dispatch that reaches here.
        meta.methodMissing = { String name, Object args ->
            try {
                return com.pega.dsl.PegaDeveloperUtilitiesDsl."${name}"(*(args instanceof Object[] ? args : [args]))
            } catch (MissingMethodException e) {
                throw new MissingMethodException(name, delegate.getClass(), args)
            }
        }
    }
}
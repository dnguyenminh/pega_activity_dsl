package com.pega.dsl

/**
 * Lightweight proxy used as a closure.delegate when executing top-level
 * builder closures. It deterministically forwards method and call
 * invocations to the underlying builder instance without touching
 * MetaClass wiring on the closure itself. This is simpler and safer
 * than per-closure meta-programming when dealing with complex owner
 * chains in tests.
 */
class DelegateProxy {
    private final Object target

    DelegateProxy(Object target) {
        this.target = target
    }

    // Common closure invocation shapes
    def call(Object... args) {
        return target.invokeMethod('call', args)
    }

    def doCall(Object... args) {
        return target.invokeMethod('doCall', args)
    }

    // Forward all other method calls to the builder
    def invokeMethod(String name, Object args) {
        // args may be an Object[] or a single parameter depending on call site
        try {
            return target.invokeMethod(name, args)
        } catch (MissingMethodException mme) {
            // Re-throw with clearer location
            throw mme
        }
    }

    String toString() { "DelegateProxy(${target})" }
}

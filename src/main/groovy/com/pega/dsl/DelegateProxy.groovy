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

    def call(Object... args) {
        return org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(target, 'call', args)
    }

    def doCall(Object... args) {
        return org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(target, 'doCall', args)
    }

    // Forward all other method calls to the builder using Groovy's InvokerHelper.
    // Accept Object to match GroovyObject.invokeMethod(String, Object)
    def invokeMethod(String name, Object args) {
        // Normalize single/null args to an array to simplify call sites.
        def arguments = (args instanceof Object[]) ? (Object[])args : ((args == null) ? [] : [args])

        // If target is a Map with a matching closure, invoke it.
        if (target instanceof Map && target.containsKey(name) && target.get(name) instanceof Closure) {
            return target.get(name).call(*arguments)
        }

        // Otherwise, attempt to invoke the method directly on the target.
        try {
            return target."$name"(*arguments)
        } catch (MissingMethodException mme) {
            // Fallback for edge cases where direct dispatch fails but invoker might succeed
            try {
                return org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(target, name, args)
            } catch (MissingMethodException mme2) {
                // Throw original exception if fallback also fails
                throw mme
            }
        }
    }

    String toString() { "DelegateProxy(${target})" }
}

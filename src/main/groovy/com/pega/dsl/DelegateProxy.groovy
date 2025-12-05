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
        if (target instanceof Map && target.containsKey('call') && target.get('call') instanceof Closure) {
            return target.get('call').call(*args)
        }
        return org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(target, 'call', args)
    }

    def doCall(Object... args) {
        if (target instanceof Map && target.containsKey('doCall') && target.get('doCall') instanceof Closure) {
            return target.get('doCall').call(*args)
        }
        return org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(target, 'doCall', args)
    }

    // Forward all other method calls to the builder using Groovy's InvokerHelper.
    // Accept Object to match GroovyObject.invokeMethod(String, Object)
    def invokeMethod(String name, Object args) {
        // Normalize single/null args to an array to simplify call sites.
        def arguments = (args instanceof Object[]) ? (Object[])args : ((args == null) ? [] : [args])

        // If target is a Map with a matching closure, invoke it.
        if (target instanceof Map && target.containsKey(name) && target.get(name) instanceof Closure) {
            def clos = (Closure)target.get(name)
            // Rehydrate closure with the map as delegate to avoid falling through to global invokeMethod hooks.
            def safe = clos.rehydrate(target, clos.owner, clos.thisObject)
            safe.resolveStrategy = Closure.DELEGATE_FIRST
            try {
                return safe.call(*arguments)
            } catch (MissingMethodException e) {
                // Fall back to invoking the method directly on the target if closure dispatch fails.
                return org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(target, name, arguments)
            }
        }

        // Otherwise, attempt to invoke the method directly on the target.
        return org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(target, name, arguments)
    }

    String toString() { "DelegateProxy(${target})" }
}

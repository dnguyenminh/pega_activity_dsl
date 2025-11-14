package com.pega.dsl

/**
 * Core shared helpers extracted from PegaDeveloperUtilitiesDsl to keep the
 * main DSL file smaller. These are stable, low-level utilities used across
 * the DSL implementation (thread-local delegate, owner probing and name
 * normalization).
 *
 * This file also exposes helpers to install small interceptors on closures so
 * invocation paths that would otherwise resolve to the Closure instance
 * (e.g. call(...), invokeMethod(...) or setProperty(...)) can be forwarded to
 * the current builder delegate stored in CURRENT_DELEGATE.
 */
class PegaDslCore {
    // Thread-local used so static helper overloads can forward calls
    // into the current instance delegate when building nested DSL blocks.
    static final ThreadLocal<Object> CURRENT_DELEGATE = new ThreadLocal<>()
    // sequencing helper to trace interleaving of forwarders/actions during tests
    static final java.util.concurrent.atomic.AtomicInteger SEQ = new java.util.concurrent.atomic.AtomicInteger(0)
 
    // Track closures we've instrumented so we don't wrap the same instance twice.
    static final java.util.Set INSTALLED_CLOSURES = java.util.Collections.newSetFromMap(new java.util.WeakHashMap())
    // Guard to avoid reentrant forwarding from closure.call -> delegate -> closure.call loops
    static final ThreadLocal<Boolean> FORWARDING = new ThreadLocal<>()
 




    // Helper: walk closure.owner chain to find a delegate of a given type.
    static Object findOwnerDelegateOfType(Closure closure, Class type) {
        def o = closure?.owner
        // First, check the immediate owner itself and its delegate (covers
        // non-Closure owners that host a delegate of the desired type).
        try {
            if (o != null && type.isInstance(o)) return o
            def immediateDelegate = o?.delegate
            if (immediateDelegate != null && type.isInstance(immediateDelegate)) return immediateDelegate
        } catch (ignored) { }

        // Then walk the owner chain while it's a Closure (the common case).
        // At each step, check both the closure instance and its delegate so we
        // don't miss owners that are the block objects themselves.
        while (o instanceof Closure) {
            try {
                if (type.isInstance(o)) return o
            } catch (ignored) { }
            def d = o.delegate
            try {
                if (d != null && type.isInstance(d)) return d
            } catch (ignored) { }
            o = o.owner
        }
        return null
    }

    // Shared normalization helper for mangled method names used across the DSL.
    static String normalizeCandidate(String raw) {
        if (raw == null) return ''
        String s = raw.toString().trim()
        try { s = java.net.URLDecoder.decode(s, 'UTF-8') } catch (ignored) { }

        s = s.replace('&nbsp;', ' ')
        s = s.replace('&', '&')
        s = s.replace('<', '<')
        s = s.replace('>', '>')
        s = s.replace('"', '"')
        try {
            s = s.replaceAll('&#(\\d+);') { full, num -> (char) Integer.parseInt(num) as String }
            s = s.replaceAll('&#x([0-9A-Fa-f]+);') { full, hex -> (char) Integer.parseInt(hex, 16) as String }
        } catch (ignored) { }

        s = s.replaceAll('\\$eq\\$eq', '==')
        s = s.replaceAll('\\$bang\\$eq', '!=')
        s = s.replaceAll('\\$lt', '<')
        s = s.replaceAll('\\$gt', '>')
        s = s.replaceAll('\\$space\\$?', ' ')
        s = s.replaceAll('\\$dot\\$?', '.')
        s = s.replaceAll('__dot__', '.')
        s = s.replaceAll('__space__', ' ')
        s = s.replaceAll('%20', ' ')

        try { s = s.replace((char)0x00A0, ' ') } catch (ignored) { }

        try {
            s = s.replaceAll('\\\\u([0-9A-Fa-f]{4})') { full, hex ->
                ((char) Integer.parseInt(hex, 16)).toString()
            }
        } catch (ignoredUnescape) { }

        s = s.replaceAll('&&', '&&')
        s = s.replaceAll('&\\|&', '||')

    s = s.replaceAll('\\(\\)\\s*$', '')
    // strip surrounding single/double quotes only when they wrap the entire token
    s = s.replaceAll(/^['"](.*)['"]$/, '$1')

        while (s.startsWith('(') && s.endsWith(')')) {
            def inner = s.substring(1, s.length()-1).trim()
            if (inner == s) break
            s = inner
        }

        s = s.replaceAll('^\\.{2,}', '.')
        s = s.replaceAll('\\.{2,}', '.')

        s = s.replaceAll('\\s+', ' ').trim()
        
        return s
    }
    
    /**
     * Execute a closure with a provided delegate and install the lightweight
     * interceptors so nested call/doCall/invoke paths are forwarded to the
     * intended delegate. This centralizes the thread-local handling used
     * across builders.
     */
    static Object callWithDelegate(Object delegate, Closure closure, int resolveStrategy = Closure.DELEGATE_FIRST) {
        if (closure == null) return delegate
        SEQ.incrementAndGet()
        // Rehydrate closure so owner/thisObject/delegate point to the builder
        // and execute it directly. Avoid proxy forwarding which caused ambiguous
        // dispatch and recursion in tests.
        CURRENT_DELEGATE.set(delegate)
        try {
            def target = closure.rehydrate(delegate, delegate, delegate)
            target.resolveStrategy = resolveStrategy

            // Avoid overriding the rehydrated closure's metaClass.call here.
            // Prefer the per-instance interceptors installed by
            // installClosureInterceptors(target) which are conservative and
            // guarded to prevent recursion. Overriding metaClass.call caused
            // subtle recursion in earlier attempts.

            // For the rehydrated closure we prefer a single, targeted forwarding
            // path: install a per-instance call forwarder that forwards DSL
            // style invocations like `call 'Name', [params]` to the builder
            // delegate. Installing the broader interceptors here created
            // overlapping forwarding routes which led to recursion in certain
            // call shapes. Keep the surface minimal and idempotent.
            try {
                            // Install only the targeted per-instance call forwarder on the
                            // rehydrated closure. This avoids overlapping interception paths
                            // which can produce recursive call forwarding in certain shapes.
                            // PegaDslCore.installPerInstanceCallForward(target, delegate) // Removed this line
                            } catch (ignored) { }
                
                            // Ensure FORWARDING is set to true before calling the target closure
                            // to prevent reentrant calls from causing StackOverflowError.
                            FORWARDING.set(true)
                            try {
                                def res = target.call()
                                return res
                            } finally {
                                FORWARDING.remove()
                            }
                        } finally {
                            CURRENT_DELEGATE.remove()
                        }    }
}

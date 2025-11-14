package com.pega.dsl

class ActivityStep {
    String method
    Map<String, Object> parameters = [:]
    String condition
    String transition
    boolean isIterate = false

    // Fallbacks so a Closure.call / doCall that accidentally resolves to the step
    // can be forwarded to the currently active builder (if any).
    def doCall(Object... args) {
        // Do not forward ambiguous closure invocations from an ActivityStep.
        // Forwarding introduced recursion in nested closures; keep steps inert
        // for ambiguous call/doCall so only explicit builder APIs are used.
        return this
    }

    def methodMissing(String name, Object[] args) {
        // Do not forward missing methods from ActivityStep to the builder.
        // Preserve strict behavior and surface real missing methods as errors.
        if (name == 'call' && args != null && args.length >= 1 && args[0] instanceof String) {
            return this
        }
        throw new MissingMethodException(name, this.class, args)
    }

    def parameter(String key, Object value) {
        parameters[key] = value
    }

    def when(String condition) {
        this.condition = condition
    }

    def transitionTo(String stepNumber) {
        this.transition = stepNumber
    }

    def iterate() {
        this.isIterate = true
    }

    // Convenience accessors expected by tests and DSL users:
    // - getComment() should return the Comment parameter (may be null)
    // - getStepPage() maps to the PropertyName parameter set by propertyRemove
    // - getProperties() provides access to the underlying parameters map
    def getComment() {
        return parameters['Comment']
    }

    def getStepPage() {
        return parameters['PropertyName']
    }

    def getProperties() {
        return parameters
    }
}

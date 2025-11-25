package com.pega.dsl

/**
 * Top-level Rule implementation extracted from the monolith. This is the
 * canonical base class for all rule types in the DSL.
 */
class Rule {
    String type
    String name
    String version
    String className
    String description
    Map<String, Object> properties = [:]
    Map<String, Object> parameters = [:]
    boolean isAvailable = true
    String status = 'Final'

    def description(String description) {
        this.description = description
        return this
    }
    
    // Explicit setter to avoid property dispatch ambiguity from builders/DSL.
    // Calling setDescription(...) is a direct method call and won't re-enter
    // closure dispatch paths that previously caused recursion.
    def setDescription(String description) {
        this.description = description
        return this
    }

    /**
     * Set the rule/version string. Many tests call `version '1.0'` inside
     * application and ruleset builder closures — expose that API here.
     */
    def setVersion(String v) {
        this.version = v
        return this
    }

    def property(String key, Object value) {
        properties[key] = value
    }

    def parameter(String key, Object value) {
        parameters[key] = value
    }

    def setStatus(String status) {
        this.status = status
    }

    def setAvailable(boolean available) {
        this.isAvailable = available
    }

    def className(String className) {
        this.className = className
    }
}

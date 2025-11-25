package com.pega.dsl

class Application extends Rule {
    // Ensure ScriptExtensions (which installs global DSL helpers) is loaded
    static final __initScriptExtensions = com.pega.dsl.ScriptExtensions.class

    List<String> rulesets = []
    Map<String, Object> settings = [:]

    Application() { this.type = 'Application' }

    /**
     * Add a child ruleset to this application and execute its closure with
     * the new Ruleset as delegate. Tests expect the application to collect
     * ruleset names in `app.rulesets`.
     */
    def ruleset(String name, Closure closure) {
        def rs = new Ruleset(name: name)
        // record the ruleset name for assertions
        this.rulesets << name
        // attach ruleset-specific settings if provided via closure using rs.settings
        rs.parentApplication = this
        // delegate into the ruleset closure
        PegaDslCore.callWithDelegate(rs, closure, Closure.DELEGATE_FIRST)
        return rs
    }

    /**
     * Application-level settings helper used by the DSL (e.g. `setting 'debug', true`).
     */
    def setting(String key, Object value) {
        this.settings[key] = value
        return this
    }

    def setVersion(String v) {
        super.setVersion(v)
        return this
    }
}

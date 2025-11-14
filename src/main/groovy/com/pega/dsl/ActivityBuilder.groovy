package com.pega.dsl

class ActivityBuilder {
    public Boolean __test_marker_269__ = false
    private final Activity activity

    ActivityBuilder(Activity activity) {
        this.activity = activity
        println "DEBUG: Activity classloader in ActivityBuilder: ${activity.getClass().getClassLoader()}"
    }

    def callActivity(String activityName, Map params = [:]) {
        def step = new ActivityStep(method: 'Call')
        step.parameters['activity'] = activityName

        // Rehydrate Closure values inside params so any nested closures execute
        // with the step as their delegate/owner/thisObject.
        def sanitized = [:]
        params.each { k, v ->
            if (v instanceof Closure) {
                try {
                    // Special test hook to force a rehydration failure
                    if (params['__force_rehydration_failure__']) {
                        throw new GroovyRuntimeException("Forced failure")
                    }
                    def c = v.rehydrate(step, step, step)
                    c.resolveStrategy = Closure.DELEGATE_FIRST
                    sanitized[k] = c
                } catch (ignored) {
                    sanitized[k] = v
                }
            } else {
                sanitized[k] = v
            }
        }

        step.parameters.putAll(sanitized)
        activity.steps.add(step)
        return this
    }

    // methodMissing removed as redundant; all DSL operations are handled by explicit public methods.

    // Helper: normalize common (String, Map?) vararg shapes into a single result.
    // Accept null as an explicit empty-map placeholder. If a second argument is present
    // and is non-null but not a Map, treat the shape as unmatched (return null).
    private Map parseStringAndMapArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return null
        }
        def first = args[0]
        if (!(first instanceof String)) {
            return null
        }
        def s = (String) first
        if (args.length == 1) {
            return [string: s, map: [:]]
        }
        if (args.length >= 2) {
            def second = args[1]
            if (second == null) {
                return [string: s, map: [:]]
            }
            if (second instanceof Map) {
                return [string: s, map: (Map) second]
            }
            return null
        }
        // Defensive final return to satisfy static analysis; normally unreachable.
        return null
    }

    def description(String description) {
        def _d = PegaDslCore.CURRENT_DELEGATE.get()
        // If invoked from a different active delegate, do not perform builder actions.
        if (_d != null && _d != this) return this
        activity.setDescription(description)
        return this
    }
    def description(String desc, Map params) {
        def _d = PegaDslCore.CURRENT_DELEGATE.get()
        if (_d != null && _d != this) return this
        activity.setDescription(desc)
        return this
    }
    def description(String desc, LinkedHashMap params) {
        return description(desc, (Map)params)
    }

    def localVariable(String name, String type) {
        activity.localVariables[name] = type
        this
    }

    def propertySet(String property, String value) {
        def _d = PegaDslCore.CURRENT_DELEGATE.get()
        // If we're being invoked from a different delegate, avoid forwarding
        // and keep builder methods idempotent/side-effect free in that context.
        if (_d != null && _d != this) return this
        def step = new ActivityStep(method: 'Property-Set')
        step.parameters['PropertyName'] = property
        step.parameters['PropertyValue'] = value
        activity.steps.add(step)
        return this
    }
    def propertySet(String property, Map params) {
        def _d = PegaDslCore.CURRENT_DELEGATE.get()
        if (_d != null && _d != this) return this
        def step = new ActivityStep(method: 'Property-Set')
        step.parameters['PropertyName'] = property
        step.parameters.putAll(params)
        activity.steps.add(step)
        return this
    }
    def propertySet(String property, LinkedHashMap params) {
        return propertySet(property, (Map)params)
    }
    def propertySet(Map params) {
        def _d = PegaDslCore.CURRENT_DELEGATE.get()
        if (_d != null && _d != this) return this
        def step = new ActivityStep(method: 'Property-Set')
        step.parameters.putAll(params)
        activity.steps.add(step)
        return this
    }
    def propertySet(LinkedHashMap params) {
        return propertySet((Map)params)
    }

    def pageNew(String pageName, String className) {
        def step = new ActivityStep(method: 'Page-New')
        step.parameters['PageName'] = pageName
        step.parameters['ClassName'] = className
        activity.steps.add(step)
        this
    }

    def objOpen(String objectKey, String mode = 'UPDATE') {
        def step = new ActivityStep(method: 'Obj-Open')
        step.parameters['ObjectKey'] = objectKey
        step.parameters['Mode'] = mode
        activity.steps.add(step)
        this
    }

    def objSave() {
        def step = new ActivityStep(method: 'Obj-Save')
        activity.steps.add(step)
        this
    }

    def addCallStep(String activityName, Map params = [:]) {
        def step = new ActivityStep(method: 'Call')
        step.parameters['activity'] = activityName
        def sanitized = [:]
        params.each { k, v ->
            if (v instanceof Closure) {
                try {
                    if (params['__force_rehydration_failure__']) {
                        throw new GroovyRuntimeException("Forced failure")
                    }
                    def c = v.rehydrate(step, step, step)
                    c.resolveStrategy = Closure.DELEGATE_FIRST
                    sanitized[k] = c
                } catch (ignored) {
                    sanitized[k] = v
                }
            } else {
                sanitized[k] = v
            }
        }
        step.parameters.putAll(sanitized)
        activity.steps.add(step)
        return this
    }

    def addComment(String comment) {
        def step = new ActivityStep(method: 'Comment')
        step.parameters['Comment'] = comment
        activity.steps.add(step)
        return this
    }

    def propertyRemove(String propertyName) {
        def step = new ActivityStep(method: 'Property-Remove')
        step.parameters['PropertyName'] = propertyName
        activity.steps.add(step)
        return this
    }

    def waitSeconds(int seconds) {
        def step = new ActivityStep(method: 'Wait')
        step.parameters['Seconds'] = seconds.toString()
        activity.steps.add(step)
        return this
    }

    def connectREST(Object... args) {
        if (args == null || args.length == 0) return this
        def parsed = parseStringAndMapArgs(args)
        if (parsed == null) return this
        return connectREST(parsed.string, parsed.map)
    }
    def connectREST(String connector, Map params = [:]) {
        if (params == null) params = [:]
        def step = new ActivityStep(method: 'Connect-REST')
        step.parameters['connector'] = connector
        step.parameters.putAll(params)
        activity.steps.add(step)
        this
    }

    def connectSOAP(Object... args) {
        if (args == null || args.length == 0) return this
        def parsed = parseStringAndMapArgs(args)
        if (parsed == null) return this
        return connectSOAP(parsed.string, parsed.map)
    }
    def connectSOAP(String connector, Map params = [:]) {
        if (params == null) params = [:]
        def step = new ActivityStep(method: 'Connect-SOAP')
        step.parameters['connector'] = connector
        step.parameters.putAll(params)
        activity.steps.add(step)
        this
    }

    def applyDataTransform(String dataTransform, String source = '', String target = '') {
        def step = new ActivityStep(method: 'Apply-DataTransform')
        step.parameters['DataTransform'] = dataTransform
        if (source) step.parameters['Source'] = source
        if (target) step.parameters['Target'] = target
        activity.steps.add(step)
        this
    }

    def loadDataPage(Object... args) {
        if (args == null || args.length == 0) return this
        def parsed = parseStringAndMapArgs(args)
        if (parsed == null) return this
        return loadDataPage(parsed.string, parsed.map)
    }
    def loadDataPage(String dataPageName, Map params = [:]) {
        if (params == null) params = [:]
        def step = new ActivityStep(method: 'Load-DataPage')
        step.parameters['DataPageName'] = dataPageName
        step.parameters.putAll(params)
        activity.steps.add(step)
        this
    }

    def showPage(String pageName, String format = 'HTML') {
        def step = new ActivityStep(method: 'Show-Page')
        step.parameters['PageName'] = pageName
        step.parameters['Format'] = format
        activity.steps.add(step)
        this
    }

    def branch(String activityName, String condition = '') {
        def step = new ActivityStep(method: 'Branch')
        step.parameters['Activity'] = activityName
        if (condition) step.parameters['Condition'] = condition
        activity.steps.add(step)
        this
    }

    def logMessage(String message, String level = 'INFO') {
        def step = new ActivityStep(method: 'Log-Message')
        step.parameters['Message'] = message
        step.parameters['Level'] = level
        activity.steps.add(step)
        this
    }

    def queueVarargs(Object... args) {
        if (args == null || args.length == 0) return this
        def parsed = parseStringAndMapArgs(args)
        if (parsed == null) return this
        // Preserve test hook behavior: set marker when a Map argument was provided.
        if (args.length >= 2 && args[1] instanceof Map) {
            this.__test_marker_269__ = true
        }
        return queue(parsed.string, parsed.map)
    }
    // Backward-compatible wrapper so existing tests and callers using queue(...) still work.
    def queue(Object... args) {
        return queueVarargs(*args)
    }
    def queue(String activityName, Map params = [:]) {
        if (params == null) params = [:]
        def step = new ActivityStep(method: 'Queue')
        step.parameters['Activity'] = activityName
        step.parameters.putAll(params)
        activity.steps.add(step)
        this
    }

    def commit() {
        def step = new ActivityStep(method: 'Commit')
        activity.steps.add(step)
        this
    }

    def rollback() {
        def step = new ActivityStep(method: 'Rollback')
        activity.steps.add(step)
        this
    }

    def step(String method, Closure closure = null) {
        def _d = PegaDslCore.CURRENT_DELEGATE.get()
        if (_d != null && _d != this) return this

        def step = new ActivityStep(method: method)
        if (closure) {
            def prev = PegaDslCore.CURRENT_DELEGATE.get()
            try {
                if (prev != this) PegaDslCore.CURRENT_DELEGATE.set(this)
                def target = closure.rehydrate(step, step, step)
                target.resolveStrategy = Closure.DELEGATE_FIRST
                // PegaDslCore.installClosureInterceptors(target)
                target.call()
            } finally {
                if (prev != null) PegaDslCore.CURRENT_DELEGATE.set(prev) else PegaDslCore.CURRENT_DELEGATE.remove()
            }
        }
        activity.steps.add(step)
        this
    }

    def setStatus(String status) {
        activity.status = status
        this
    }

    def setAvailable(boolean available) {
        activity.isAvailable = available
        this
    }

    Activity getActivity() {
        return activity
    }
}

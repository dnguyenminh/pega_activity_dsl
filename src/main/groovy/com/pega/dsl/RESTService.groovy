package com.pega.dsl

class RESTService {
    String name
    String servicePackage
    String description // Added this line
    // common metadata expected by tests
    String type = 'Service-REST'
    String activity
    List<Map> paths = []
    Map<String, Object> properties = [:]

    // transient helpers for path/operation building
    Map _currentPathNode = null
    Map _currentOperation = null
    String method
    String _currentMappingMode = null

    RESTService() {}
    RESTService(String name) { this.name = name }
    def call(Closure c) { if (c) { c.delegate = this; c.resolveStrategy = Closure.DELEGATE_FIRST; c.call() } }

    def servicePackage(String pkg) { this.servicePackage = pkg; return this }
    def path(String p, Closure c = null) {
        def _prev = PegaDslCore.CURRENT_DELEGATE.get()
        def node = [path: p, operations: []]
        paths.add(node)
        this._currentPathNode = node
        if (c) {
            PegaDslCore.CURRENT_DELEGATE.set(this)
            try {
                c.delegate = this
                c.resolveStrategy = Closure.DELEGATE_FIRST
                c.call()
            } finally {
                if (_prev != null) PegaDslCore.CURRENT_DELEGATE.set(_prev) else PegaDslCore.CURRENT_DELEGATE.remove()
                this._currentPathNode = null
                this._currentOperation = null
            }
        }
        return this
    }

    // create an operation entry for the active path and set current operation context
    private Map _addOperation(String httpMethod) {
        def node = this._currentPathNode ?: (paths ? paths[-1] : null)
        if (node == null) {
            node = [path: '/', operations: []]
            paths.add(node)
            this._currentPathNode = node
        }
        def op = [method: httpMethod, activity: null, properties: [:], requestMapping: [:], responseMapping: [:]]
        node.operations << op
        this._currentOperation = op
        this.method = httpMethod
        return op
    }

    def get() { _addOperation('GET'); return this }
    def post() { _addOperation('POST'); return this }
    def put() { _addOperation('PUT'); return this }
    def delete() { _addOperation('DELETE'); return this }
    def patch() { _addOperation('PATCH'); return this }

    def header(String k, String v) {
        if (this._currentOperation != null) {
            this._currentOperation.properties[k] = v
        } else {
            this.properties[k] = v
        }
        return this
    }

    // Associate the current operation with an activity
    def activity(String activityName) {
        if (this._currentOperation != null) {
            this._currentOperation.activity = activityName
            this._currentOperation.properties['activity'] = activityName
            // also set top-level activity for easier assertions
            this.activity = activityName
        } else {
            this.properties['activity'] = activityName
            this.activity = activityName
        }
        return this
    }

    // Mapping helpers delegated to current operation
    def requestMapping(Map m) {
        if (this._currentOperation != null) this._currentOperation.requestMapping.putAll(m)
        return this
    }
    def requestMapping(Closure c) {
        this._currentMappingMode = 'request'
        try {
            if (c) { c.delegate = this; c.resolveStrategy = Closure.DELEGATE_FIRST; c.call() }
        } finally {
            this._currentMappingMode = null
        }
        return this
    }

    def responseMapping(Map m) {
        if (this._currentOperation != null) this._currentOperation.responseMapping.putAll(m)
        return this
    }
    def responseMapping(Closure c) {
        this._currentMappingMode = 'response'
        try {
            if (c) { c.delegate = this; c.resolveStrategy = Closure.DELEGATE_FIRST; c.call() }
        } finally {
            this._currentMappingMode = null
        }
        return this
    }

    // Called from mapping closures
    def map(String from, String to) {
        if (this._currentOperation == null) return this
        if (_currentMappingMode == 'response') {
            this._currentOperation.responseMapping[to] = from
        } else {
            this._currentOperation.requestMapping[to] = from
        }
        return this
    }
    
    // Expose the active resource path via property access (service.resourcePath)
    def getResourcePath() {
        def node = this._currentPathNode ?: (paths ? paths[-1] : null)
        return node?.path
    }

    // Convenience accessors used by tests: return the request/response mapping for the
    // most recently defined operation on the most recent path.
    def getRequestMapping() {
        def node = (paths ? paths[-1] : null)
        def op = node?.operations ? node.operations[-1] : null
        return op?.requestMapping ?: [:]
    }

    def getResponseMapping() {
        def node = (paths ? paths[-1] : null)
        def op = node?.operations ? node.operations[-1] : null
        return op?.responseMapping ?: [:]
    }

    def description(String description) {
        this.description = description
        return this
    }

    def setDescription(String description) {
        this.description = description
        return this
    }
}


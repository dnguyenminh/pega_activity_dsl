package com.pega.dsl

class RESTConnector {
    String type
    String name
    String description
    String url
    String method = 'GET'
    String authProfile
    Map<String, String> headers = [:]
    Map<String, Object> requestMapping = [:]
    Map<String, Object> responseMapping = [:]
    Map<String, Object> properties = [:]

    // internal marker used when mapping closures are active
    String _currentMappingMode = null

    RESTConnector() { this.type = 'Connect-REST' }
    RESTConnector(String name) { this.name = name; this.type = 'Connect-REST' }

    def call(Closure c) { if (c) { c.delegate = this; c.resolveStrategy = Closure.DELEGATE_FIRST; c.call() } }

    // Minimal API expected by tests
    def description(String d) { this.description = d; return this }
    def url(String u) { this.url = u; return this }
    def get() { this.method = 'GET'; return this }
    def post() { this.method = 'POST'; return this }
    def put() { this.method = 'PUT'; return this }
    def delete() { this.method = 'DELETE'; return this }
    def patch() { this.method = 'PATCH'; return this }
    def method(String m) { this.method = m; return this }
    def authentication(String auth) { this.authProfile = auth; return this }
    def header(String k, String v) { headers[k] = v; return this }

    // Mapping helpers: provide a map(from, to) method usable inside closures
    def requestMapping(Map m) { if (m) this.requestMapping.putAll(m); return this }
    def requestMapping(Closure c) {
        _currentMappingMode = 'request'
        try {
            if (c) { c.delegate = this; c.resolveStrategy = Closure.DELEGATE_FIRST; c.call() }
        } finally {
            _currentMappingMode = null
        }
        return this
    }

    def responseMapping(Map m) { if (m) this.responseMapping.putAll(m); return this }
    def responseMapping(Closure c) {
        _currentMappingMode = 'response'
        try {
            if (c) { c.delegate = this; c.resolveStrategy = Closure.DELEGATE_FIRST; c.call() }
        } finally {
            _currentMappingMode = null
        }
        return this
    }

    // Called from mapping closures
    def map(String from, String to) {
        if (_currentMappingMode == 'response') {
            this.responseMapping[to] = from
        } else {
            // default to request mapping when unclear
            this.requestMapping[to] = from
        }
        return this
    }

    // Allow explicit set(key, value) calls inside mapping closures used by tests
    def set(String key, Object value) {
        if (_currentMappingMode == 'response') {
            this.responseMapping[key] = value
        } else {
            this.requestMapping[key] = value
        }
        return this
    }
}


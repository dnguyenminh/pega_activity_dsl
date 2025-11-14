package com.pega.dsl

class SOAPConnector {
    String type = 'Connect-SOAP'
    String name
    String namespace
    String operation
    String description
    String wsdlUrl
    String authProfile
    Map<String, String> headers = [:]
    Map<String, Object> requestMapping = [:]
    Map<String, Object> responseMapping = [:]
    Map<String, Object> properties = [:]
    String _currentMappingMode = null
    List<String> operations = []

    SOAPConnector() {}
    SOAPConnector(String name) { this.name = name }
    def call(Closure c) { if (c) { c.delegate = this; c.resolveStrategy = Closure.DELEGATE_FIRST; c.call() } }

    def wsdl(String url) { this.wsdlUrl = url; return this }
    def namespace(String ns) { this.namespace = ns; this.properties['namespace'] = ns; return this }
    def description(String d) { this.description = d; return this }
    def header(String k, String v) { headers[k] = v; return this }
    def authentication(String auth) { this.authProfile = auth; return this }

    def operation(String op) { if (op) operations << op; this.operation = op; properties['operation'] = op; return this }

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

    def map(String from, String to) {
        if (_currentMappingMode == 'response') {
            this.responseMapping[to] = from
        } else {
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

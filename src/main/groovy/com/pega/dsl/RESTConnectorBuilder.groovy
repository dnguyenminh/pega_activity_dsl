package com.pega.dsl

class RESTConnectorBuilder {
    private final RESTConnector connector

    RESTConnectorBuilder(RESTConnector connector) {
        this.connector = connector
    }

    def doCall(Object... args) { this }

    def methodMissing(String name, Object[] args) {
        if (name == 'call' && args != null && args.length >= 1 && args[0] instanceof String) {
            return this
        }
        throw new MissingMethodException(name, this.class, args)
    }

    def description(String description) {
        connector.description = description
        this
    }

    def url(String url) {
        connector.url = url
        this
    }

    def get() {
        connector.method = 'GET'
        this
    }

    def post() {
        connector.method = 'POST'
        this
    }

    def put() {
        connector.method = 'PUT'
        this
    }

    def delete() {
        connector.method = 'DELETE'
        this
    }

    def patch() {
        connector.method = 'PATCH'
        this
    }

    def authentication(String authProfile) {
        connector.authProfile = authProfile
        this
    }

    def header(String name, String value) {
        connector.headers[name] = value
        this
    }

    def requestMapping(Closure closure) {
        connector.requestMapping(closure)
        this
    }

    def responseMapping(Closure closure) {
        connector.responseMapping(closure)
        this
    }
}

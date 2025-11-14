package com.pega.dsl

class SOAPConnectorBuilder {
    private final SOAPConnector connector

    SOAPConnectorBuilder(SOAPConnector connector) {
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

    def wsdl(String url) {
        connector.wsdlUrl = url
        this
    }

def namespace(String ns) {
        connector.namespace = ns
        this
    }

    def operation(String name) {
        connector.operation = name
        this
    }

    def authentication(String authProfile) {
        connector.authProfile = authProfile
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

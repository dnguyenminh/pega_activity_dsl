package com.pega.dsl

import groovy.lang.Closure

class PegaDslBuilders {
    static RESTConnector restConnector(String name, @DelegatesTo(RESTConnectorBuilder) Closure closure) {
        def connector = new RESTConnector(name: name)
        def builder = new RESTConnectorBuilder(connector)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        return connector
    }

    static SOAPConnector soapConnector(String name, @DelegatesTo(SOAPConnectorBuilder) Closure closure) {
        def connector = new SOAPConnector(name: name)
        def builder = new SOAPConnectorBuilder(connector)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        return connector
    }
}

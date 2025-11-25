package com.pega.dsl

import spock.lang.Specification

class SOAPConnectorSpec extends Specification {

    def "fluent setters and operation list update"() {
        given:
        def c = new SOAPConnector('mySoap')

        when:
        c.wsdl('http://example/wsdl')
         .namespace('urn:ns')
         .description('desc')
         .header('h1', 'v1')
         .authentication('authProfile')
         .operation('op1')

        then:
        c.name == 'mySoap'
        c.wsdlUrl == 'http://example/wsdl'
        c.namespace == 'urn:ns'
        c.description == 'desc'
        c.headers['h1'] == 'v1'
        c.authProfile == 'authProfile'
        c.operation == 'op1'
        c.operations.contains('op1')
        c.properties['operation'] == 'op1'
        c.properties['namespace'] == 'urn:ns'
    }

    def "request and response mapping closures and map/set behavior"() {
        given:
        def c = new SOAPConnector()

        when: "use requestMapping closure"
        c.requestMapping {
            map 'fromA', 'toA'
            set 'k1', 'v1'
        }

        and: "use responseMapping closure"
        c.responseMapping {
            map 'respFrom', 'respTo'
            set 'rk', 123
        }

        then:
        c.requestMapping['toA'] == 'fromA'
        c.requestMapping['k1'] == 'v1'
        c.responseMapping['respTo'] == 'respFrom'
        c.responseMapping['rk'] == 123
    }

    def "call delegates closure to connector instance"() {
        given:
        def c = new SOAPConnector()

        when:
        def invoked = false
        c.call {
            invoked = true
            // ensure delegate is the connector
            assert delegate instanceof SOAPConnector
        }

        then:
        invoked
    }
}

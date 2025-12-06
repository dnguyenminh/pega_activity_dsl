package com.pega.dsl

import spock.lang.Specification

class ConnectorBranchCoverageSpec extends Specification {

    def "RESTConnector handles direct map inputs and bare calls"() {
        given:
        def connector = new RESTConnector('rest-branch')

        when:
        connector.call(null)
        connector.method('HEAD')
        connector.requestMapping([direct: 'source'])
        connector.requestMapping((Map) null)
        connector.responseMapping([directResp: 'respSource'])
        connector.responseMapping((Map) null)
        connector.requestMapping((Closure) null)
        connector.responseMapping((Closure) null)
        connector.map('payload', 'payloadOut')
        connector.set('explicit', 42)
        connector.responseMapping {
            map 'server', 'client'
            set 'status', '200'
        }

        then:
        connector.method == 'HEAD'
        connector.requestMapping['payloadOut'] == 'payload'
        connector.requestMapping['explicit'] == 42
        connector.requestMapping['direct'] == 'source'
        connector.responseMapping['client'] == 'server'
        connector.responseMapping['status'] == '200'
        connector.responseMapping['directResp'] == 'respSource'
    }

    def "RESTConnectorBuilder methodMissing enforces argument shape"() {
        given:
        def builder = new RESTConnectorBuilder(new RESTConnector())

        when:
        builder.methodMissing('call', ['ok'] as Object[])

        then:
        noExceptionThrown()

        when:
        builder.methodMissing('unknown', [] as Object[])

        then:
        thrown(MissingMethodException)

        when:
        builder.methodMissing('call', null)

        then:
        thrown(MissingMethodException)

        when:
        builder.methodMissing('call', [] as Object[])

        then:
        thrown(MissingMethodException)

        when:
        builder.methodMissing('call', [123] as Object[])

        then:
        thrown(MissingMethodException)
    }

    def "SOAPConnector handles null operations and map based mappings"() {
        given:
        def connector = new SOAPConnector('soap-branch')

        when:
        connector.call(null)
        connector.operation(null)
        connector.requestMapping([direct: 'value'])
        connector.requestMapping((Map) null)
        connector.responseMapping([respKey: 'respValue'])
        connector.responseMapping((Map) null)
        connector.requestMapping((Closure) null)
        connector.responseMapping((Closure) null)
        connector.map('bodyIn', 'bodyOut')
        connector.set('explicit', 'exp')
        connector.responseMapping {
            map 'respIn', 'respOut'
            set 'flag', true
        }

        then:
        connector.operations.isEmpty()
        connector.requestMapping['bodyOut'] == 'bodyIn'
        connector.requestMapping['explicit'] == 'exp'
        connector.requestMapping['direct'] == 'value'
        connector.responseMapping['respOut'] == 'respIn'
        connector.responseMapping['flag'] == true
        connector.responseMapping['respKey'] == 'respValue'
    }

    def "SOAPConnectorBuilder methodMissing validates input"() {
        given:
        def builder = new SOAPConnectorBuilder(new SOAPConnector())

        when:
        builder.methodMissing('call', ['ok'] as Object[])

        then:
        noExceptionThrown()

        when:
        builder.methodMissing('ignored', [] as Object[])

        then:
        thrown(MissingMethodException)

        when:
        builder.methodMissing('call', null)

        then:
        thrown(MissingMethodException)

        when:
        builder.methodMissing('call', [] as Object[])

        then:
        thrown(MissingMethodException)

        when:
        builder.methodMissing('call', [new Object()] as Object[])

        then:
        thrown(MissingMethodException)
    }
}

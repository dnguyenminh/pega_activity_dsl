package com.pega.dsl

import spock.lang.Specification

class SOAPConnectorBuilderSpec extends Specification {

    def "builder fluent methods apply to connector and return builder"() {
        given:
        def conn = new SOAPConnector()
        def b = new SOAPConnectorBuilder(conn)

        when:
        def ret = b.description('d').wsdl('u').namespace('ns').operation('op').authentication('auth')

        then:
        ret.is(b)
        conn.description == 'd'
        conn.wsdlUrl == 'u'
        conn.namespace == 'ns'
        conn.operation == 'op'
        conn.authProfile == 'auth'
    }

    def "doCall returns builder and methodMissing behavior"() {
        given:
        def builder = new SOAPConnectorBuilder(new SOAPConnector())

        expect:
        builder.doCall() instanceof SOAPConnectorBuilder
        builder.methodMissing('call', ['some'] as Object[]) instanceof SOAPConnectorBuilder

        when: "invalid methodMissing invocation"
        builder.methodMissing('call', [1,2] as Object[])

        then: "should throw MissingMethodException for bad args"
        thrown(MissingMethodException)
    }

    def "request/response mapping closures pass through and clear mode"() {
        given:
        def c = new SOAPConnector('x')
        def b = new SOAPConnectorBuilder(c)

        when: 'request mapping closure is used'
        b.requestMapping {
            map('a', 'toA')
            set('rk', 'rv')
        }

        then:
        c.requestMapping['toA'] == 'a'
        c.requestMapping['rk'] == 'rv'
        c._currentMappingMode == null

        when: 'response mapping closure is used'
        b.responseMapping {
            map('rfrom', 'rTo')
            set('rkey', 'rval')
        }

        then:
        c.responseMapping['rTo'] == 'rfrom'
        c.responseMapping['rkey'] == 'rval'
        c._currentMappingMode == null
    }
}


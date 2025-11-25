package com.pega.dsl

import spock.lang.Specification

class RESTConnectorBuilderSpec extends Specification {

    def "builder fluent setters and headers/authentication"() {
        given:
        def conn = new RESTConnector('demo')
        def builder = new RESTConnectorBuilder(conn)

        when:
        def ret = builder.description('desc').url('http://example').post().authentication('oauth').header('X-Test','v')

        then:
        ret.is(builder)
        conn.description == 'desc'
        conn.url == 'http://example'
        conn.method == 'POST'
        conn.authProfile == 'oauth'
        conn.headers['X-Test'] == 'v'
    }

    def "methodMissing supports call(String)"() {
        given:
        def conn = new RESTConnector()
        def builder = new RESTConnectorBuilder(conn)

        expect:
        builder.methodMissing('call', ['someName'] as Object[]) instanceof RESTConnectorBuilder
    }

    def "request and response mapping closures operate correctly"() {
        given:
        def conn = new RESTConnector()
        def builder = new RESTConnectorBuilder(conn)

        when:
        builder.requestMapping {
            map 'inA', 'outA'
            set 'explicitReq', 'reqValue'
        }

        builder.responseMapping {
            map 'respIn', 'respOut'
            set 'explicitResp', 'respValue'
        }

        then:
        conn.requestMapping['outA'] == 'inA'
        conn.requestMapping['explicitReq'] == 'reqValue'
        conn.responseMapping['respOut'] == 'respIn'
        conn.responseMapping['explicitResp'] == 'respValue'
    }
}


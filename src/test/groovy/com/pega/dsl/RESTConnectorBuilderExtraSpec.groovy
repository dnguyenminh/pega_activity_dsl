package com.pega.dsl

import spock.lang.Specification

class RESTConnectorBuilderExtraSpec extends Specification {

    def "exercise more RESTConnectorBuilder API and mapping branches"() {
        given:
        def connector = new RESTConnector('t')
        def builder = new RESTConnectorBuilder(connector)

        when: 'invoke descriptive setters and auth'
        builder.url('https://example.test')
        builder.description('desc')
        builder.authentication('OAuth')

        and: 'exercise header variants'
        builder.header('X-A', '1')
        builder.header('X-B', '2')

        and: 'exercise verb helpers'
        builder.get()
        builder.post()
        builder.put()
        builder.delete()
        builder.patch()

        and: 'exercise callable/doCall behavior'
        def called = builder.doCall('some', 'args')

    and: 'exercise mapping helpers with closure forms (map/set available inside)'
    builder.requestMapping { map 'p1','q1' }
    builder.requestMapping { set('k1', 'v1'); map('from1', 'to1') }
    builder.responseMapping { map 'r1','s1' }
    builder.responseMapping { set('rk', 'rv'); map('rf', 'rt') }

        then:
        noExceptionThrown()
        called.is(builder)
        connector.url == 'https://example.test'
        connector.headers['X-A'] == '1'
    }

    def "RESTConnectorBuilder methods set connector fields and support methodMissing"() {
        given:
        def connector = new RESTConnector(name: 'api')
        def builder = new RESTConnectorBuilder(connector)

        when: "call fluent setters and mappings"
        builder.description('desc').url('http://x').post().authentication('auth').header('h','v')
        builder.requestMapping { map 'fromA','toA'; set 'k','v' }
        builder.responseMapping { map 'fromR','toR'; set 'rk','rv' }

        then:
        connector.description == 'desc'
        connector.url == 'http://x'
        connector.method == 'POST'
        connector.authProfile == 'auth'
        connector.headers['h'] == 'v'
        // mappings applied by map/set inside closures
        connector.requestMapping['toA'] == 'fromA'
        connector.requestMapping['k'] == 'v'
        connector.responseMapping['toR'] == 'fromR'
        connector.responseMapping['rk'] == 'rv'

        when: "methodMissing 'call' with a String returns the builder"
        def result = builder.methodMissing('call', ['op'] as Object[])

        then:
        result.is(builder)
    }
}

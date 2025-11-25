package com.pega.dsl

import spock.lang.Specification

class RESTConnectorSpec extends Specification {

    def "mapping closures attach mappings to correct maps and shortcuts work"() {
        given:
        def rc = new RESTConnector('C1')

        when:
        rc.requestMapping {
            map('a','b')
            set('k','v')
        }

        rc.responseMapping {
            map('r1','r2')
            set('rk','rv')
        }

        rc.url('https://x')
        rc.put()
        rc.header('H','V')
        rc.authentication('AUTHX')

        then:
        rc.requestMapping['b'] == 'a'
        rc.requestMapping['k'] == 'v'

        rc.responseMapping['r2'] == 'r1'
        rc.responseMapping['rk'] == 'rv'

        rc.url == 'https://x'
        rc.method == 'PUT'
        rc.headers['H'] == 'V'
        rc.authProfile == 'AUTHX'
    }

    def "call delegates closure to connector instance"() {
        given:
        def rc = new RESTConnector('C2')

        when:
        rc.call {
            url 'http://delegated'
            post()
            header('X','Y')
        }

        then:
        rc.url == 'http://delegated'
        rc.method == 'POST'
        rc.headers['X'] == 'Y'
    }
}

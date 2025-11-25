package com.pega.dsl

import spock.lang.Specification

class PegaDslBuildersSpec extends Specification {

    def "restConnector builder delegates and configures connector"() {
        when:
        def connector = PegaDslBuilders.restConnector('myRest') {
            description 'desc'
            url 'http://example'
            post()
            authentication 'auth1'
            header 'H1', 'v1'
            requestMapping {
                map 'a', 'b'
                set 'c', 'd'
            }
            responseMapping {
                map 'x', 'y'
                set 'z', 'w'
            }
        }

        then:
        connector.name == 'myRest'
        connector.description == 'desc'
        connector.url == 'http://example'
        connector.method == 'POST'
        connector.authProfile == 'auth1'
        connector.headers['H1'] == 'v1'
        connector.requestMapping['b'] == 'a'
        connector.requestMapping['c'] == 'd'
        connector.responseMapping['y'] == 'x'
        connector.responseMapping['z'] == 'w'
    }

    def "soapConnector builder delegates and configures connector"() {
        when:
        def connector = PegaDslBuilders.soapConnector('mySoap') {
            description 'soapdesc'
            wsdl 'http://wsdl'
            namespace 'ns1'
            operation 'op1'
            authentication 'auth2'
            requestMapping {
                map 'r1', 'r2'
                set 'r3', 'r4'
            }
            responseMapping {
                map 's1', 's2'
                set 's3', 's4'
            }
        }

        then:
        connector.name == 'mySoap'
        connector.description == 'soapdesc'
        connector.wsdlUrl == 'http://wsdl'
    connector.namespace == 'ns1'
    connector.operation == 'op1'
    connector.operations.isEmpty()
        connector.authProfile == 'auth2'
        connector.requestMapping['r2'] == 'r1'
        connector.requestMapping['r3'] == 'r4'
        connector.responseMapping['s2'] == 's1'
        connector.responseMapping['s3'] == 's4'
    }

}

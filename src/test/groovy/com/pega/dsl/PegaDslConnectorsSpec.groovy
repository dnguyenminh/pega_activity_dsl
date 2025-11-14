package com.pega.dsl

import spock.lang.Specification

class PegaDslConnectorsSpec extends Specification {

    def "connector(name, Map) with no current delegate returns a RESTConnector"() {
        when:
        // ensure thread-local is cleared
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
        def conn = new PegaDslConnectors().connector('MyAPI', [foo: 'bar'])

        then:
        conn instanceof RESTConnector
        conn.name == 'MyAPI'
    }

    def "connector(name, activity, closure) with no delegate returns a RESTConnector"() {
        when:
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
        def conn = new PegaDslConnectors().connector('MyAPI2', 'ActivityX')

        then:
        conn instanceof RESTConnector
        conn.name == 'MyAPI2'
    }

    def "correspondence builder produces configured Correspondence"() {
        when:
        def corr = new PegaDslConnectors().correspondence('NotifyUser') {
            subject 'Order Update'
            body 'Your order has been updated.'
        }

        then:
        corr instanceof Correspondence
        corr.name == 'NotifyUser'
        corr.subject == 'Order Update'
        corr.body == 'Your order has been updated.'
    }
}


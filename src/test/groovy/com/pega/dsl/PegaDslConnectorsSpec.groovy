package com.pega.dsl

import spock.lang.Specification

class PegaDslConnectorsSpec extends Specification {

    def cleanup() {
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
    }

    def "connector(name, Map) with no current delegate returns a RESTConnector"() {
        when:
        // ensure thread-local is cleared
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
        def conn = new PegaDslConnectors().connector('MyAPI', [foo: 'bar'])

        then:
        conn instanceof RESTConnector
        conn.name == 'MyAPI'
    }

    def "connector(name, Map) forwards to data page delegate"() {
        given:
        def dataPage = new DataPage(name: 'DP1')
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(dataPage)

        when:
        def result = new PegaDslConnectors().connector('ExternalSystem', [query: '123'])

        then:
        result.is(dataPage)
        dataPage.sourceType == 'Connector'
        dataPage.dataSource == 'ExternalSystem'
        dataPage.sourceParameters == [query: '123']
    }

    def "connector(name, activity, closure) with no delegate returns a RESTConnector"() {
        when:
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
        def conn = new PegaDslConnectors().connector('MyAPI2', 'ActivityX')

        then:
        conn instanceof RESTConnector
        conn.name == 'MyAPI2'
    }

    def "connector(name, activity, closure) forwards to flow delegate"() {
        given:
        def flow = new Flow(name: 'ExampleFlow')
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(flow)

        when:
        def shape = new PegaDslConnectors().connector('RetryPath', 'MyActivity') {
            connector = 'OverrideActivity'
        }

        then:
        shape instanceof ConnectorShape
        flow.shapes.contains(shape)
        shape.name == 'RetryPath'
        shape.connector == 'OverrideActivity'
    }

    def "connector(name, activity) forwards to data page delegate"() {
        given:
        def dataPage = new DataPage(name: 'DP2')
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(dataPage)

        when:
        def result = new PegaDslConnectors().connector('ExternalConnector', 'ConnectorActivity')

        then:
        result.is(dataPage)
        dataPage.sourceType == 'Connector'
        dataPage.dataSource == 'ExternalConnector'
        dataPage.sourceParameters == [connector: 'ConnectorActivity']
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


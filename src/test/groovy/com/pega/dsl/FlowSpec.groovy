package com.pega.dsl

import spock.lang.Specification

class FlowSpec extends Specification {
    def "start with closure sets delegate and restores when prev null"() {
        given:
        def flow = new Flow('F1')

        when:
        flow.start('S1') {
            // this delegate should be the StartShape instance
            property('foo', 'bar')
        }

        then:
        flow.shapes.size() == 1
        def s = flow.shapes[0]
        s instanceof StartShape
        s.properties['foo'] == 'bar'
        // CURRENT_DELEGATE should be removed/restored to null
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get() == null
    }

    def "utility preserves previous delegate when present and restores it"() {
        given:
        def flow = new Flow('F2')
        def marker = new Object()
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(marker)

        when:
        flow.utility('U1', 'Act1') {
            property('u', 1)
        }

        then:
        flow.shapes.size() == 1
        def u = flow.shapes[0]
        u instanceof UtilityShape
        u.properties['u'] == 1
        // CURRENT_DELEGATE should be restored to marker
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get() is marker

        cleanup:
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
    }

    def "connector and subProcess add shapes and accept null closure"() {
        given:
        def flow = new Flow('F3')

        when:
        flow.connector('C1', 'ActX', null)
        flow.subProcess('SP1', 'SubFlowX', null)

        then:
        flow.shapes.find { it instanceof ConnectorShape && it.name == 'C1' }
        flow.shapes.find { it instanceof SubProcessShape && it.name == 'SP1' }
    }

    def "connector and subProcess with closure restore when prev null"() {
        given:
        def flow = new Flow('F5')
        // ensure there is no previous delegate
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()

        when:
        flow.connector('C3', 'ActZ') {
            property('ck2', 'vv')
        }
        flow.subProcess('SP3', 'SubZ') {
            property('spk2', 'vv2')
        }

        then:
        flow.shapes.find { it instanceof ConnectorShape && it.name == 'C3' }
        flow.shapes.find { it instanceof SubProcessShape && it.name == 'SP3' }
        // CURRENT_DELEGATE should be removed/restored to null
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get() == null
    }

    def "connector and subProcess restore previous delegate when present"() {
        given:
        def flow = new Flow('F4')
        def marker = new Object()
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(marker)

        when:
        flow.connector('C2', 'ActY') {
            property('ck', 'v')
        }
        flow.subProcess('SP2', 'SubY') {
            property('spk', 'v2')
        }

        then:
        flow.shapes.find { it instanceof ConnectorShape && it.name == 'C2' }
        flow.shapes.find { it instanceof SubProcessShape && it.name == 'SP2' }
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get() is marker

        cleanup:
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
    }
}

package com.pega.dsl

import spock.lang.Specification

class FlowBuilderSpec extends Specification {

    def "builder basic behavior and shape creation"() {
        given:
        def flow = new Flow('MyFlow')
        def b = new FlowBuilder(flow)

        when:
        b.description('desc').work()
        b.start('S1')
        b.assignment('A1')
        b.decision('D1')
        b.utility('U1', 'Act1')
        b.connector('C1', 'Conn1')
        b.subProcess('SP1', 'SubF')
        b.end('E1')
        b.connect('S1','A1','')

        then:
        flow.description == 'desc'
        flow.flowType == 'Work'
        flow.getShape('S1')?.name == 'S1'
        flow.getShape('A1')?.name == 'A1'
        flow.getShape('D1')?.name == 'D1'
        flow.getShape('U1')?.name == 'U1'
        flow.getShape('C1')?.name == 'C1'
        flow.getShape('SP1')?.name == 'SP1'
        flow.getShape('E1')?.name == 'E1'
        flow.connectors.find { it.from == 'S1' && it.to == 'A1' }
    }

    def "call/doCall return builder"() {
        given:
        def flow = new Flow('F')
        def b = new FlowBuilder(flow)

        expect:
        b.call() instanceof FlowBuilder
        b.doCall() instanceof FlowBuilder
    }
}


package com.pega.dsl

import spock.lang.Specification

class FlowBuilderAllMethodsSpec extends Specification {

    def "exercise builder methods with and without closures"() {
        given:
        def flow = new Flow()
        def fb = new FlowBuilder(flow)

        when: "basic properties and flow type setters"
        fb.description('the description')
        fb.work()
        fb.screen()
        fb.subFlow()

        then:
        // no exceptions and flow.flowType is set by last call
        flow.flowType == 'SubFlow'

        when: "call the doCall / call style methods"
        def r1 = fb.call('x')
        def r2 = fb.doCall('y')

        then:
        r1.is(fb)
        r2.is(fb)

        when: "add shapes using builder methods with closures"
        fb.start('S1') { setShapeProperty('a', 1) }
        fb.assignment('A1') { setShapeProperty('b', 2) }
        fb.decision('D1') { setShapeProperty('c', 3) }
        fb.utility('U1', 'Activity1') { setShapeProperty('d', 4) }
        fb.connector('C1', 'Connector1') { setShapeProperty('e', 5) }
        fb.subProcess('SP1', 'SubFlow1') { setShapeProperty('f', 6) }
        fb.end('E1') { setShapeProperty('g', 7) }

        then: "shapes were added and properties set"
        flow.shapes.find { it?.name == 'S1' }?.properties['a'] == 1
        flow.shapes.find { it?.name == 'A1' }?.properties['b'] == 2
        flow.shapes.find { it?.name == 'D1' }?.properties['c'] == 3
        flow.shapes.find { it?.name == 'U1' }?.properties['d'] == 4
        flow.shapes.find { it?.name == 'C1' }?.properties['e'] == 5
        flow.shapes.find { it?.name == 'SP1' }?.properties['f'] == 6
        flow.shapes.find { it?.name == 'E1' }?.properties['g'] == 7

        when: "add shapes using builder methods without closures"
        fb.start('S2', null)
        fb.assignment('A2', null)
        fb.decision('D2', null)
        fb.utility('U2', 'Activity2', null)
        fb.connector('C2', 'Connector2', null)
        fb.subProcess('SP2', 'SubFlow2', null)
        fb.end('E2', null)

        then: "shapes exist"
        flow.getShape('S2') != null
        flow.getShape('A2') != null
        flow.getShape('D2') != null
        flow.getShape('U2') != null
        flow.getShape('C2') != null
        flow.getShape('SP2') != null
        flow.getShape('E2') != null

    when: "add a connector via connect()"
    fb.connect('S1', 'E1', 'cond')

        then:
        flow.connectors.any { it.from == 'S1' && it.to == 'E1' && it.condition == 'cond' }
    }
}

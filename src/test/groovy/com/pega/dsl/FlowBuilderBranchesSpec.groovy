package com.pega.dsl

import spock.lang.Specification

class FlowBuilderBranchesSpec extends Specification {

    def "withDelegate applies closure to created shapes"() {
        given:
        def flow = new Flow('MyFlow')
        def b = new FlowBuilder(flow)

        when:
        b.start('S1') { property('k','v') }
        b.assignment('A1') { property('ak','av') }
        b.decision('D1') { property('dk','dv') }
        b.utility('U1','Act') { property('uk','uv') }
        b.connector('C1','Conn') { property('ck','cv') }
        b.subProcess('SP1','Sub') { property('spk','spv') }
        b.end('E1') { property('ek','ev') }

        then:
        flow.getShape('S1')?.properties['k'] == 'v'
        flow.getShape('A1')?.properties['ak'] == 'av'
        flow.getShape('D1')?.properties['dk'] == 'dv'
        flow.getShape('U1')?.properties['uk'] == 'uv'
        flow.getShape('C1')?.properties['ck'] == 'cv'
        flow.getShape('SP1')?.properties['spk'] == 'spv'
        flow.getShape('E1')?.properties['ek'] == 'ev'
    }

    def "withDelegate accepts null closures and doesn't throw"() {
        given:
        def flow = new Flow('F')
        def b = new FlowBuilder(flow)

        when:
        b.start('S')
        b.assignment('A')
        b.end('E')

        then:
        noExceptionThrown()
        flow.getShape('S') != null
        flow.getShape('A') != null
        flow.getShape('E') != null
    }

    def "flow type setters work"() {
        given:
        def flow = new Flow('F')
        def b = new FlowBuilder(flow)

        when:
        b.screen()
        then: flow.flowType == 'Screen'

        when:
        b.subFlow()
        then: flow.flowType == 'SubFlow'
    }

    def "connect adds connector with condition"() {
        given:
        def flow = new Flow('F')
        def b = new FlowBuilder(flow)

        when:
        b.connect('from','to','cond')

        then:
        flow.connectors.find { it.from == 'from' && it.to == 'to' && it.condition == 'cond' }
    }

    def "invoke missing method triggers MissingMethodException via invokeMethod"() {
        given:
        def flow = new Flow('F')
        def b = new FlowBuilder(flow)

        when:
        // call methodMissing reflectively so the implementation is executed directly
        def m = b.getClass().getDeclaredMethod('methodMissing', String, Object[].class)
        m.setAccessible(true)
        m.invoke(b, 'someUnknown', (Object) ['x'] as Object[])

        then:
        def ex = thrown(java.lang.reflect.InvocationTargetException)
        ex.cause instanceof MissingMethodException
    }
}

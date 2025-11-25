package com.pega.dsl

import spock.lang.Specification

class FlowBuilderExtraSpec extends Specification {

    def "methodMissing returns this when name is call and first arg is String"() {
        given:
        def flow = new Flow()
        def fb = new FlowBuilder(flow)

        when:
        def result = fb.methodMissing('call', ['someName'] as Object[])

        then:
        result.is(fb)
    }

    def "methodMissing throws MissingMethodException for unknown method"() {
        given:
        def fb = new FlowBuilder(new Flow())

        when:
        fb.methodMissing('noSuchMethod', [] as Object[])

        then:
        thrown(MissingMethodException)
    }

    def "start delegates closure to shape and sets properties and adds shape to flow"() {
        given:
        def flow = new Flow()
        def fb = new FlowBuilder(flow)

        when:
        fb.start('MyStart') { setShapeProperty('foo', 'bar') }

        then:
        flow.shapes.size() == 1
        def s = flow.shapes[0]
        s.name == 'MyStart'
        s.properties['foo'] == 'bar'
    }

    def "start with null closure still adds shape"() {
        given:
        def flow = new Flow()
        def fb = new FlowBuilder(flow)

        when:
        fb.start('NoClosure', null)

        then:
        flow.getShape('NoClosure') != null
    }
}

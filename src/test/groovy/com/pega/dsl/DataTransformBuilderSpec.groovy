package com.pega.dsl

import spock.lang.Specification

class DataTransformBuilderSpec extends Specification {

    def "basic setters and actions update the DataTransform"() {
        given:
        def dt = new DataTransform()
        def b = new DataTransformBuilder(dt)

        when:
        b.description('d1').className('com.Example').setStatus('Active').setAvailable(true)
        b.property('k1', 'v1')
        b.set('A', 'B')
        b.applyDataTransform('DT1', 'src', 'tgt')
        b.appendTo('T', 'S')
        b.remove('R')

        then:
        dt.description == 'd1'
        dt.className == 'com.Example'
        dt.status == 'Active'
        dt.isAvailable == true
        dt.properties['k1'] == 'v1'
        dt.actions.find { it.type == 'Set' && it.target == 'A' }
        dt.actions.find { it.type == 'Apply-DataTransform' && it.value == 'DT1' }
        dt.actions.find { it.type == 'Append to' && it.target == 'T' }
        dt.actions.find { it.type == 'Remove' && it.target == 'R' }
    }

    def "when and forEach accept closures and add nested children"() {
        given:
        def dt = new DataTransform()
        def b = new DataTransformBuilder(dt)

        when:
        b.when(if: 'x==1', then: {
            set('inner', '1')
            applyDataTransform('DT2')
        })

        b.forEach(in: 'D_Page', do: {
            set('innerF', 'f')
            appendTo('T', 'S')
        })

        then:
        def whenAction = dt.actions.find { it.type == 'When' }
        whenAction != null
        whenAction.children.size() >= 1

        def fe = dt.actions.find { it.type == 'For Each Page In' }
        fe != null
        fe.children.size() >= 1
    }

    def "doCall and methodMissing('call') return builder"() {
        given:
        def dt = new DataTransform()
        def b = new DataTransformBuilder(dt)

        expect:
    b.doCall() instanceof DataTransformBuilder
    b.doCall('someName') instanceof DataTransformBuilder
    }
}



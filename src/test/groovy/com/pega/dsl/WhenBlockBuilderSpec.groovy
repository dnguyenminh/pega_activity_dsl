package com.pega.dsl

import spock.lang.Specification

class WhenBlockBuilderSpec extends Specification {

    def "methodMissing call with string returns builder and set adds action"() {
        given:
        def container = new DataTransformAction(children: [])
        def block = new DataTransformWhenBlock(parentTransform: new DataTransform(), containerAction: container)
        def builder = new WhenBlockBuilder(block)

        when:
        def res = builder.methodMissing('call', ['candidate'] as Object[])
        builder.set('a.b', 'value')

        then:
        res == builder
        container.children.size() == 1
        container.children[0].type == 'Set'
        container.children[0].target == 'a.b'
        container.children[0].source == 'value'
    }

    def "when without closure creates a When child with condition"() {
        given:
        def container = new DataTransformAction(children: [])
        def block = new DataTransformWhenBlock(parentTransform: new DataTransform(), containerAction: container)
        def builder = new WhenBlockBuilder(block)

        when:
        builder.when([if: 'pxExists'])

        then:
        container.children.size() == 1
        container.children[0].type == 'When'
        container.children[0].condition == 'pxExists'
    }

    def "when with closure delegates to nested WhenBlockBuilder and nested set adds child"() {
        given:
        def container = new DataTransformAction(children: [])
        def block = new DataTransformWhenBlock(parentTransform: new DataTransform(), containerAction: container)
        def builder = new WhenBlockBuilder(block)

        when:
        builder.when([if: 'cond', then: { set('x','y') }])

        then:
        // top-level should have one child (the When action)
        container.children.size() == 1
        def whenAction = container.children[0]
        whenAction.type == 'When'
        // nested children should contain the Set added by the closure
        whenAction.children.size() == 1
        whenAction.children[0].type == 'Set'
        whenAction.children[0].target == 'x'
    }

    def "forEach with closure creates For Each Page In child and nested actions"() {
        given:
        def container = new DataTransformAction(children: [])
        def block = new DataTransformWhenBlock(parentTransform: new DataTransform(), containerAction: container)
        def builder = new WhenBlockBuilder(block)

    when:
    builder.forEach([in: 'D_pyWorkPage', do: { set('p','q') }])

    then:
    container.children.size() == 1
    def forAction = container.children[0]
    forAction.type == 'For Each Page In'
    forAction.target == 'D_pyWorkPage'
    // nested children added by the closure
    forAction.children.size() == 1
    forAction.children.find { it.type == 'Set' && it.target == 'p' }
    }

    def "applyDataTransform, appendTo and remove add appropriate actions"() {
        given:
        def container = new DataTransformAction(children: [])
        def block = new DataTransformWhenBlock(parentTransform: new DataTransform(), containerAction: container)
        def builder = new WhenBlockBuilder(block)

        when:
        builder.applyDataTransform('MyDT', 'src', 'tgt')
        builder.appendTo('tgt2', 'src2')
        builder.remove('tgt3')

        then:
        container.children.size() == 3
        container.children[0].type == 'Apply-DataTransform'
        container.children[0].value == 'MyDT'
        container.children[0].source == 'src'
        container.children[0].target == 'tgt'

        container.children[1].type == 'Append to'
        container.children[1].target == 'tgt2'
        container.children[1].source == 'src2'

        container.children[2].type == 'Remove'
        container.children[2].target == 'tgt3'
    }

    def "doCall returns builder and methodMissing throws for unknown method"() {
        given:
        def container = new DataTransformAction(children: [])
        def block = new DataTransformWhenBlock(parentTransform: new DataTransform(), containerAction: container)
        def builder = new WhenBlockBuilder(block)

        expect:
        builder.doCall() == builder

        when:
        builder.methodMissing('notACall', [] as Object[])

        then:
        thrown(MissingMethodException)
    }
}


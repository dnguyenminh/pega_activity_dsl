package com.pega.dsl

import spock.lang.Specification

class ForEachBlockBuilderSpec extends Specification {

    def "doCall and methodMissing call handling and set/apply/append/remove"() {
        given:
        def container = new DataTransformAction(children: [])
        def block = new DataTransformForEachBlock(parentTransform: null, containerAction: container)
        def builder = new ForEachBlockBuilder(block)

        expect:
        builder.doCall() == builder

        when: "methodMissing should allow call(String)"
    def res = builder.methodMissing('call', ['x'] as Object[])

        then:
        res == builder

        when: "set/apply/append/remove create children"
        builder.set('T', 'S')
        builder.applyDataTransform('DT', 'src', 'tgt')
        builder.appendTo('T2', 'S2')
        builder.remove('T3')

        then:
        container.children.find { it.type == 'Set' && it.target == 'T' }
        container.children.find { it.type == 'Apply-DataTransform' && it.value == 'DT' }
        container.children.find { it.type == 'Append to' && it.target == 'T2' }
        container.children.find { it.type == 'Remove' && it.target == 'T3' }
    }

    def "when/forEach nested closures build nested actions"() {
        given:
        def container = new DataTransformAction(children: [])
        def block = new DataTransformForEachBlock(parentTransform: null, containerAction: container)
        def builder = new ForEachBlockBuilder(block)

        when:
        builder.when(if: 'x==y', then: {
            set('innerT', 'innerS')
        })

        builder.forEach(in: 'D_pyPageList', do: {
            applyDataTransform('TransformOne')
        })

        then:
        def whenAction = container.children.find { it.type == 'When' }
        whenAction != null
        whenAction.children.size() == 1

        def feAction = container.children.find { it.type == 'For Each Page In' }
        feAction != null
        // nested applyDataTransform should show up as a child under the For Each action
        feAction.children.size() == 1
    }

    def "methodMissing call with String returns builder and non-string throws"() {
        given:
        def blockAction = new DataTransformAction(children: [])
        def block = new DataTransformForEachBlock(parentTransform: new DataTransform(), containerAction: blockAction)
        def builder = new ForEachBlockBuilder(block)

        expect:
        builder.methodMissing('call', ['hello'] as Object[]) is builder

        when:
    builder.methodMissing('call', [1] as Object[]) // This line remains unchanged

        then:
        thrown(MissingMethodException)
    }

    def "when adds child action and executes closure when provided"() {
        given:
        def blockAction = new DataTransformAction(children: [])
        def block = new DataTransformForEachBlock(parentTransform: new DataTransform(), containerAction: blockAction)
        def builder = new ForEachBlockBuilder(block)

        when:
        builder.when(if: 'x==y', then: { set('a','b') })

        then:
        blockAction.children.size() == 1
        def child = blockAction.children[0]
        child.type == 'When'
        child.condition == 'x==y'
        child.children.size() == 1
        child.children[0].type == 'Set'
        child.children[0].target == 'a'
        child.children[0].source == 'b'
    }

    def "forEach adds child action and executes closure when provided"() {
        given:
        def blockAction = new DataTransformAction(children: [])
        def block = new DataTransformForEachBlock(parentTransform: new DataTransform(), containerAction: blockAction)
        def builder = new ForEachBlockBuilder(block)

        when:
        builder.forEach(in: 'D_page', do: { set('x','y') })

        then:
        blockAction.children.size() == 1
        def child = blockAction.children[0]
        child.type == 'For Each Page In'
        child.target == 'D_page'
        child.children.size() == 1
        child.children[0].type == 'Set'
        child.children[0].target == 'x'
        child.children[0].source == 'y'
    }

    def "when/forEach handle non-closure gracefully"() {
        given:
        def blockAction = new DataTransformAction(children: [])
        def block = new DataTransformForEachBlock(parentTransform: new DataTransform(), containerAction: blockAction)
        def builder = new ForEachBlockBuilder(block)

        when:
        builder.when(if: 'cond', then: null)
        builder.forEach(in: 'p', do: null)

        then:
        blockAction.children.size() == 2
        blockAction.children[0].children.size() == 0
        blockAction.children[1].children.size() == 0
    }

    def "methodMissing handles null/empty args and non-call names; doCall returns builder"() {
        given:
        def blockAction = new DataTransformAction(children: [])
        def block = new DataTransformForEachBlock(parentTransform: new DataTransform(), containerAction: blockAction)
        def builder = new ForEachBlockBuilder(block)

        expect:
        // doCall should just return this builder
        builder.doCall() is builder
        builder.doCall('x') is builder

        when: "args is null"
        builder.methodMissing('call', null)

        then:
        thrown(MissingMethodException)

        when: "args is empty array"
        builder.methodMissing('call', [] as Object[])

        then:
        thrown(MissingMethodException)

        when: "name is not 'call'"
        builder.methodMissing('somethingElse', ['x'] as Object[])

        then:
        thrown(MissingMethodException)
    }
}

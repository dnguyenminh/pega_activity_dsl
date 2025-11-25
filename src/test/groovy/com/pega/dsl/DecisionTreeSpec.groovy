package com.pega.dsl

import spock.lang.Specification

class DecisionTreeSpec extends Specification {

    def "build a simple decision tree with branches and results"() {
        given:
        def tree = new DecisionTree()

        when:
        def root = tree.root('status') {
            condition('status', 'equals')
            branch('A') {
                result('DoA')
            }
            branch('B', 'DoB')
        }

        then:
        tree.rootNode.is(root)
        root.property == 'status'
        root.children.size() == 2
    root.children[0].value == 'A'
    root.children[0].result == 'DoA'
        root.children[1].value == 'B'
        root.children[1].result == 'DoB'
    }
}

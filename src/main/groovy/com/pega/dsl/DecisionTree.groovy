package com.pega.dsl

class DecisionTree extends Rule {
    TreeNode rootNode

    DecisionTree() {
        this.type = 'DecisionTree'
    }

    def root(String property, Closure closure) {
        rootNode = new TreeNode(property: property)
        closure.delegate = rootNode
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        return rootNode
    }
}


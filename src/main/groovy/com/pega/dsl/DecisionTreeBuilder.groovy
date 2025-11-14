package com.pega.dsl

class DecisionTreeBuilder {
    private final DecisionTree tree

    DecisionTreeBuilder(DecisionTree tree) {
        this.tree = tree
    }

    def description(String description) {
        tree.description = description
        this
    }

    def className(String className) {
        tree.className = className
        this
    }

    def setStatus(String status) {
        tree.status = status
        this
    }

    def setAvailable(boolean available) {
        tree.isAvailable = available
        this
    }

    def setTreeProperty(String key, Object value) {
        if (!tree.properties) tree.properties = [:]
        tree.properties[key] = value
        this
    }

    def root(Closure closure) {
        tree.rootNode = new TreeNode()
        closure.delegate = tree.rootNode
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        this
    }
}

package com.pega.dsl

class TreeNode {
    String property
    String operator
    String value
    String result
    List<TreeNode> children = []

    // Defines the condition for the current node
    def condition(String property, String operator) {
        this.property = property
        this.operator = operator
        this
    }

    // Creates a child branch with a specific value to test against the parent condition
    def branch(String value, Closure closure) {
        def child = new TreeNode(value: value)
        closure.delegate = child
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        children.add(child)
        this
    }

    // Creates a terminal branch with a value and a result
    def branch(String value, String result) {
        def child = new TreeNode(value: value, result: result)
        children.add(child)
        this
    }

    // Sets the result for a terminal node
    def result(String result) {
        this.result = result
        this
    }
}

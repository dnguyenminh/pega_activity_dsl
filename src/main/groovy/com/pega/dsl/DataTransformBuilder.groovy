package com.pega.dsl

class DataTransformBuilder {
    private final DataTransform transform

    DataTransformBuilder(DataTransform transform) {
        this.transform = transform
    }

    def doCall(Object... args) { this }

    def description(String description) {
        transform.description = description
        this
    }

    def className(String className) {
        transform.className = className
        this
    }

    def setStatus(String status) {
        transform.status = status
        this
    }

    def setAvailable(boolean available) {
        transform.isAvailable = available
        this
    }

    def property(String key, Object value) {
        if (!transform.properties) transform.properties = [:]
        transform.properties[key] = value
        this
    }

    def set(String target, Object source) {
        def a = new DataTransformAction(type: 'Set', target: target, source: source.toString())
        transform.actions.add(a)
        this
    }

    def when(Map params) {
        def condition = params.if
        def block = params.then
        def whenAction = new DataTransformAction(type: 'When', condition: condition, children: [])
        transform.actions.add(whenAction)
        
        if (block instanceof Closure) {
            def nestedBlock = new DataTransformWhenBlock(parentTransform: transform, containerAction: whenAction)
            def builder = new WhenBlockBuilder(nestedBlock)
            block.delegate = builder
            block.resolveStrategy = Closure.DELEGATE_FIRST
            block.call()
        }
        this
    }

    def forEach(Map params) {
        def pageList = params.in
        def block = params.do
        def forEachAction = new DataTransformAction(type: 'For Each Page In', target: pageList, children: [])
        transform.actions.add(forEachAction)

        if (block instanceof Closure) {
            def nestedBlock = new DataTransformForEachBlock(parentTransform: transform, containerAction: forEachAction)
            def builder = new ForEachBlockBuilder(nestedBlock)
            block.delegate = builder
            block.resolveStrategy = Closure.DELEGATE_FIRST
            block.call()
        }
        this
    }

    def applyDataTransform(String dataTransform, String source = '', String target = '') {
        def a = new DataTransformAction(
            type: 'Apply-DataTransform',
            target: target,
            source: source,
            value: dataTransform
        )
        transform.actions.add(a)
        this
    }

    def appendTo(String target, String source) {
        def a = new DataTransformAction(type: 'Append to', target: target, source: source)
        transform.actions.add(a)
        this
    }

    def remove(String target) {
        def a = new DataTransformAction(type: 'Remove', target: target)
        transform.actions.add(a)
        this
    }
}

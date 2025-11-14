package com.pega.dsl

class WhenBlockBuilder {
    private final DataTransformWhenBlock block

    WhenBlockBuilder(DataTransformWhenBlock block) {
        this.block = block
    }

    def doCall(Object... args) { this }

    def methodMissing(String name, Object[] args) {
        if (name == 'call' && args != null && args.length >= 1 && args[0] instanceof String) {
            return this
        }
        throw new MissingMethodException(name, this.class, args)
    }

    def set(String target, Object source) {
        def a = new DataTransformAction(type: 'Set', target: target, source: source.toString())
        block.containerAction.children.add(a)
        this
    }

    def when(Map params) {
        def condition = params.if
        def blockClosure = params.then
        def childAction = new DataTransformAction(type: 'When', condition: condition, children: [])
        block.containerAction.children.add(childAction)
        if (blockClosure instanceof Closure) {
            def newBlock = new DataTransformWhenBlock(parentTransform: block.parentTransform, containerAction: childAction)
            def newBuilder = new WhenBlockBuilder(newBlock)
            blockClosure.delegate = newBuilder
            blockClosure.resolveStrategy = Closure.DELEGATE_FIRST
            blockClosure.call()
        }
        this
    }

    def forEach(Map params) {
        def pageList = params.in
        def blockClosure = params.do
        def childAction = new DataTransformAction(type: 'For Each Page In', target: pageList, children: [])
        block.containerAction.children.add(childAction)
        if (blockClosure instanceof Closure) {
            def newBlock = new DataTransformForEachBlock(parentTransform: block.parentTransform, containerAction: childAction)
            def newBuilder = new ForEachBlockBuilder(newBlock)
            blockClosure.delegate = newBuilder
            blockClosure.resolveStrategy = Closure.DELEGATE_FIRST
            blockClosure.call()
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
        block.containerAction.children.add(a)
        this
    }

    def appendTo(String target, String source) {
        def a = new DataTransformAction(type: 'Append to', target: target, source: source)
        block.containerAction.children.add(a)
        this
    }

    def remove(String target) {
        def a = new DataTransformAction(type: 'Remove', target: target)
        block.containerAction.children.add(a)
        this
    }
}

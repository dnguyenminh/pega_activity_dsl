package com.pega.dsl

class FlowBuilder {
    private final Flow flow

    FlowBuilder(Flow flow) {
        this.flow = flow
    }

    def doCall(Object... args) {
        return this
    }
    
    // Explicit call method so the builder can be invoked like a closure:
    // builder() or builder('someString') — tests expect this behavior.
    def call(Object... args) {
        return this
    }
    
    def methodMissing(String name, Object[] args) {
        if (name == 'call' && args != null && args.length >= 1 && args[0] instanceof String) {
            return this
        }
        throw new MissingMethodException(name, this.class, args)
    }

    private def withDelegate(delegate, closure) {
        if (closure) {
            closure.delegate = delegate
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        return delegate
    }

    def description(String description) {
        flow.description = description
        this
    }

    def work() {
        flow.flowType = 'Work'
        this
    }

    def screen() {
        flow.flowType = 'Screen'
        this
    }

    def subFlow() {
        flow.flowType = 'SubFlow'
        this
    }

    def start(String name = 'Start', Closure closure = null) {
        def shape = new StartShape(name: name)
        withDelegate(shape, closure)
        flow.shapes.add(shape)
        this
    }

    def assignment(String name, Closure closure = null) {
        def shape = new AssignmentShape(name: name)
        withDelegate(shape, closure)
        flow.shapes.add(shape)
        this
    }

    def decision(String name, Closure closure = null) {
        def shape = new DecisionShape(name: name)
        withDelegate(shape, closure)
        flow.shapes.add(shape)
        this
    }

    def utility(String name, String activity, Closure closure = null) {
        def shape = new UtilityShape(name: name, activity: activity)
        withDelegate(shape, closure)
        flow.shapes.add(shape)
        this
    }

    def connector(String name, String connector, Closure closure = null) {
        def shape = new ConnectorShape(name: name, connector: connector)
        withDelegate(shape, closure)
        flow.shapes.add(shape)
        this
    }

    def subProcess(String name, String subFlow, Closure closure = null) {
        def shape = new SubProcessShape(name: name, subFlow: subFlow)
        withDelegate(shape, closure)
        flow.shapes.add(shape)
        this
    }

    def end(String name = 'End', Closure closure = null) {
        def shape = new EndShape(name: name)
        withDelegate(shape, closure)
        flow.shapes.add(shape)
        this
    }

    def connect(String from, String to, String condition = '') {
        flow.connectors.add(new FlowConnector(from: from, to: to, condition: condition))
        this
    }
}

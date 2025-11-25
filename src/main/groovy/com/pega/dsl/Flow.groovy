package com.pega.dsl

class Flow extends Rule {
    String flowType = 'Work'
    List<FlowShape> shapes = []
    List<FlowConnector> connectors = []

    Flow() {
        this.type = 'Flow'
    }
    
    Flow(String name) {
        this()
        this.name = name
    }
    
    /**
     * Return the first shape with the provided name or null if not found.
     */
    def getShape(String name) {
        return shapes.find { it?.name == name }
    }

    def work() {
        this.flowType = 'Work'
    }

    def screen() {
        this.flowType = 'Screen'
    }

    def subFlow() {
        this.flowType = 'SubFlow'
    }

    def start(String name = 'Start', Closure closure = null) {
        def shape = new StartShape(name: name)
        if (closure) {
            def _prev = PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get()
            PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(shape)
            try {
                closure.delegate = shape
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call()
            } finally {
                if (_prev != null) PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(_prev) else PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
            }
        }
        shapes.add(shape)
        return shape
    }

    def assignment(String name, Closure closure = null) {
        def shape = new AssignmentShape(name: name)
        if (closure) {
            def _prev = PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get()
            PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(shape)
            try {
                closure.delegate = shape
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call()
            } finally {
                if (_prev != null) PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(_prev) else PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
            }
        }
        shapes.add(shape)
        return shape
    }

    def decision(String name, Closure closure = null) {
        def shape = new DecisionShape(name: name)
        if (closure) {
            def _prev = PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get()
            PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(shape)
            try {
                closure.delegate = shape
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call()
            } finally {
                if (_prev != null) PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(_prev) else PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
            }
        }
        shapes.add(shape)
        return shape
    }

    def utility(String name, String activity, Closure closure = null) {
        def shape = new UtilityShape(name: name, activity: activity)
        if (closure) {
            def _prev = PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get()
            PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(shape)
            try {
                closure.delegate = shape
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call()
            } finally {
                if (_prev != null) PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(_prev) else PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
            }
        }
        shapes.add(shape)
        return shape
    }

    def connector(String name, String activity, Closure closure = null) {
        def shape = new ConnectorShape(name: name, connector: activity)
        if (closure) {
            def _prev = PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get()
            PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(shape)
            try {
                closure.delegate = shape
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call()
            } finally {
                if (_prev != null) PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(_prev) else PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
            }
        }
        shapes.add(shape)
        return shape
    }

    def subProcess(String name, String subFlow, Closure closure = null) {
        def shape = new SubProcessShape(name: name, subFlow: subFlow)
        if (closure) {
            def _prev = PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get()
            PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(shape)
            try {
                closure.delegate = shape
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call()
            } finally {
                if (_prev != null) PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(_prev) else PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
            }
        }
        shapes.add(shape)
        return shape
    }

    def end(String name = 'End', Closure closure = null) {
        def shape = new EndShape(name: name)
        if (closure) {
            def _prev = PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get()
            PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(shape)
            try {
                closure.delegate = shape
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call()
            } finally {
                if (_prev != null) PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(_prev) else PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
            }
        }
        shapes.add(shape)
        return shape
    }

    def connect(String from, String to, String condition = '') {
        connectors.add(new FlowConnector(from: from, to: to, condition: condition))
    }
}

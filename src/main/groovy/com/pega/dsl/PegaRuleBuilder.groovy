package com.pega.dsl

class PegaRuleBuilder {
    // Conservative fallbacks so closure.call/doCall that accidentally resolve to the rule
    // builder won't break nested DSL execution.
    def doCall(Object... args) { this }
    def methodMissing(String name, Object[] args) {
        if (name == 'call' && args != null && args.length >= 1 && args[0] instanceof String) {
            return this
        }
        throw new MissingMethodException(name, this.class, args)
    }

    // NOTE: invokeMethod intentionally not overridden. We only forward
    // through methodMissing which keeps dispatch simpler and avoids
    // accidental recursion between meta-class handlers.
 
    final ThreadLocal<Object> CURRENT_DELEGATE = PegaDslCore.CURRENT_DELEGATE

    def findOwnerDelegateOfType(Closure closure, Class type) { PegaDslCore.findOwnerDelegateOfType(closure, type) }

    def normalizeCandidate(String raw) { PegaDslCore.normalizeCandidate(raw) }

    private withNewDelegate(delegate, closure) {
        CURRENT_DELEGATE.set(delegate)
        try {
            // Simpler and safer: set the closure's delegate/resolveStrategy directly.
            // Rehydration caused ambiguous owner/thisObject interactions in tests.
            closure.delegate = delegate
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        } finally {
            CURRENT_DELEGATE.remove()
        }
        return delegate
    }

    private withNewDelegateOwnerFirst(delegate, closure) {
        // Rehydrate the closure so owner/thisObject/delegate all point to
        // the provided delegate. This avoids ambiguous method resolution
        // where the test closure's owner could intercept calls.
        return PegaDslCore.callWithDelegate(delegate, closure, Closure.DELEGATE_FIRST)
    }

    private withPrevDelegate(delegate, closure) {
        def _prev = CURRENT_DELEGATE.get()
        CURRENT_DELEGATE.set(delegate)
        try {
            closure.delegate = delegate
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        } finally {
            if (_prev != null) CURRENT_DELEGATE.set(_prev) else CURRENT_DELEGATE.remove()
        }
        return delegate
    }

    private callDelegate(delegate, closure) {
        closure.delegate = delegate
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        return delegate
    }

    private currentDelegate() { CURRENT_DELEGATE.get() }


    def application(String name, Closure closure) {
        def app = new Application(name: name)
        withNewDelegateOwnerFirst(app, closure)
        return app
    }

    def ruleset(name, closure) { 
        def d = currentDelegate()
        if (d instanceof Application) {
            return d.ruleset(name, closure)
        }
        withNewDelegateOwnerFirst(new Ruleset(name: name), closure)
    }

    def activity(String name, Closure closure) {
        def activity = new Activity(name: name)
        def builder = new ActivityBuilder(activity)
        withNewDelegateOwnerFirst(builder, closure)
        return activity
    }

    def decisionTable(String name, Closure closure) {
        def table = new DecisionTable(name: name)
        def builder = new DecisionTableBuilder(table)
        withNewDelegateOwnerFirst(builder, closure)
        return table
    }

    def decisionTree(String name, Closure closure) {
        def tree = new DecisionTree(name: name)
        def builder = new DecisionTreeBuilder(tree)
        withNewDelegateOwnerFirst(builder, closure)
        return tree
    }

    def dataPage(String name, Closure closure) {
        def dataPage = new DataPage(name: name)
        def builder = new DataPageBuilder(dataPage)
        withNewDelegateOwnerFirst(builder, closure)
        return dataPage
    }

    def when(String name, Closure closure) {
        def whenCondition = new WhenCondition(name: name)
        def builder = new WhenConditionBuilder(whenCondition)
        withNewDelegateOwnerFirst(builder, closure)
        return whenCondition
    }

    def dataTransform(String name, Closure closure) {
        def transform = new DataTransform(name: name)
        def builder = new DataTransformBuilder(transform)
        withNewDelegateOwnerFirst(builder, closure)
        return transform
    }

    def property(String name, Closure closure) {
        def property = new Property(name: name)
        def builder = new PropertyBuilder(property)
        withNewDelegateOwnerFirst(builder, closure)
        return property
    }

    def section(String name, Closure closure) {
        def section = new Section(name: name)
        def builder = new SectionBuilder(section)
        withNewDelegateOwnerFirst(builder, closure)
        return section
    }

    def harness(String name, Closure closure) { withNewDelegate(new Harness(name: name), closure) }

    def flow(String name, Closure closure) { withNewDelegate(new Flow(name: name), closure) }

    def correspondence(String name, Closure closure) { callDelegate(new Correspondence(name: name), closure) }

    def restConnector(String name, Closure closure) { withPrevDelegate(new RESTConnector(name: name), closure) }

    def soapConnector(String name, Closure closure) { withPrevDelegate(new SOAPConnector(name: name), closure) }

    def restService(String name, Closure closure) { withPrevDelegate(new RESTService(name: name), closure) }

    def testCase(String name, Closure closure) { withNewDelegate(new TestCase(name: name), closure) }

    def accessGroup(String name, Closure closure) { callDelegate(new AccessGroup(name: name), closure) }

    def accessRole(String name, Closure closure) { callDelegate(new AccessRole(name: name), closure) }



    def database(String name, Closure closure) { withNewDelegate(new Database(name: name), closure) }

    def authenticationProfile(String name, Closure closure) {
        callDelegate(new AuthenticationProfile(name: name), closure)
    }
}

package org.example

import groovy.transform.CompileStatic

class ActivityBuilder {
    Activity act

    static Activity activity(Closure c) {
        def b = new ActivityBuilder()
        b.act = new Activity()
        c.delegate = b
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c()
        return b.act
    }

    void name(String n) { act.name = n }

    void step(Closure c) {
        def s = new Step()
        def d = new Expando()
        d.id = { int v -> s.id = v }
        d.method = { String m -> s.method = m }
        d.param = { String k, Object v -> s.params[k] = v }
        d.params = { Map m -> s.params.putAll(m) }
        d.desc = { String t -> s.description = t }

        // Pega-like fields
        d.stepPage = { String v -> s.stepPage = v }
        d.page = { String v -> s.stepPage = v }   // alias
        d.on = { String v -> s.stepPage = v }     // alias
        d.pageClass = { String v -> s.pageClass = v }
        // allow setting when condition via 'when' or 'whenCondition'
        d.when = { def cond -> s.whenCondition = cond }
        d.whenCondition = { def cond -> s.whenCondition = cond }
        d.returns = { boolean b -> s.returns = b }
        d.returnPage = { String p -> s.returnPage = p }
        d.required = { boolean b -> s.required = b }
        d.stepType = { String t -> s.stepType = t }

        // Loop / Jump DSL
        d.label = { String lbl -> s.label = lbl }
        d.loop = { boolean b -> s.loop = b }
        d.loopCondition = { def cond -> s.loopCondition = cond }
        d.loopCount = { int n -> s.loopCount = n }
        d.jumpTo = { String target -> s.jumpTo = target }
        d.jumpCondition = { def cond -> s.jumpCondition = cond }

        // Precondition DSL
        d.precondition = { def cond -> s.precondition = cond }
        d.preconditionNegate = { boolean b -> s.preconditionNegate = b }

        c.delegate = d
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c()
        act.addStep(s)
    }
}

package com.pega.dsl

class Ruleset extends Rule {
    List<String> rules = []
    Application parentApplication

    Ruleset() { this.type = 'Ruleset' }

    /**
     * Create a nested rule inside this ruleset. This is a compact dispatcher
     * used by the tests which call `rule('activity', 'Name') { ... }`.
     */
    def rule(String type, String name, Closure closure) {
        // record the rule name (tests don't assert on content but we keep it)
        this.rules << name
        switch (type) {
            case 'activity':
                def activity = new Activity(name: name)
                def builder = new ActivityBuilder(activity)
                PegaDslCore.callWithDelegate(builder, closure, Closure.DELEGATE_FIRST)
                return activity
            case 'section':
                def section = new Section(name: name)
                def builder = new SectionBuilder(section)
                PegaDslCore.callWithDelegate(builder, closure, Closure.DELEGATE_FIRST)
                return section
            case 'flow':
                def flow = new Flow(name: name)
                PegaDslCore.callWithDelegate(flow, closure, Closure.DELEGATE_FIRST)
                return flow
            case 'property':
                def property = new Property(name: name)
                def builder = new PropertyBuilder(property)
                PegaDslCore.callWithDelegate(builder, closure, Closure.DELEGATE_FIRST)
                return property
            default:
                // generic rule fallback
                def r = new Rule(type: type, name: name)
                PegaDslCore.callWithDelegate(r, closure, Closure.DELEGATE_FIRST)
                return r
        }
    }
}
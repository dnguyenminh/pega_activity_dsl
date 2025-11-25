package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreMoreTargetedSpec extends Specification {

    def "while-loop owner chain: can find a Closure owner when requested type is Closure"() {
        given:
        def inner = { -> }
        def outer = { -> }
        // make inner.owner == outer
        def innerRehydrated = inner.rehydrate(outer, outer, outer)

        expect:
        PegaDslCore.findOwnerDelegateOfType(innerRehydrated, Closure).is(outer)
    }

    def "while-loop owner chain: walks multiple closure owners and finds delegate deep in chain"() {
        given:
        def level3 = { -> }
        def level2 = { -> }
        def level1 = { -> }

    // attach delegate on level2 (we expect the lookup to find this)
    level2.delegate = new RepeatingGridElement()

    // wire owners as in other tests so the chain is level3 -> level2 -> level1
    level3.rehydrate(level2, level2, level2)
    level2.rehydrate(level1, level1, level1)

    def t = level3.rehydrate(level2, level2, level2)

    expect:
    PegaDslCore.findOwnerDelegateOfType(t, RepeatingGridElement).is(level2.delegate)
    }

    def "callWithDelegate returns delegate when closure is null and clears CURRENT_DELEGATE"() {
        given:
        def d = [name: 'x']

        when:
        def res = PegaDslCore.callWithDelegate(d, null)

        then:
        res.is(d)
        PegaDslCore.CURRENT_DELEGATE.get() == null
    }
}

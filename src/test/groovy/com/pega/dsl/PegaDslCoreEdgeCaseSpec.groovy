package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreEdgeCaseSpec extends Specification {

    def "immediateDelegate present but not instance returns null"() {
        given:
        def c = { -> }
        def immediate = 'notTheType'
        def ownerMap = [delegate: immediate]
        def target = c.rehydrate(ownerMap, ownerMap, ownerMap)

        expect:
        PegaDslCore.findOwnerDelegateOfType(target, RepeatingGridElement) == null
    }

    def "owner is instance of requested type (non-Closure) returns owner"() {
        given:
        def c = { -> }
        def owner = new RepeatingGridElement()
        def target = c.rehydrate(owner, owner, owner)

        expect:
        PegaDslCore.findOwnerDelegateOfType(target, RepeatingGridElement).is(owner)
    }

    def "owner-chain: first closure delegate non-matching, deeper closure delegate matches"() {
        given:
        def l3 = { -> }
        def l2 = { -> }
        def l1 = { -> }

        // create a rehydrated middle closure whose owner is l1
        l1.delegate = new RepeatingGridElement()
        def r2 = l2.rehydrate(l1, l1, l1)
        // assign a non-matching delegate on the rehydrated middle closure
        r2.delegate = 'nope'
        // rehydrate the outer closure to point at the rehydrated middle closure
        def t = l3.rehydrate(r2, r2, r2)

        expect:
        PegaDslCore.findOwnerDelegateOfType(t, RepeatingGridElement).is(l1.delegate)
    }
}

package com.pega.dsl

import groovy.util.Expando
import spock.lang.Specification

class PegaDslCoreFindOwnerSpec extends Specification {

    static class Marker {}

    def "returns owner delegate when owner's delegate is instance of requested type"() {
        given:
        def owner = new Expando(delegate: 'ownerString')
        def closure = { -> }
        def rehydrated = closure.rehydrate(owner, owner, owner.delegate)

        when:
        def res = PegaDslCore.findOwnerDelegateOfType(rehydrated, String)

        then:
        res == 'ownerString'
    }

    def "returns immediate delegate property when owner's delegate matches type"() {
        given:
        def owner = new Expando(delegate: 42)
        def closure = { -> }
        // owner is a non-Closure that exposes a 'delegate' property
        def rehydrated = closure.rehydrate(owner, owner, owner.delegate)

        when:
        def res = PegaDslCore.findOwnerDelegateOfType(rehydrated, Integer)

        then:
        res == 42
    }

    def "walks closure owner chain and finds matching delegate on ancestor closure"() {
        given:
        def marker = new Marker()
        def outer = { ->
            def inner = { -> }
            return inner
        }

        // create the inner closure from inside the outer so the owner chain is natural
        def innerClosure = outer()
        // now set the outer's delegate so the owner (outer) carries the marker
        outer.delegate = marker

        when:
    def res = PegaDslCore.findOwnerDelegateOfType(innerClosure, Marker)

        then:
        res.is(marker)
    }
}

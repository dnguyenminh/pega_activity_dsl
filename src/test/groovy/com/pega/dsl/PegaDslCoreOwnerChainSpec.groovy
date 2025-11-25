package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreOwnerChainSpec extends Specification {

    static class Marker {}


    def "findOwnerDelegateOfType finds immediate owner when owner itself matches"() {
        given:
        def marker = new Marker()

        def base = { -> }
        // rehydrate the closure so its owner is the marker instance
        def c = base.rehydrate(marker, marker, marker)

        expect:
        PegaDslCore.findOwnerDelegateOfType(c, Marker) == marker
    }

    def "findOwnerDelegateOfType finds immediate delegate when delegate matches"() {
    given:
    def marker = new Marker()
    def owner = { -> }
    owner.delegate = marker
    def c = { -> }
    def r = c.rehydrate(owner, owner, owner)

    expect:
    PegaDslCore.findOwnerDelegateOfType(r, Marker) == marker
    }

    def "findOwnerDelegateOfType walks owner chain to find deep delegate"() {

    given:
    def marker = new Marker()

    // Create a chain of closures: a <- b <- c <- d, then set a.delegate to marker
    def a = { -> }
    def b = { -> }
    def c = { -> }
    def d = { -> }

    // set ownership chain via rehydrate: b.owner = a, c.owner = bR, d.owner = cR
    def bR = b.rehydrate(a, a, a)
    def cR = c.rehydrate(bR, bR, bR)
    def dR = d.rehydrate(cR, cR, cR)

    a.delegate = marker

    // final closure whose owner chain goes through dR -> cR -> bR -> a
    def finalClosure = { -> }
    def finalR = finalClosure.rehydrate(dR, dR, dR)

    expect:
    PegaDslCore.findOwnerDelegateOfType(finalR, Marker) == marker
    }

}

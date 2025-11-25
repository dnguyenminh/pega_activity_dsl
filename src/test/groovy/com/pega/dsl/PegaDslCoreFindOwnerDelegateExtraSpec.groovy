package com.pega.dsl

import spock.lang.Specification

// file-scoped helper type to avoid local-class compile issues
class Exploder { Object getDelegate() { throw new RuntimeException('boom') } }

class PegaDslCoreFindOwnerDelegateExtraSpec extends Specification {

    def "deep owner chain finds a delegate several levels up"() {
        given:
        def target = [ok: true]
        def level3 = { -> }
        level3.delegate = target

        def level2 = { -> }
        def level1 = { -> }
        def inner = { -> }

        // chain: level1.owner = level2 ; level2.owner = level3
        def level2r = level2.rehydrate(level3, level3, level3)
        def level1r = level1.rehydrate(level2r, level2r, level2r)
        def innerr = inner.rehydrate(level1r, level1r, level1r)

        expect:
        def found = PegaDslCore.findOwnerDelegateOfType(innerr, Map)
        found instanceof Map
        found.ok == true
    }

    def "deep owner chain swallows delegate getter exceptions and continues"() {
    given:
    def bad = new Exploder()
    def level2 = { -> }
    def level1 = { -> }
    def inner = { -> }

    def level2r = level2.rehydrate(bad, bad, bad)
    def level1r = level1.rehydrate(level2r, level2r, level2r)
    def innerr = inner.rehydrate(level1r, level1r, level1r)

        expect:
        // should not propagate and should simply return null when no other match
        PegaDslCore.findOwnerDelegateOfType(innerr, Map) == null
    }
}

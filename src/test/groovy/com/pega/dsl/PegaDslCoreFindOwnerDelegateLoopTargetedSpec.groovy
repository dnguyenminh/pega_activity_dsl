package com.pega.dsl

import spock.lang.Specification

// Unique helper to avoid duplicate class names
class ExplodingDelegateOwner {
    def getDelegate() { throw new RuntimeException('boom-delegate') }
}

class PegaDslCoreFindOwnerDelegateLoopTargetedSpec extends Specification {

    def "while-loop returns the first closure owner that matches the requested type"() {
        given:
        def level2 = { -> }
        def level1 = { -> }
        def inner = { -> }

    // wire owners: level1.owner = level2 ; inner.owner = level1
    // rehydrate with consistent owner/thisObject/delegate like other specs to avoid spock/runtime differences
    def level1r = level1.rehydrate(level2, level2, level2)
    def innerr = inner.rehydrate(level1r, level1r, level1r)

    expect:
    // the immediate owner is level1r and should be returned when asking for Closure
    def r1 = PegaDslCore.findOwnerDelegateOfType(innerr, Closure)
    r1 != null
    r1 instanceof Closure
    }

    def "while-loop finds a delegate on a deeper closure owner"() {
        given:
        def level2 = { -> }
        level2.delegate = [magic: true]
        def level1 = { -> }
        def inner = { -> }

    // level1.owner = level2 ; inner.owner = level1
    def level1r = level1.rehydrate(level2, level2, level2)
    def innerr = inner.rehydrate(level1r, level1r, level1r)

    expect:
    // the deeper owner's delegate (a Map) should be found
    def r2 = PegaDslCore.findOwnerDelegateOfType(innerr, Map)
    r2 != null
    r2 instanceof Map
    r2.magic == true
    }

    def "while-loop swallows exceptions from delegate getter and continues"() {
        given:
        def bad = new ExplodingDelegateOwner()
        def level1 = { -> }
        def inner = { -> }

    // level1.owner = bad ; inner.owner = level1
    def level1r = level1.rehydrate(bad, bad, bad)
    def innerr = inner.rehydrate(level1r, level1r, level1r)

        expect:
        // despite the bad owner's getter throwing, the method should not propagate and should return null
        PegaDslCore.findOwnerDelegateOfType(innerr, Map) == null
    }
}

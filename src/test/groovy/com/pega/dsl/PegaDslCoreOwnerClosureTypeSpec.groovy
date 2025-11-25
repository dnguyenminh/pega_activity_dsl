package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreOwnerClosureTypeSpec extends Specification {

    def "findOwnerDelegateOfType returns closure when owner chain contains Closure and type is Closure"() {
        given:
        // inner closure we'll try to find
        def target = { -> 'target' }

        // an owner closure whose delegate is the target closure
        def owner = { -> 'owner' }
        owner = owner.rehydrate(owner, owner, target)

        // outer closure whose owner is the owner closure
        def outer = { -> 'outer' }
        outer = outer.rehydrate(owner, outer, null)

        when:
        def res = PegaDslCore.findOwnerDelegateOfType(outer, Closure)

        then:
        // we expect either the closure instance or its delegate to be found
        res != null
        res instanceof Closure
    }

    def "findOwnerDelegateOfType finds delegate closure when immediate owner has a closure delegate"() {
        given:
        def d = { -> 'd' }
        // create an owner object that exposes a 'delegate' property pointing to the closure
        def o = new Expando(delegate: d)
        // create a closure whose owner is o (the immediate owner's 'delegate' should be found)
        def c = { -> 'c' }.rehydrate(o, o, null)

        when:
        def res = PegaDslCore.findOwnerDelegateOfType(c, Closure)

        then:
        res != null
        res instanceof Closure
    }
}

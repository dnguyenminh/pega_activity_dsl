package com.pega.dsl

import groovy.util.Expando
import spock.lang.Specification

class PegaDslCoreBranchSpec extends Specification {

    def "callWithDelegate returns delegate when closure is null"() {
        given:
        def obj = [a:1]

        expect:
        PegaDslCore.callWithDelegate(obj, null) == obj
    }

    def "findOwnerDelegateOfType returns owner closure when owner is Closure and matches Closure.class"() {
        given:
        def outer = { ->
            def inner = { -> }
            return inner
        }

        def innerClosure = outer()

        expect:
        PegaDslCore.findOwnerDelegateOfType(innerClosure, Closure).is(outer)
    }

    def "findOwnerDelegateOfType returns owner instance when owner is instance of requested type"() {
        given:
        def owner = new Expando()
        def closure = { -> }
        def re = closure.rehydrate(owner, owner, null)

        expect:
        PegaDslCore.findOwnerDelegateOfType(re, owner.getClass()) == owner
    }
}

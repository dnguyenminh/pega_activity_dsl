package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreFindOwnerWhileSpec extends Specification {

    def "findOwnerDelegateOfType finds a closure owner when type matches the closure class in the chain"() {
        given:
        def inner = { -> 'inner' }
        def mid = { -> 'mid' }
        def outer = { -> 'outer' }

        // arrange owner chain: inner.owner -> mid, mid.owner -> outer
        def innerRe = inner.rehydrate(mid, mid, mid)
        def midRe = mid.rehydrate(outer, outer, outer)
        // rehydrate inner to set owner to midRe
        innerRe = inner.rehydrate(midRe, midRe, midRe)

        when:
        // ask for the type matching the midRe closure runtime class
        def found = PegaDslCore.findOwnerDelegateOfType(innerRe, midRe.getClass())

        then:
        // should return the closure instance (midRe)
        found == midRe
    }
}

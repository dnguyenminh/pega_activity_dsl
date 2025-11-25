package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreFindOwnerEdgeSpec extends Specification {

    static class ExplodingOwner {
        Object getDelegate() {
            throw new RuntimeException('boom delegate')
        }
    }

    def "findOwnerDelegateOfType swallows exceptions from immediate delegate getter"() {
        given:
        def closure = { -> 'x' }
        def owner = new ExplodingOwner()
        // rehydrate to set owner to our ExplodingOwner
        def re = closure.rehydrate(owner, owner, owner)

        when:
        def found = PegaDslCore.findOwnerDelegateOfType(re, String)

        then:
        // the delegate getter throws; method should swallow and return null
        found == null
    }
}

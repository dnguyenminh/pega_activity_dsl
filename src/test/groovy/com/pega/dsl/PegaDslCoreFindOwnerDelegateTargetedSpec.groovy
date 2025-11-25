package com.pega.dsl

import spock.lang.Specification

// Helper classes at file scope to avoid local-class compile issues in Spock feature methods
class OwnerWithDelegate2 {
    def delegate
}

class BadOwner2 {
    def getDelegate() { throw new RuntimeException('boom') }
}

class PegaDslCoreFindOwnerDelegateTargetedSpec extends Specification {

    def "findOwnerDelegateOfType returns the closure owner when owner is a Closure and matches the type"() {
        given:
        def owner = { -> }
        def inner = { -> }
        // rehydrate inner so that owner becomes the owner
        def innerRe = inner.rehydrate(null, owner, null)

        expect:
        PegaDslCore.findOwnerDelegateOfType(innerRe, Closure) == owner
    }

    def "findOwnerDelegateOfType returns owner's delegate when owner's delegate matches requested type"() {
        given:
        def owner = { -> }
        owner.delegate = [found: true]
        def inner = { -> }
        def innerRe = inner.rehydrate(null, owner, null)

        expect:
        PegaDslCore.findOwnerDelegateOfType(innerRe, Map) == owner.delegate
    }

    def "findOwnerDelegateOfType checks immediate non-closure owner.delegate property"() {
        given:
        def owner = new OwnerWithDelegate2(delegate: [x:1])
        def inner = { -> }
        def innerRe = inner.rehydrate(null, owner, null)

        expect:
        PegaDslCore.findOwnerDelegateOfType(innerRe, Map) == owner.delegate
    }

    def "findOwnerDelegateOfType swallows exceptions from owner.getDelegate and returns null"() {
        given:
        def owner = new BadOwner2()
        def inner = { -> }
        def innerRe = inner.rehydrate(null, owner, null)

        expect:
        PegaDslCore.findOwnerDelegateOfType(innerRe, Map) == null
    }
}

package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreFindOwnerMoreSpec extends Specification {

    static class TargetType {}

    def "finds a closure owner by its runtime class in the owner chain"() {
        given: "a mid closure and an inner closure whose owner is the mid closure"
        def mid = { -> /* marker closure */ }
        // rehydrate mid so it's a plain closure instance with itself as delegate/owner
        mid = mid.rehydrate(mid, mid, mid)

        def inner = { -> }
        inner = inner.rehydrate(mid, mid, mid) // inner.owner == mid

        when:
        def found = PegaDslCore.findOwnerDelegateOfType(inner, mid.getClass())

        then:
        // should find the mid closure instance on the owner chain
        found == mid
    }

    def "finds a delegate object on a closure in the owner chain"() {
        given: "an owner closure whose delegate is an instance of TargetType"

    def ownerClosure = { -> }
    def target = new TargetType()
    ownerClosure = ownerClosure.rehydrate(ownerClosure, ownerClosure, ownerClosure)
    // explicitly set the delegate to ensure the delegate is the target
    ownerClosure.delegate = target

        def inner = { -> }
        inner = inner.rehydrate(ownerClosure, ownerClosure, ownerClosure)

    expect:
    // sanity-check: ownerClosure has the expected delegate
    ownerClosure.delegate == target

    when:
    def found = PegaDslCore.findOwnerDelegateOfType(inner, target.getClass())

    then:
    found == target
    }
}

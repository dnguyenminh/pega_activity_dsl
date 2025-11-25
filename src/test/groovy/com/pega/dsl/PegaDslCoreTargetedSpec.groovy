package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreTargetedSpec extends Specification {

    def "findOwnerDelegateOfType finds immediate delegate on inner closure"() {
        given:
        // follow existing test patterns: set delegate on the owner (map) and rehydrate closure
        def c = { -> }
    def immediate = new RepeatingGridElement()
    def ownerMap = [delegate: immediate]
    def target = c.rehydrate(ownerMap, ownerMap, ownerMap)

    expect:
    PegaDslCore.findOwnerDelegateOfType(target, RepeatingGridElement).is(immediate)
    }

    def "normalizeCandidate trims matching parentheses until stable"() {
        when:
        def res = PegaDslCore.normalizeCandidate('(((  abc  )))')

        then:
        // ensure inner content remains and parentheses removed
        res.contains('abc')
        !res.contains('(')
        !res.contains(')')
    }

}

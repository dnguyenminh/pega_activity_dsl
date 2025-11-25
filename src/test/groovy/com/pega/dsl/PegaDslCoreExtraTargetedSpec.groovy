package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreExtraTargetedSpec extends Specification {

    def "normalizeCandidate decodes numeric HTML entities and unicode escapes"() {
        expect:
        PegaDslCore.normalizeCandidate('&#65;') == 'A'
        PegaDslCore.normalizeCandidate('\\u0041') == 'A'
    }

    def "normalizeCandidate handles __dot__ and __space__ tokens"() {
        expect:
        PegaDslCore.normalizeCandidate('a__dot__b__space__c') == 'a.b c'
        // also ensure percent-20 and $dot variants are normalized
        PegaDslCore.normalizeCandidate('a%20b') == 'a b'
        PegaDslCore.normalizeCandidate('a$dot$b') == 'a.b'
    }

    def "findOwnerDelegateOfType walks multiple closure-owner levels to find a delegate"() {
        given:
        def rg = new RepeatingGridElement()

        // level1: closure whose delegate points to the target RepeatingGridElement
        def level1 = { -> }
        level1.delegate = rg

        // level2: a closure rehydrated to have owner/delegate = level1
        def level2 = { -> }.rehydrate(level1, level1, level1)

        // inner: a closure rehydrated to have owner/delegate = level2
        def inner = { -> }.rehydrate(level2, level2, level2)

        expect:
        PegaDslCore.findOwnerDelegateOfType(inner, RepeatingGridElement).is(rg)
    }
}

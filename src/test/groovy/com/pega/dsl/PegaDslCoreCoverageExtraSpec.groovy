package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreCoverageExtraSpec extends Specification {

    def "normalizeCandidate handles URL-decoding and entity/unicode escapes"() {
        expect:
        PegaDslCore.normalizeCandidate('a%20b') == 'a b'
        PegaDslCore.normalizeCandidate('&#65;') == 'A'
        PegaDslCore.normalizeCandidate('&#x41;') == 'A'
        PegaDslCore.normalizeCandidate('\\u0041') == 'A'
        PegaDslCore.normalizeCandidate('__dot__') == '.'
    // normalizeCandidate will collapse to a single space which is trimmed -> empty string
    PegaDslCore.normalizeCandidate('__space__') == ''
    // leading/trailing whitespace will be collapsed/trimmed
    PegaDslCore.normalizeCandidate('%20dot') == 'dot'
    }

    def "findOwnerDelegateOfType finds matching owner object and delegates in owner chain"() {
        given:
    // use Map instances rather than named classes to avoid local class issues
    // owner is a plain object of the target type (Map)
    def ownerObj = [a:1]
    def c1 = { -> 'x' }
    def c1r = c1.rehydrate(null, ownerObj, null)

    expect:
    PegaDslCore.findOwnerDelegateOfType(c1r, Map) == ownerObj

    when: "delegate on an owner closure matches"
    def ownerClosure = { -> 'owner' }
    ownerClosure.delegate = [k:'v']
    def child = { -> 'child' }
    // set child.owner = ownerClosure
    def childRe = child.rehydrate(null, ownerClosure, null)

    then:
    def found = PegaDslCore.findOwnerDelegateOfType(childRe, Map)
    found != null
    found instanceof Map

    and: "nothing matches returns null"
    def plain = { -> 'p' }
    PegaDslCore.findOwnerDelegateOfType(plain, String) == null
    }
}

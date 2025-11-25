package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreMoreEdgeSpec extends Specification {

    static class BadOwner {
        Object getDelegate() { throw new RuntimeException('boom') }
    }

    def "normalizeCandidate decodes hex numeric entity and space token variant"() {
        expect:
        PegaDslCore.normalizeCandidate('&#x41;') == 'A'
        PegaDslCore.normalizeCandidate('a$space$b') == 'a b'
    PegaDslCore.normalizeCandidate('a$space') == 'a'
    }

    def "findOwnerDelegateOfType ignores thrown delegate getter and continues"() {
        given:
        def bad = new BadOwner()
        def cl = { -> }.rehydrate(bad, bad, bad)

        expect:
        PegaDslCore.findOwnerDelegateOfType(cl, RepeatingGridElement) == null
    }

    def "findOwnerDelegateOfType returns null when nothing matches"() {
        given:
        def cl = { -> }
        expect:
        PegaDslCore.findOwnerDelegateOfType(cl, RepeatingGridElement) == null
    }
}

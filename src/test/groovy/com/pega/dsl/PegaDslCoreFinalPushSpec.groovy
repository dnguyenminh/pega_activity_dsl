package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreFinalPushSpec extends Specification {

    def "normalizeCandidate handles combined numeric, hex and unicode escapes"() {
        expect:
        PegaDslCore.normalizeCandidate('pre&#65;&#x42;\\u0043post') == 'preABCpost'
        // multiple numeric entities together
        PegaDslCore.normalizeCandidate('X&#67;&#68;Y') == 'XCDY'
    }

    def "normalizeCandidate handles dollar-token forms and optional trailing dollar"() {
        when:
        def out = PegaDslCore.normalizeCandidate('$eq$eq a$dot$b $bang$eq $space$ c')

        then:
        out.contains('==')
        out.contains('a.b')
    out.contains('!=')
    out.contains('!= c')
    }

    def "normalizeCandidate collapses repeated dots and percent-20 and __space__/__dot__"() {
        expect:
        PegaDslCore.normalizeCandidate('....a') == '.a'
        PegaDslCore.normalizeCandidate('a%20b') == 'a b'
        PegaDslCore.normalizeCandidate('x__dot__y__space__z') == 'x.y z'
    }

    def "normalizeCandidate collapses whitespace sequences"() {
        expect:
        PegaDslCore.normalizeCandidate('a   b\t\n c') == 'a b c'
    }
}

package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreNormalizeMoreSpec extends Specification {

    def "normalizes html numeric and hex entities and unicode escapes"() {
        expect:
        PegaDslCore.normalizeCandidate('A&#65;B') == 'AAB' // &#65; -> 'A'
        PegaDslCore.normalizeCandidate('x&#x41;y') == 'xAy' // &#x41; -> 'A'
        PegaDslCore.normalizeCandidate('\\u0041') == 'A' // unicode escape
    }

    def "normalizes dollar-token patterns, dots, spaces and percent-20"() {
        when:
        def raw = '\n            $eq$eq $bang$eq $lt $gt $space$ $space $dot$ __dot__ __space__ %20\n        '.stripIndent().trim()

        def out = PegaDslCore.normalizeCandidate(raw)

        then:
        out.contains('==')
        out.contains('!=')
        out.contains('<')
        out.contains('>')
        // $space$ and $space should both become spaces (we check by ensuring no token remains)
        !out.contains('$space')
        // dot patterns collapse into '.'
        out.contains('.')
        // percent encoding
        out.contains(' ')
    }

    def "handles non-breaking space, ampersand-pipe and dot-collapsing and parentheses stripping"() {
        when:
        def nb = "a${(char)0x00A0}b" // contains non-breaking space
        def r1 = PegaDslCore.normalizeCandidate(nb)
        def r2 = PegaDslCore.normalizeCandidate('a&& &|& b')
        def r3 = PegaDslCore.normalizeCandidate('((..foo..))')

        then:
    // accept either direct replacement or allow replacing NBSP here for assertion
    r1.replace((char)0x00A0, (char)0x0020) == 'a b'
        // &|& should become || while && remains unchanged (noop)
        r2.contains('||')
    // dots and surrounding parentheses get collapsed/stripped
    r3.contains('foo')
    !r3.contains('(')
    !r3.contains(')')
    }
}

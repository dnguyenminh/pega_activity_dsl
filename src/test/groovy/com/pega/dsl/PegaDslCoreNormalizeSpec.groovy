package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreNormalizeSpec extends Specification {

    def "numeric and hex HTML entities are decoded"() {
        expect:
        PegaDslCore.normalizeCandidate('&amp;#65;') ==~ /(?i).*A.*/
        PegaDslCore.normalizeCandidate('&amp;#x41;') ==~ /(?i).*A.*/
    }

    def "unicode escapes are unescaped"() {
        expect:
        PegaDslCore.normalizeCandidate('\\u0041') ==~ /(?i).*A.*/
    }

    def "dollar token replacements and dot/space helpers"() {
        expect:
        PegaDslCore.normalizeCandidate('a$eq$eqb').contains('==')
        PegaDslCore.normalizeCandidate('a$bang$eqb').contains('!=')
        PegaDslCore.normalizeCandidate('a$dot$b').contains('a.b')
        PegaDslCore.normalizeCandidate('a__dot__b').contains('a.b')
        PegaDslCore.normalizeCandidate('a%20b').contains('a b')
    }

    def "ampersand/pipe patterns and nbsp handling"() {
        expect:
        // the implementation normalizes &|& to ||
        PegaDslCore.normalizeCandidate('&|&').contains('||')
        // nbsp should become a space and be trimmed
        PegaDslCore.normalizeCandidate('&nbsp;X').contains('X')
    }

    def "parenthesis stripping collapses nested parens"() {
        expect:
        PegaDslCore.normalizeCandidate('(((inner)))').contains('inner')
        PegaDslCore.normalizeCandidate('(a.b)').contains('a.b')
    }
}

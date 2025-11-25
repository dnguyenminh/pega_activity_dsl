package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreExtraSpec extends Specification {

    static class MyType {}

    static class OwnerWithDelegate {
        def delegate
        Closure c = { -> }
    }

    static class Holder {
        Closure c = { -> }
    }

    def "normalizeCandidate handles tokens and whitespace"() {
        given:
        def core = new PegaDslCore()

        when:
        def r1 = core.normalizeCandidate(" a.b ")
        def r2 = core.normalizeCandidate("&|&")
        def r3 = core.normalizeCandidate("\u00A0Z")

        then:
        r1 != null
        r2 != null
        r3 != null
    }

    def "findOwnerDelegateOfType returns null for non-matching chain"() {
        given:
        def c = { -> }
        def result = PegaDslCore.findOwnerDelegateOfType(c, Map)

        expect:
        result == null || result instanceof Map == false
    }

    def "findOwnerDelegateOfType finds non-closure owner instance"() {
        given:
        def holder = new Holder()
        // closure defined on the instance -> owner should be the instance
        def c = holder.c

        expect:
        PegaDslCore.findOwnerDelegateOfType(c, Holder) == holder
    }

    def "findOwnerDelegateOfType finds immediate delegate on non-closure owner"() {
        given:
        def owner = new OwnerWithDelegate()
        owner.delegate = new MyType()
        def c = owner.c

        expect:
        PegaDslCore.findOwnerDelegateOfType(c, MyType) == owner.delegate
    }

    def "findOwnerDelegateOfType walks nested closure owner chain and finds delegate"() {
        given:
        def outer = { ->
            return { ->
            }
        }
        def inner = outer.call()
        // attach a delegate object to the outer closure
        outer.delegate = new MyType()

        expect:
        PegaDslCore.findOwnerDelegateOfType(inner, MyType) == outer.delegate
    }

    def "findOwnerDelegateOfType returns null for null closure"() {
        expect:
        PegaDslCore.findOwnerDelegateOfType(null, MyType) == null
    }

    def "findOwnerDelegateOfType can find a Closure owner when requested type is Closure"() {
        given:
        def outer = { ->
            return { ->
            }
        }
        def inner = outer.call()

        expect:
        PegaDslCore.findOwnerDelegateOfType(inner, Closure) == outer
    }

    def "normalizeCandidate handles many token forms and unicode/encodings"() {
        expect:
        PegaDslCore.normalizeCandidate(null) == ''
        PegaDslCore.normalizeCandidate('a%20b') == 'a b'
        PegaDslCore.normalizeCandidate('&nbsp;X') == 'X'
        PegaDslCore.normalizeCandidate('a$eq$eqb') == 'a==b'
        PegaDslCore.normalizeCandidate('a$bang$eqb') == 'a!=b'
        def ltgt = PegaDslCore.normalizeCandidate('a$lt$b$gt$c')
        ltgt.contains('<') && ltgt.contains('>') && ltgt.indexOf('<') < ltgt.indexOf('b') && ltgt.indexOf('>') > ltgt.indexOf('b')
        PegaDslCore.normalizeCandidate('a$space$b') == 'a b'
        PegaDslCore.normalizeCandidate('a$dot$b') == 'a.b'
        PegaDslCore.normalizeCandidate('x__dot__y') == 'x.y'
        PegaDslCore.normalizeCandidate('x__space__y') == 'x y'
        PegaDslCore.normalizeCandidate('%20leading') == 'leading'
        PegaDslCore.normalizeCandidate('literal\\u0041') == 'literalA'
        def pipe = PegaDslCore.normalizeCandidate('&|&')
        pipe.contains('||')
        def nameNorm = PegaDslCore.normalizeCandidate('name()')
        nameNorm.contains('name')
        PegaDslCore.normalizeCandidate('"quoted"') == 'quoted'
        PegaDslCore.normalizeCandidate("'q'") == 'q'
        PegaDslCore.normalizeCandidate('((surrounded))') == 'surrounded'
        PegaDslCore.normalizeCandidate('..lead..dot..') == '.lead.dot.'
        PegaDslCore.normalizeCandidate('a    b\tc') == 'a b c'
    }
}

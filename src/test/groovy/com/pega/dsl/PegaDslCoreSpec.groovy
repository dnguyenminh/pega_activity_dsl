package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreSpec extends Specification {

    static class D { String say() { 'ok' } }
    static class Target { String id = 't' }

    def "normalizeCandidate handles many mangled inputs"() {
        expect:
        PegaDslCore.normalizeCandidate(input) == expected

        where:
        input                     || expected
        null                      || ''
        '  abc  '                 || 'abc'
        '&#65;'                   || 'A'
        '&#x41;'                  || 'A'
        "'quoted'"              || 'quoted'
        'value()'                 || 'value'
        '((x))'                   || 'x'
        '..start'                 || '.start'
        'a..b'                    || 'a.b'
        '$eq$eq'                  || '=='
        '$bang$eq'                || '!='
    '$dot$'                   || '.'
    '__dot__'                 || '.'
        '\\u0041'               || 'A'
    }

    def "normalizeCandidate special token behaviors"() {
        expect:
        PegaDslCore.normalizeCandidate('&|&').contains('||')
        PegaDslCore.normalizeCandidate('$space$').trim() == ''
        PegaDslCore.normalizeCandidate('__space__').trim() == ''
        PegaDslCore.normalizeCandidate('%20').trim() == ''
    }

    def "callWithDelegate executes closure against provided delegate and cleans up threadlocal"() {
        given:
        def d = new D()
        def c = { -> say() }

        when:
        def result = PegaDslCore.callWithDelegate(d, c)

        then:
        result == 'ok'
        PegaDslCore.CURRENT_DELEGATE.get() == null
    }

    def "findOwnerDelegateOfType locates the rehydrated delegate"() {
        given:
        def t = new Target()
        def c = { -> /* noop */ }
        def r = c.rehydrate(t, t, t)

        expect:
        PegaDslCore.findOwnerDelegateOfType(r, Target).is(t)
    }
}



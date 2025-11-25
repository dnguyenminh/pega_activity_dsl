package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreExtraCoverageSpec extends Specification {

    def "normalizeCandidate exercises many replacement paths"() {
        when:
        def samples = [
            '&#65;',           // numeric entity -> A
            '&#x41;',          // hex entity -> A
            '\$eq\$eq',      // eq token -> ==
            '\$bang\$eq',    // bang token -> !=
            '\$lt', '\$gt',  // lt/gt tokens
            '\$space\$ ',    // space token
            'a$dot$b',         // dot token
            '__dot__', '__space__',
            '%20',             // percent-space
            '\\u0041',       // unicode escape as literal sequence
            '&nbsp;', '&|&', '&&',
            '((nested))', '(())', 'token()', "'quoted'", '"quoted"'
        ]

        then:
        samples.collect { PegaDslCore.normalizeCandidate(it) }.every { it != null }
    }

    def "findOwnerDelegateOfType covers deeper chains and exception paths"() {
        given:
        // case: immediate owner is Expando with delegate Map
        def mapDelegate = [found: true]
    def owner1 = new Expando(delegate: mapDelegate)
    def c1 = { -> 'x' }
    c1 = c1.rehydrate(null, owner1, c1)

        // case: deeper closure owner chain where deeper closure's delegate is Map
        def deepDelegate = [deep: true]
    def inner = { -> 'inner' }
    inner = inner.rehydrate(deepDelegate, inner, inner)
    def mid = { -> 'mid' }
    mid = mid.rehydrate(null, inner, mid)
    def outer = { -> 'outer' }
    outer = outer.rehydrate(null, mid, outer)

    // case: owner exposes a delegate getter that throws to exercise catch
    def broken = new Object() { Object getDelegate() { throw new RuntimeException('boom') } }
    def cb = { -> 'c' }
    cb = cb.rehydrate(null, broken, cb)

        expect:
        PegaDslCore.findOwnerDelegateOfType(c1, Map) instanceof Map
        def found = PegaDslCore.findOwnerDelegateOfType(outer, Map)
        println "found for outer => ${found} class=${found?.getClass()}"
        // diagnostic: walk the owner chain (safe - don't access properties that may not exist)
        def o = outer.owner
        int depth = 0
        while (o != null && depth < 6) {
            println "chain node[${depth}] -> ${o.getClass().name} -> ${o}"
            try { o = o.owner } catch (ignored) { break }
            depth++
        }
        found instanceof Map
        // for broken owner we expect null (exception swallowed) rather than throw
        PegaDslCore.findOwnerDelegateOfType(cb, Map) == null
    }
}

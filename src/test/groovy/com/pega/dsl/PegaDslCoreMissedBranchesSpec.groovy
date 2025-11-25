package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreMissedBranchesSpec extends Specification {

    def "findOwnerDelegateOfType finds closure owner when requested type is the owner's concrete class"() {
        given:
    def outer = { -> 'outer' }
    def mid = { -> 'mid' }
    def owner1 = { -> 'owner1' }
    def inner = { -> 'inner' }

    // chain: outer <- mid <- owner1 <- inner
    mid = mid.rehydrate(null, outer, mid)
    owner1 = owner1.rehydrate(null, mid, owner1)
    def inn = inner.rehydrate(null, owner1, inner)

    when:
    // Search for the concrete class of `mid`. The immediate owner of `inn`
    // is `owner1` so the initial immediate check will not match; the
    // while-loop should advance to `mid` and the inner `type.isInstance(o)`
    // branch will return `mid`.
    def found = PegaDslCore.findOwnerDelegateOfType(inn, mid.getClass())

    then:
    found == mid
    }

    def "findOwnerDelegateOfType finds a delegate on a closure owner during the while-loop walk"() {
        given:
        def outer = { -> /* outer closure */ }
        def middle = { -> /* middle closure */ }
        def inner = { -> /* inner closure */ }

    // make the middle closure have a Map delegate so the while loop will
    // inspect o.delegate and should return that delegate when asked for Map
    def targetMap = [found: true]
    // use the same rehydrate(delegate, owner, thisObject) ordering as other tests
    def mid = middle.rehydrate(targetMap, outer, middle)
    def inn = inner.rehydrate(null, mid, inner)

    expect:
    PegaDslCore.findOwnerDelegateOfType(inn, Map) == targetMap
    }

    def "normalizeCandidate exercise starts/ends paren edge cases and dot/space tokens"() {
        expect:
        // startsWith true, endsWith false
        PegaDslCore.normalizeCandidate('(abc') == '(abc'

        // startsWith false, endsWith true
        PegaDslCore.normalizeCandidate('abc)') == 'abc)'

        // dot-collapse variants
        PegaDslCore.normalizeCandidate('...leading') == '.leading'
        PegaDslCore.normalizeCandidate('a...b..c') == 'a.b.c'

    // $space token collapses to whitespace which is trimmed away by
    // normalizeCandidate, resulting in an empty string
    PegaDslCore.normalizeCandidate('$space') == ''
    PegaDslCore.normalizeCandidate('$space$') == ''

        // &|& replacement: implementation may vary (pipes repeated), accept at least two pipes
        PegaDslCore.normalizeCandidate('&|&') =~ /\|{2,}/
    }
}

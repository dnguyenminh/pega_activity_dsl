package com.pega.dsl

import spock.lang.Specification

class SectionBuilderSpec extends Specification {

    def cleanup() {
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "doCall returns builder instance"() {
        given:
        def sec = new Section()
        def sb = new SectionBuilder(sec)

        expect:
        sb.doCall('x').is(sb)
    }

    def "methodMissing handles explicit call(String) fallback and returns the builder"() {
        given:
        def sec = new Section()
        def sb = new SectionBuilder(sec)
        when: 'no closure provided, unknown-call should surface as MissingMethodException'
        sb.methodMissing('call', ['someName'] as Object[])

        then:
        thrown(groovy.lang.MissingMethodException)
    }

    def "methodMissing returns null for a candidate that normalizes to empty"() {
        given:
        def b = new SectionBuilder(new Section())
        when: 'candidate normalizes to empty and no matching method exists'
        b.methodMissing("' '", { -> /* noop */ })

        then:
        thrown(groovy.lang.MissingMethodException)
    }

    def "methodMissing routes to repeatingGrid for DetailsList and dot-prefixed names and accepts closure forms"() {
        given:
        def sec = new Section()
        def sb = new SectionBuilder(sec)

        when: "name contains DetailsList (case-insensitive) and a closure is provided as the last arg"
        def markerClosure = {
            if (delegate instanceof RepeatingGridElement) delegate.properties['marker'] = 'd1'
        }
        def grid1 = sb.methodMissing('DetailsList', [ markerClosure ] as Object[])

        then:
        grid1 instanceof RepeatingGridElement
        sec.elements.contains(grid1)
        grid1.pageList == '.DetailsList'
        sec.elements[-1].properties['marker'] == 'd1'

        when: "name already starts with a dot"
        def grid2 = sb.methodMissing('.CustomList', [ { -> /* noop */ } ] as Object[])

        then:
        grid2 instanceof RepeatingGridElement
        grid2.pageList == '.CustomList'

        when: "closure passed directly as non-array arg"
        def grid3 = sb.methodMissing('SomeList', { -> /* noop */ })

        then:
        grid3 instanceof RepeatingGridElement
        sec.elements.contains(grid3)
    }

    def "table delegates to repeatingGrid and executes closure on the grid delegate"() {
        given:
        def sec = new Section()
        def sb = new SectionBuilder(sec)

        when:
        def c3 = { if (delegate instanceof RepeatingGridElement) delegate.properties['marker'] = 'tbl' }
        def t = sb.table('MyList', c3)

        then:
        t instanceof RepeatingGridElement
        sec.elements[-1].pageList == 'MyList'
        sec.elements[-1].properties['marker'] == 'tbl'
    }
}

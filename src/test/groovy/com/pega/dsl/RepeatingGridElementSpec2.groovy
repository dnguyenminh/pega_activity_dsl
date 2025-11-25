package com.pega.dsl

import spock.lang.Specification

class RepeatingGridElementSpec2 extends Specification {

    def "column without closure adds column"() {
        given:
        def r = new RepeatingGridElement()

        when:
        def c = r.column('name', 'Name')

        then:
        r.columns.size() == 1
        c.property == 'name'
        c.label == 'Name'
    }

    def "column closure sets column attributes and CURRENT_DELEGATE removed after when prev null"() {
        given:
        def r = new RepeatingGridElement()

        when:
        r.column('amt', 'Amount') {
            dropdown()
            sortable()
            width(120)
            readOnly()
        }

        then:
        def c = r.columns[0]
        c.control == 'Dropdown'
        c.sortable
        c.width == 120
        c.readOnly
        // When there was no prior CURRENT_DELEGATE, the column call should remove it after
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get() == null
    }

    def "column preserves previous CURRENT_DELEGATE when set"() {
        given:
        def prev = new Object()
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(prev)
        def r = new RepeatingGridElement()

        when:
        // use the (String, String, Closure) overload so the closure matches the method signature
        r.column('x', '') {
            textInput()
        }

        then:
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get() == prev

        cleanup:
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
    }
}

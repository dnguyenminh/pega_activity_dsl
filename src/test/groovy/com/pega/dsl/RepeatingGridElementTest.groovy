package com.pega.dsl

import spock.lang.Specification

class RepeatingGridElementTest extends Specification {

    def setup() {
        // Ensure the delegate is clean before each test
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
    }

    def cleanup() {
        // Clean up the delegate after each test
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
    }

    def "should create a repeating grid element"() {
        when:
        def grid = new RepeatingGridElement()

        then:
        grid.type == 'Repeating Grid'
    }

    def "should set the page list"() {
        given:
        def grid = new RepeatingGridElement()

        when:
        grid.pageList = 'MyPageList'

        then:
        grid.pageList == 'MyPageList'
    }

    def "should add a simple column"() {
        given:
        def grid = new RepeatingGridElement()

        when:
        grid.column('Property1')

        then:
        grid.columns.size() == 1
        grid.columns[0].property == 'Property1'
        grid.columns[0].label == ''
    }

    def "should add a column with a label"() {
        given:
        def grid = new RepeatingGridElement()

        when:
        grid.column('Property2', 'My Label')

        then:
        grid.columns.size() == 1
        grid.columns[0].property == 'Property2'
        grid.columns[0].label == 'My Label'
    }

    def "should add a column with a closure"() {
        given:
        def grid = new RepeatingGridElement()

        when:
        grid.column('Property3') {
            readOnly()
        }

        then:
        grid.columns.size() == 1
        def column = grid.columns[0]
        column.property == 'Property3'
        column.properties['readOnly']
    }

    def "should manage delegate stack correctly within a closure"() {
        given:
        def grid = new RepeatingGridElement()
        def section = new Section()
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(section)

        when:
        grid.column('Property4') {
            // Inside the closure, the delegate should be the grid
            assert PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get() == grid
        }

        then:
        // After the closure, the delegate should be restored
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get() == section
    }

    def "should find owner delegate of type Section"() {
        given:
        def section = new Section()
        def foundDelegate = null
        def outerClosure
        def innerClosure

        outerClosure = {
            def grid = new RepeatingGridElement()
            innerClosure = {
                foundDelegate = PegaDeveloperUtilitiesDsl.findOwnerDelegateOfType(innerClosure, Section)
            }
            grid.column('Property5', 'Label5', innerClosure)
        }
        outerClosure.delegate = section
        outerClosure.resolveStrategy = Closure.DELEGATE_FIRST
        outerClosure.call()

        expect:
        foundDelegate == section
    }
}

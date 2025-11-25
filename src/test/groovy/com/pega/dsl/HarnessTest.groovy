package com.pega.dsl

import spock.lang.Specification

class HarnessTest extends Specification {

    def "should create harness with a name"() {
        when:
        def harness = new Harness(name: 'NewHarness')

        then:
        harness.name == 'NewHarness'
        harness.type == 'Harness'
    }

    def "should set the template"() {
        given:
        def harness = new Harness()

        when:
        harness.template 'MyTemplate'

        then:
        harness.template == 'MyTemplate'
    }

    def "should add a header element"() {
        given:
        def harness = new Harness()

        when:
        harness.header 'HeaderSection'

        then:
        harness.elements.size() == 1
        harness.elements[0].type == 'Header'
        harness.elements[0].content == 'HeaderSection'
    }

    def "should add a workArea element"() {
        given:
        def harness = new Harness()

        when:
        harness.workArea 'WorkSection'

        then:
        harness.elements.size() == 1
        harness.elements[0].type == 'Work Area'
        harness.elements[0].content == 'WorkSection'
    }

    def "should add a footer element"() {
        given:
        def harness = new Harness()

        when:
        harness.footer 'FooterSection'

        then:
        harness.elements.size() == 1
        harness.elements[0].type == 'Footer'
        harness.elements[0].content == 'FooterSection'
    }

    def "should add a navigation element"() {
        given:
        def harness = new Harness()

        when:
        harness.navigation 'NavSection'

        then:
        harness.elements.size() == 1
        harness.elements[0].type == 'Navigation'
        harness.elements[0].content == 'NavSection'
    }

    def "should add an includeSection element"() {
        given:
        def harness = new Harness()

        when:
        harness.includeSection 'IncludedSection'

        then:
        harness.elements.size() == 1
        harness.elements[0].type == 'Section'
        harness.elements[0].content == 'IncludedSection'
    }

    def "should add an element with a closure"() {
        given:
        def harness = new Harness()

        when:
        harness.header('ConfigurableHeader') {
            readOnly true
        }

        then:
        harness.elements.size() == 1
        def element = harness.elements[0]
        element.type == 'Header'
        element.content == 'ConfigurableHeader'
        element.readOnly
    }
}
package com.pega.dsl

import spock.lang.Specification

class HarnessSpec extends Specification {

    def "template and element helpers add elements and execute closures"() {
        given:
        def h = new Harness()

        expect:
        h.type == 'Harness'

        when:
        h.template('basic')
    h.header('Top') { delegate.parameter('title','T') }
    h.workArea('Main') { delegate.parameter('body','B') }
    h.footer('End') { delegate.parameter('note','N') }
    h.navigation('Nav') { delegate.parameter('link','L') }
    h.includeSection('Sec') { delegate.parameter('ref','R') }

        then:
        h.template == 'basic'
        h.elements.size() == 5
        h.elements*.type.containsAll(['Header','Work Area','Footer','Navigation','Section'])
        // closures should have been delegated to element instances
    h.elements.find { it.type == 'Header' }.parameters['title'] == 'T'
    h.elements.find { it.type == 'Work Area' }.parameters['body'] == 'B'
    h.elements.find { it.type == 'Footer' }.parameters['note'] == 'N'
    h.elements.find { it.type == 'Navigation' }.parameters['link'] == 'L'
    h.elements.find { it.type == 'Section' }.parameters['ref'] == 'R'
    }
}

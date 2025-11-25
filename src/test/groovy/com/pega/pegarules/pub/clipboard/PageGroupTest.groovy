package com.pega.pegarules.pub.clipboard

import spock.lang.Specification
import spock.lang.Ignore

@Ignore('flaky tests that interact with multi-constructor overloading; re-enable when fixed')
class PageGroupTest extends Specification {
    def "should set name and include page values"() {
    given:
    def a = [x: 1]
    def b = [y: 2]

    when:
    def pg = new PageGroup(['a': a, 'b': b])

        then:
        pg.getName() == 'PageGroup'
        pg.getPropertyObject('a') instanceof Page
        pg.getPropertyObject('b') instanceof Page
        pg.getPropertyObject('a').getString('x') == '1'
        pg.getPropertyObject('b').getString('y') == '2'
    }
}

package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class PageGroupSpec extends Specification {

    def "should create PageGroup with default constructor"() {
        when:
        def pageGroup = new PageGroup()

        then:
        pageGroup != null
        pageGroup instanceof PageGroup
    }

    def "should create PageGroup from map and return PageGroup name"() {
        given:
        def a = [x: 1]
        def b = [y: 2]

        when:
        def pg = new PageGroup(['a': a, 'b': b])

        then:
        pg.getName() == 'PageGroup'
        pg.getPropertyObject('a') instanceof ClipboardProperty
        pg.getPropertyObject('a').getPageValue() instanceof ClipboardPage
        pg.getPropertyObject('b') instanceof ClipboardProperty
        pg.getPropertyObject('b').getPageValue() instanceof ClipboardPage
    }

    def "should inherit AbstractClipboardPage methods"() {
        given:
        def pageGroup = new PageGroup()

        when:
        pageGroup.put("testProp", "testValue")

        then:
        pageGroup.getString("testProp") == "testValue"
    }

    def "should support page group operations"() {
        given:
        def pageGroup = new PageGroup()
        pageGroup.put("page1", "value1")
        pageGroup.put("page2", "value2")

        expect:
        pageGroup.getString("page1") == "value1"
        pageGroup.getString("page2") == "value2"
    }

    def "map constructor stores clipboard constructs verbatim"() {
        given:
        def nestedPage = new SimpleClipboardPage([alpha: 'A'])
        def nestedProperty = new SimpleClipboardProperty('beta', new Page([beta: 'B']))
        def payload = [pageEntry: nestedPage, propertyEntry: nestedProperty, scalarEntry: 'value']

        when:
        def group = new PageGroup(payload)

        then:
        def entrySet = group.entrySet()
        def pageEntryRecord = entrySet.find { it.key == 'pageEntry' }
        pageEntryRecord.value instanceof Page
        pageEntryRecord.value.getString('alpha') == 'A'
        entrySet.find { it.key == 'propertyEntry' }.value.is(nestedProperty)
        group.getPropertyObject('pageEntry').alpha == 'A'
        group.getString('scalarEntry') == 'value'
    }
}
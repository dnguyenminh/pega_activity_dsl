package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

/**
 * Comprehensive coverage tests for Page class constructors and methods.
 */
class PageCoverageTest extends Specification {

    def "Page default constructor"() {
        when:
        def page = new Page()

        then:
        page instanceof Page
        page instanceof SimpleClipboardPage
        page instanceof ClipboardPage
        page.isClipboardPage() == true
    }

    def "Page Object constructor with null"() {
        when:
        def page = new Page((Object)null)

        then:
        page instanceof Page
        page.isClipboardPage() == true
    }

    def "Page Object constructor with ClipboardPage"() {
        given:
        def sourcePage = new SimpleClipboardPage()
        sourcePage.put("key1", "value1")
        sourcePage.put("key2", "value2")

        when:
        def page = new Page((Object)sourcePage)

        then:
        page instanceof Page
        page.getAt("key1") == "value1"
        page.getAt("key2") == "value2"
        page.isClipboardPage() == true
    }

    def "Page Object constructor with Map"() {
        given:
        def map = [key1: "value1", key2: "value2"]

        when:
        def page = new Page((Object)map)

        then:
        page instanceof Page
        page.getAt("key1") == "value1"
        page.getAt("key2") == "value2"
        page.isClipboardPage() == true
    }

    def "Page Object constructor with non-ClipboardPage object"() {
        given:
        def nonPageObject = "just a string"

        when:
        def page = new Page(nonPageObject)

        then:
        page instanceof Page
        page.isClipboardPage() == true
        // Should not copy anything since it's not a ClipboardPage or Map
    }

    def "Page Map constructor with null"() {
        when:
        def page = new Page((Map)null)

        then:
        page instanceof Page
        page.isClipboardPage() == true
    }

    def "Page Map constructor with empty map"() {
        given:
        def map = [:]

        when:
        def page = new Page(map)

        then:
        page instanceof Page
        page.isClipboardPage() == true
    }

    def "Page Map constructor with data"() {
        given:
        def map = [key1: "value1", key2: "value2"]

        when:
        def page = new Page(map)

        then:
        page instanceof Page
        page.getAt("key1") == "value1"
        page.getAt("key2") == "value2"
        page.isClipboardPage() == true
    }

    def "Page Map constructor normalizes null keys and clipboard values"() {
        given:
        def nestedPage = new Page([nestedKey: 'nestedVal'])
        def wrappedProp = new SimpleClipboardProperty('wrapped', [alpha: 'A'])
        def map = [
            (null): 'nil-value',
            42: 'answer',
            nestedEntry: nestedPage,
            propertyEntry: wrappedProp
        ]

        when:
        def page = new Page(map)

        then:
        page.getAt(null) == 'nil-value'
        page.getAt('42') == 'answer'
        page.getAt('nestedEntry') instanceof SimpleClipboardPage
        page.getAt('nestedEntry').getAt('nestedKey') == 'nestedVal'
        page.getAt('propertyEntry') instanceof SimpleClipboardPage
        page.getAt('propertyEntry').getAt('alpha') == 'A'
    }

    def "Page List constructor with null"() {
        when:
        def page = new Page((List)null)

        then:
        page instanceof Page
        page.isClipboardPage() == true
    }

    def "Page List constructor with empty list"() {
        given:
        def list = []

        when:
        def page = new Page(list)

        then:
        page instanceof Page
        page.isClipboardPage() == true
    }

    def "Page List constructor with simple values"() {
        given:
        def list = ["item1", "item2", "item3"]

        when:
        def page = new Page(list)

        then:
        page instanceof Page
        def items = page.getAt("items")
        items != null
        items.contains("item1")
        items.contains("item2")
        items.contains("item3")
        page.isClipboardPage() == true
    }

    def "Page List constructor with maps"() {
        given:
        def list = [[key1: "value1"], [key2: "value2"]]

        when:
        def page = new Page(list)

        then:
        page instanceof Page
        page.getAt("key1") == "value1"
        page.getAt("key2") == "value2"
        page.isClipboardPage() == true
    }

    def "Page List constructor with ClipboardPage objects"() {
        given:
        def page1 = new SimpleClipboardPage()
        page1.put("page1Key", "page1Value")
        def page2 = new SimpleClipboardPage()
        page2.put("page2Key", "page2Value")
        def list = [page1, page2]

        when:
        def page = new Page(list)

        then:
        page instanceof Page
        page.getAt("page1Key") == "page1Value"
        page.getAt("page2Key") == "page2Value"
        page.isClipboardPage() == true
    }

    def "Page List constructor with mixed content"() {
        given:
        def page1 = new SimpleClipboardPage()
        page1.put("nestedKey", "nestedValue")
        def list = [[mapKey: "mapValue"], page1, "standaloneItem"]

        when:
        def page = new Page(list)

        then:
        page instanceof Page
        page.getAt("mapKey") == "mapValue"
        page.getAt("nestedKey") == "nestedValue"
        def items = page.getAt("items")
        items != null
        items.contains("standaloneItem")
        page.isClipboardPage() == true
    }

    def "Page List constructor generates sequential item keys for ClipboardProperty descriptors"() {
        given:
        def propertyPage = new SimpleClipboardProperty('first', new Page([alpha: 'A']))
        def mapProperty = new SimpleClipboardProperty('second', [beta: 'B'])
        def list = [
            [directKey: 'directValue'],
            propertyPage,
            mapProperty,
            'tail'
        ]

        when:
        def page = new Page(list)

        then:
        page.getAt('directKey') == 'directValue'
        page.getAt('item1') instanceof SimpleClipboardPage
        page.getAt('item1').getAt('alpha') == 'A'
        page.getAt('item2') instanceof SimpleClipboardPage
        page.getAt('item2').getAt('beta') == 'B'
        page.getAt('items') instanceof List
        page.getAt('items').contains('tail')
    }

    def "Page String constructor with null name and null value"() {
        when:
        def page = new Page(null, null)

        then:
        page instanceof Page
        page.isClipboardPage() == true
        page.getName() == null
    }

    def "Page String constructor with name and null value"() {
        when:
        def page = new Page("testPage", null)

        then:
        page instanceof Page
        page.isClipboardPage() == true
        page.getName() == "testPage"
    }

    def "Page String constructor with Map value"() {
        given:
        def map = [key1: "value1", key2: "value2"]

        when:
        def page = new Page("testPage", map)

        then:
        page instanceof Page
        page.getName() == "testPage"
        page.getAt("key1") == "value1"
        page.getAt("key2") == "value2"
        page.isClipboardPage() == true
    }

    def "Page String constructor with List value containing maps"() {
        given:
        def list = [[key1: "value1"], [key2: "value2"]]

        when:
        def page = new Page("testPage", list)

        then:
        page instanceof Page
        page.getName() == "testPage"
        page.getAt("key1") == "value1"
        page.getAt("key2") == "value2"
        page.isClipboardPage() == true
    }

    def "Page String constructor with List value containing ClipboardPage"() {
        given:
        def sourcePage = new SimpleClipboardPage()
        sourcePage.put("sourceKey", "sourceValue")
        def list = [sourcePage]

        when:
        def page = new Page("testPage", list)

        then:
        page instanceof Page
        page.getName() == "testPage"
        page.getAt("sourceKey") == "sourceValue"
        page.isClipboardPage() == true
    }

    def "Page String constructor with List value containing raw values"() {
        given:
        def list = ["item1", "item2", "item3"]

        when:
        def page = new Page("testPage", list)

        then:
        page instanceof Page
        page.getName() == "testPage"
        def items = page.getAt("items")
        items != null
        items.contains("item1")
        items.contains("item2")
        items.contains("item3")
        page.isClipboardPage() == true
    }

    def "Page String constructor with ClipboardPage value"() {
        given:
        def sourcePage = new SimpleClipboardPage()
        sourcePage.put("sourceKey", "sourceValue")

        when:
        def page = new Page("testPage", sourcePage)

        then:
        page instanceof Page
        page.getName() == "testPage"
        page.getAt("sourceKey") == "sourceValue"
        page.isClipboardPage() == true
    }

    def "Page String constructor with ClipboardPageType parameter"() {
        given:
        def map = [key1: "value1"]

        when:
        def page = new Page("testPage", map, ClipboardPropertyType.PAGE)

        then:
        page instanceof Page
        page.getName() == "testPage"
        page.getAt("key1") == "value1"
        page.isClipboardPage() == true
    }

    def "Page ClipboardPage constructor with null"() {
        when:
        def page = new Page((ClipboardPage)null)

        then:
        page instanceof Page
        page.isClipboardPage() == true
    }

    def "Page ClipboardPage constructor with SimpleClipboardPage"() {
        given:
        def sourcePage = new SimpleClipboardPage()
        sourcePage.put("key1", "value1")
        sourcePage.put("key2", "value2")

        when:
        def page = new Page(sourcePage)

        then:
        page instanceof Page
        page.getAt("key1") == "value1"
        page.getAt("key2") == "value2"
        page.isClipboardPage() == true
    }

    def "Page ClipboardPage constructor with Page"() {
        given:
        def sourcePage = new Page()
        sourcePage.put("key1", "value1")
        sourcePage.put("key2", "value2")

        when:
        def page = new Page(sourcePage)

        then:
        page instanceof Page
        page.getAt("key1") == "value1"
        page.getAt("key2") == "value2"
        page.isClipboardPage() == true
    }

    def "Page String ClipboardPage constructor with null"() {
        when:
        def page = new Page("testPage", (ClipboardPage)null)

        then:
        page instanceof Page
        page.getName() == "testPage"
        page.isClipboardPage() == true
    }

    def "Page String ClipboardPage constructor with SimpleClipboardPage"() {
        given:
        def sourcePage = new SimpleClipboardPage()
        sourcePage.put("key1", "value1")
        sourcePage.put("key2", "value2")

        when:
        def page = new Page("testPage", sourcePage)

        then:
        page instanceof Page
        page.getName() == "testPage"
        page.getAt("key1") == "value1"
        page.getAt("key2") == "value2"
        page.isClipboardPage() == true
    }

    def "Page getName method"() {
        given:
        def page = new Page("testPage", [:])

        when:
        def name = page.getName()

        then:
        name == "testPage"
    }
}

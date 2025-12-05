package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class SimpleClipboardPageTest extends Specification {

    def "SimpleClipboardPage no-arg constructor"() {
        when:
        def page = new SimpleClipboardPage()

        then:
        page instanceof SimpleClipboardPage
        page instanceof ClipboardPage
        page instanceof AbstractClipboardPage
    }

    def "SimpleClipboardPage Map constructor"() {
        given:
        def map = [key1: "value1", key2: "value2"]

        when:
        def page = new SimpleClipboardPage(map)

        then:
        page instanceof SimpleClipboardPage
        page.getAt("key1") == "value1"
        page.getAt("key2") == "value2"
    }

    def "SimpleClipboardPage List constructor"() {
        given:
        def list = ["item1", "item2", "item3"]

        when:
        def page = new SimpleClipboardPage(list)

        then:
        page instanceof SimpleClipboardPage
        // List constructor creates an "items" list property with all values
        def items = page.getAt("items")
        items != null
        items.contains("item1")
        items.contains("item2")
        items.contains("item3")
    }

    def "SimpleClipboardPage ClipboardPage constructor"() {
        given:
        def originalPage = new SimpleClipboardPage()
        originalPage.put("testKey", "testValue")

        when:
        def copiedPage = new SimpleClipboardPage(originalPage)

        then:
        copiedPage instanceof SimpleClipboardPage
        copiedPage.getAt("testKey") == "testValue"
    }

    def "SimpleClipboardPage Object constructor with ClipboardPage"() {
        given:
        def originalPage = new SimpleClipboardPage()
        originalPage.put("objKey", "objValue")

        when:
        def objectPage = new SimpleClipboardPage((Object)originalPage)

        then:
        objectPage instanceof SimpleClipboardPage
        objectPage.getAt("objKey") == "objValue"
    }

    def "SimpleClipboardPage Object constructor with non-ClipboardPage"() {
        given:
        def nonPageObject = "string object"

        when:
        def objectPage = new SimpleClipboardPage(nonPageObject)

        then:
        objectPage instanceof SimpleClipboardPage
        // Should not copy anything since it's not a ClipboardPage
    }

    def "getAt Object method with SimpleClipboardPage result"() {
        given:
        def page = new SimpleClipboardPage()
        def nestedPage = new SimpleClipboardPage()
        nestedPage.put("nestedKey", "nestedValue")
        page.put("pageKey", nestedPage)

        when:
        def result = page.getAt("pageKey")

        then:
        result instanceof SimpleClipboardPage
        result.getAt("nestedKey") == "nestedValue"
    }

    def "getAt Object method with Page result"() {
        given:
        def page = new SimpleClipboardPage()
        def pageResult = new SimpleClipboardPage()
        pageResult.put("pageKey", "pageValue")
        page.put("pageResultKey", pageResult)

        when:
        def result = page.getAt("pageResultKey")

        then:
        result instanceof SimpleClipboardPage
        result.getAt("pageKey") == "pageValue"
    }

    def "getAt Object method with ClipboardPage result"() {
        given:
        def page = new SimpleClipboardPage()
        def clipboardPage = new SimpleClipboardPage()
        clipboardPage.put("clipboardKey", "clipboardValue")
        page.put("clipboardPageKey", clipboardPage)

        when:
        def result = page.getAt("clipboardPageKey")

        then:
        result instanceof SimpleClipboardPage
        result.getAt("clipboardKey") == "clipboardValue"
    }

    def "getAt Object method with Map result"() {
        given:
        def page = new SimpleClipboardPage()
        def map = [mapKey: "mapValue"]
        page.put("mapKey", map)

        when:
        def result = page.getAt("mapKey")

        then:
        result instanceof SimpleClipboardPage
        result.getAt("mapKey") == "mapValue"
    }

    def "getAt Object method with ClipboardProperty result"() {
        given:
        def page = new SimpleClipboardPage()
        def property = new SimpleClipboardProperty("propName", "propValue")
        page.put("propertyKey", property)

        when:
        def result = page.getAt("propertyKey")

        then:
        // Should return the property value if it's a valid property
        result == "propValue"
    }

    def "getAt Object method with non-special result"() {
        given:
        def page = new SimpleClipboardPage()
        page.put("stringKey", "simpleString")

        when:
        def result = page.getAt("stringKey")

        then:
        result == "simpleString"
    }

    def "getAt Object method with exception handling"() {
        given:
        def page = new SimpleClipboardPage()
        // Create a property that might throw exception when accessing value
        def problematicProperty = new SimpleClipboardProperty("errorProp", null)
        page.put("errorKey", problematicProperty)

        when:
        def result = page.getAt("errorKey")

        then:
        // Should return null when property has null value
        result == null
    }

    def "getAt String method with various result types"() {
        given:
        def page = new SimpleClipboardPage()
        page.put("stringVal", "text")
        page.put("numberVal", 42)
        page.put("booleanVal", true)

        when:
        def stringResult = page.getAt("stringVal")
        def numberResult = page.getAt("numberVal")
        def booleanResult = page.getAt("booleanVal")

        then:
        stringResult == "text"
        numberResult == 42
        booleanResult == true
    }

    def "getName method returns page name"() {
        given:
        def page = new SimpleClipboardPage()
        // Set the page name through the rename method
        page.rename("TestPageName")

        when:
        def name = page.getName()

        then:
        name == "TestPageName"
    }
}

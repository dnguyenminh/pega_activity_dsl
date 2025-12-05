package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

/**
 * Test coverage for AbstractClipboardPage methods that are not fully covered.
 */
class AbstractClipboardPageCoverageTest extends Specification {

    def "AbstractClipboardPage List constructor with mixed content"() {
        when:
        def page = new Page([
            [name: "John", age: 30],
            "standaloneItem",
            new Page([city: "NYC"])
        ])

        then:
        page.getAt("name") == "John"
        page.getAt("age") == 30
        page.getAt("city") == "NYC"
        page.getAt("items").contains("standaloneItem")
    }

    def "AbstractClipboardPage List constructor with ClipboardProperty"() {
        given:
        def prop = new SimpleClipboardProperty("testValue")

        when:
        def page = new Page([prop])

        then:
        page.getAt("item0") == "testValue"
    }

    def "_deepUnwrapAndConvert handles ClipboardProperty unwrapping"() {
        given:
        def page = new Page()
        def prop = new SimpleClipboardProperty("wrappedValue")
        page.put("wrappedProp", prop)

        when:
        def result = page.getAt("wrappedProp")

        then:
        result == "wrappedValue"
    }

    def "_deepUnwrapAndConvert handles Map to Page conversion"() {
        given:
        def page = new Page()

        when:
        page.put("mapProp", [nested: "value"])

        then:
        def result = page.getAt("mapProp")
        result instanceof SimpleClipboardPage
        result.getAt("nested") == "value"
    }

    def "_deepUnwrapAndConvert handles List conversion"() {
        given:
        def page = new Page()

        when:
        page.put("listProp", [[item: "1"], [item: "2"]])

        then:
        def result = page.getAt("listProp")
        result instanceof List
        result.size() == 2
        result[0].getAt("item") == "1"
        result[1].getAt("item") == "2"
    }

    def "getAt handles nested Page conversion"() {
        given:
        def page = new Page()

        when:
        page.put("nestedMap", [level2: [level3: "deepValue"]])

        then:
        def result = page.getAt("nestedMap")
        result.getAt("level2").getAt("level3") == "deepValue"
    }

    def "copy method creates independent copy"() {
        given:
        def original = new Page([name: "Original", data: [value: 123]])
        original.addMessage("Test message")

        when:
        def copy = original.copy()

        then:
        copy.getAt("name") == "Original"
        copy.getAt("data").getAt("value") == 123
        !copy.hasMessages() // Messages should not be copied
        copy.getAt("name") != null
    }

    def "copyTo method copies to destination page"() {
        given:
        def source = new Page([sourceData: "value1", nested: [item: "nestedValue"]])
        def dest = new Page()

        when:
        source.copyTo(dest)

        then:
        dest.getAt("sourceData") == "value1"
        dest.getAt("nested").getAt("item") == "nestedValue"
    }

    def "copyFrom method replaces page content"() {
        given:
        def page = new Page([existing: "oldValue"])
        def source = new Page([newData: "newValue"])

        when:
        page.copyFrom(source)

        then:
        page.getAt("newData") == "newValue"
        page.getAt("existing") == null
    }

    def "replace method replaces page content"() {
        given:
        def page = new Page([existing: "oldValue"])
        def source = new Page([replaced: "newValue", additional: "data"])

        when:
        page.replace(source)

        then:
        page.getAt("replaced") == "newValue"
        page.getAt("additional") == "data"
        page.getAt("existing") == null
    }

    def "message methods coverage"() {
        given:
        def page = new Page()

        when:
        page.addMessage("Message 1")
        page.addMessage("Message 2", "propertyName")
        page.addMessage("Message 3", "propertyName", 1)

        then:
        page.hasMessages()
        page.getMessagesAll().size() == 3
        page.getMessages().size() == 3

        when:
        page.clearMessages()

        then:
        !page.hasMessages()
        page.getMessages().isEmpty()
    }

    def "getBigDecimal method coverage"() {
        given:
        def page = new Page()

        when:
        page.put("decimalProp", new BigDecimal("123.45"))
        page.put("stringProp", "67.89")
        page.put("nullProp", null)

        then:
        page.getBigDecimal("decimalProp") == new BigDecimal("123.45")
        page.getBigDecimal("stringProp") == new BigDecimal("67.89")
        page.getBigDecimal("nullProp") == null
        page.getBigDecimal("nonExistent") == null
    }

    def "getBoolean method coverage"() {
        given:
        def page = new Page()

        when:
        page.put("trueProp", true)
        page.put("falseProp", false)
        page.put("stringProp", "true")
        page.put("nullProp", null)

        then:
        page.getBoolean("trueProp") == true
        page.getBoolean("falseProp") == false
        page.getBoolean("stringProp") == true // Non-null string is truthy
        page.getBoolean("nullProp") == false // null is falsy
    }

    def "XML and JSON methods coverage"() {
        given:
        def page = new Page([test: "value"])

        expect:
        page.getJSON(true) != null
        page.getJSON(false) != null
        page.getXML(true) != null
        page.getXML(false) != null
        page.getXML("pageName", 0) != null

        // These should be no-ops
        page.adoptJSONObject([:])
        page.adoptXMLForm("<test/>", 0)
    }

    def "getClassName and other interface methods"() {
        given:
        def page = new Page()

        expect:
        page.getClassName() == 'Data-Generic'
        page.getName() == null
        !page.isEmbedded()
        page.isValid()
        !page.isReadOnly()
        !page.isJavaPage()
        page.getType() == ClipboardProperty.TYPE_PAGE
    }

    def "propertyMissing method coverage"() {
        given:
        def page = new Page([dotProperty: "dotValue"])

        expect:
        page.dotProperty == "dotValue"
        page.nonExistent == null
    }

    def "setValue method coverage"() {
        given:
        def page = new Page()

        when:
        page.setValue("testProp", "testValue")

        then:
        page.getAt("testProp") == "testValue"
    }

    def "removeFromClipboard method coverage"() {
        given:
        def page = new Page([data: "value"])

        when:
        page.removeFromClipboard()

        then:
        page.isEmpty()
    }

    def "copy method with parameter coverage"() {
        given:
        def source = new Page([data: "sourceValue"])
        def dest = new Page([existing: "old"])

        when:
        def result = source.copy(dest)

        then:
        result == dest
        dest.getAt("data") == "sourceValue"
        dest.getAt("existing") == null
    }

    def "_toSimpleClipboardPageSafe method coverage"() {
        given:
        def page = new Page()
        def mapData = [test: "value"]
        def pageData = new Page([inner: "data"])

        when:
        def result1 = page._toSimpleClipboardPageSafe(mapData)
        def result2 = page._toSimpleClipboardPageSafe(pageData)
        def result3 = page._toSimpleClipboardPageSafe(null)

        then:
        result1 instanceof SimpleClipboardPage
        result2 instanceof SimpleClipboardPage
        result3 == null
    }

}

package com.pega.pegarules.pub.clipboard

import spock.lang.Specification
import java.lang.reflect.Method

class AbstractClipboardPageZeroCoverageTest extends Specification {

    def "isPageInstance checks via reflection"() {
        given:
        def method = AbstractClipboardPage.getDeclaredMethod("isPageInstance", Object.class)
        method.setAccessible(true)

        expect:
        method.invoke(null, new Page()) == true
        method.invoke(null, new Object[]{null}) == false
        method.invoke(null, "string") == false
    }

    def "isListOfPageInstances checks via reflection"() {
        given:
        def method = AbstractClipboardPage.getDeclaredMethod("isListOfPageInstances", List.class)
        method.setAccessible(true)

        expect:
        // null elements are ignored in isListOfPageInstances
        method.invoke(null, [new Page()]) == true
        method.invoke(null, [new Page(), null]) == true
        method.invoke(null, [new Page(), "string"]) == false
    }

    def "_storeValue handles double wrapped ClipboardProperty"() {
        given:
        def page = new Page()
        def innerProp = new SimpleClipboardProperty("inner")
        def outerProp = new SimpleClipboardProperty(innerProp)

        when:
        page.put("doubleWrapped", outerProp)

        then:
        page.getString("doubleWrapped") == "inner"
    }

    def "_storeValue handles Map conversion to SimpleClipboardPage"() {
        given:
        def page = new Page()
        def map = [key: "value"]

        when:
        page.put("mapProp", map)

        then:
        def val = page.getPropertyObject("mapProp")
        // AbstractClipboardPage.getPropertyObject returns SimpleClipboardProperty(Page) if it resolves to a Page.
        val instanceof ClipboardProperty
        val.getPageValue().getString("key") == "value"
    }

    def "_storeValue handles ClipboardPage conversion"() {
        given:
        def page = new Page()
        def otherPage = new Page([key: "value"])

        when:
        page.put("pageProp", otherPage)

        then:
        def val = page.getProperty("pageProp")
        val.getPageValue().getString("key") == "value"
    }

    def "getAt handles List of mixed types"() {
        given:
        def page = new Page()
        def list = [
            new Page([p: "1"]),
            [m: "2"],
            "raw"
        ]
        
        // Inject a raw list via reflection to test getAt's defensive conversions
        def delegateField = AbstractClipboardPage.getDeclaredField("delegate")
        delegateField.setAccessible(true)
        def delegate = (Map) delegateField.get(page)
        delegate.put("rawList", list)

        when:
        def result = page.getAt("rawList")

        then:
        result instanceof List
        result[0] instanceof SimpleClipboardPage
        result[1] instanceof SimpleClipboardPage
        result[2] == "raw"
    }
    
    def "_getPropertyValueSafe handles exception"() {
        given:
        def method = AbstractClipboardPage.getDeclaredMethod("_getPropertyValueSafe", Object.class)
        method.setAccessible(true)
        def badObj = new Object() {
            def getPropertyValue() { throw new RuntimeException("oops") }
        }

        when:
        def res = method.invoke(null, badObj)

        then:
        res == badObj // Fallback to object itself
    }

    def "_deepUnwrapAndConvert handles nested recursion"() {
        given:
        def method = AbstractClipboardPage.getDeclaredMethod("_deepUnwrapAndConvert", Object.class)
        method.setAccessible(true)
        def nestedList = [[key: "val"]]

        when:
        def res = method.invoke(null, nestedList)

        then:
        res instanceof List
        res[0] instanceof Page
        ((Page)res[0]).getString("key") == "val"
    }
    
    def "constructor with null map"() {
        when:
        def page = new Page((Map)null)
        
        then:
        page != null
        page.isEmpty() == false // base props
    }
    
    def "constructor with null list"() {
        when:
        def page = new Page((List)null)
        
        then:
        page != null
        page.isEmpty() == false // base props
    }
    
    def "constructor with list containing ClipboardPage"() {
        given:
        def subPage = new Page([sub: "val"])
        
        when:
        def page = new Page([subPage])
        
        then:
        page.getString("sub") == "val"
    }
    
    def "get with null key"() {
        given:
        def page = new Page()
        
        expect:
        page.get(null) == null
    }
    
    def "put with null key"() {
        given:
        def page = new Page()
        
        when:
        page.put(null, "val")
        
        then:
        page.get(null) == "val"
    }

    def "_toSimpleClipboardPageSafe handles entrySet exception"() {
        given:
        def page = new Page()
        def badObj = new Object() {
            def entrySet() { throw new RuntimeException("oops") }
        }
        
        expect:
        page._toSimpleClipboardPageSafe(badObj) == null
    }

    def "_toSimpleClipboardPageSafe handles entry exception"() {
        given:
        def page = new Page()
        def badEntry = new Object() {
            def getKey() { throw new RuntimeException("key") }
            def getValue() { "val" }
        }
        def badObj = new Object() {
            def entrySet() { [badEntry] }
        }
        
        when:
        def res = page._toSimpleClipboardPageSafe(badObj)
        
        then:
        res != null
        // Note: res might be empty if base props are not added or cleared, but we just check it returns a page
        res instanceof SimpleClipboardPage
    }

    def "SimpleClipboardPage constructor with empty map"() {
        when:
        def page = new SimpleClipboardPage([:])
        
        then:
        page != null
        // Should contain base props
        !page.isEmpty()
    }

    def "Map interface methods"() {
        given:
        def page = new Page()
        page.put("k", "v")
        
        expect:
        page.size() >= 1
        !page.isEmpty()
        page.containsKey("k")
        page.containsValue("v")
        page.keySet().contains("k")
        page.values().contains("v")
        
        when:
        page.remove("k")
        
        then:
        !page.containsKey("k")
        
        when:
        page.clear()
        
        then:
        page.isEmpty()
        page.size() == 0
    }

    def "Message methods"() {
        given:
        def page = new Page()
        
        when:
        page.addMessage("msg1")
        page.addMessage("msg2", "prop")
        page.addMessage("msg3", "prop", 1)
        
        then:
        page.hasMessages()
        page.getMessages().size() == 3
        page.getMessagesAll().hasNext()
        
        when:
        page.clearMessages()
        
        then:
        !page.hasMessages()
    }

    def "Type conversion getters"() {
        given:
        def page = new Page()
        page.put("dec", "10.5")
        page.put("bool", "true")
        
        expect:
        page.getBigDecimal("dec") == new BigDecimal("10.5")
        page.getBoolean("bool") == true
        page.getDate("date") == null // implementation returns null
    }

    def "XML/JSON methods"() {
        given:
        def page = new Page()
        
        expect:
        page.getJSON(true) != null
        page.getXML(true) != null
        page.getXML(1) != null
        page.getXML("name", 1) != null
        
        when:
        page.adoptJSONObject(null)
        page.adoptXMLForm(null, 0)
        
        then:
        noExceptionThrown()
    }

    def "Metadata methods"() {
        given:
        def page = new Page("MyPage", [:])
        
        expect:
        page.getName() == "MyPage"
        page.getClassName() == "Data-Generic"
        !page.isEmbedded()
        page.isValid()
        !page.isReadOnly()
        !page.isJavaPage()
        page.getType() == ClipboardProperty.TYPE_PAGE
        page.getEntryHandle("prop") == null
    }

    def "Copy/Rename/Remove methods"() {
        given:
        def page = new Page()
        page.put("k", "v")
        
        when:
        def copy = page.copy()
        
        then:
        copy.getString("k") == "v"
        
        when:
        page.rename("NewName")
        
        then:
        page.getName() == "NewName"
        
        when:
        page.removeFromClipboard()
        
        then:
        page.isEmpty()
    }

    def "Property access methods"() {
        given:
        def page = new Page()
        
        when:
        page.putString("s", "val")
        page.setValue("o", 123)
        page.putAt("a", "b")
        
        then:
        page.getString("s") == "val"
        page.get("o") == "123"
        page.getAt("a") == "b"
        
        when:
        page.clearValue("s")
        
        then:
        page.getString("s") == null
        
        when:
        page.put("p", "v")
        def prop = page.removeProperty("p")
        
        then:
        prop.getStringValue() == "v"
        
        when:
        page.putProperty(null) // no-op
        
        then:
        noExceptionThrown()
        
        when:
        page.propertyMissing("miss")
        
        then:
        noExceptionThrown()
    }
    
    def "replace method"() {
        given:
        def page = new Page()
        def other = new Page([newKey: "newValue"])
        
        when:
        page.replace(other)
        
        then:
        page.getString("newKey") == "newValue"
    }
    
    def "copyTo method"() {
        given:
        def page = new Page([k: "v"])
        def other = new Page()
        
        when:
        page.copyTo(other)
        
        then:
        other.getString("k") == "v"
    }

    def "_deepUnwrapAndConvert handles multiple layers of ClipboardProperty"() {
        given:
        def method = AbstractClipboardPage.getDeclaredMethod("_deepUnwrapAndConvert", Object.class)
        method.setAccessible(true)
        def inner = new SimpleClipboardProperty("val")
        def outer = new SimpleClipboardProperty(inner)
        def outer2 = new SimpleClipboardProperty(outer)
        
        expect:
        method.invoke(null, outer2) == "val"
    }

    def "_unwrapPropertyValue returns Page as is"() {
        given:
        def page = new Page()
        def p = new Page()
        
        expect:
        page._unwrapPropertyValue(p).is(p)
    }
    
    def "_unwrapPropertyValue converts ClipboardPage to SimpleClipboardPage"() {
        given:
        def page = new Page()
        def cp = Mock(ClipboardPage)
        
        when:
        def res = page._unwrapPropertyValue(cp)
        
        then:
        res instanceof SimpleClipboardPage
    }

    def "putAt stores SimpleClipboardPage as-is"() {
        given:
        def page = new Page()
        def embedded = new SimpleClipboardPage([foo: "bar"])

        when:
        page.putAt("embedded", embedded)

        then:
        def stored = page.getAt("embedded")
        stored instanceof SimpleClipboardPage
        stored.getString("foo") == "bar"
    }

    def "get falls back to toString for raw delegate values"() {
        given:
        def page = new Page()
        def delegateField = AbstractClipboardPage.getDeclaredField("delegate")
        delegateField.setAccessible(true)
        ((Map) delegateField.get(page))["raw"] = 123

        expect:
        page.get("raw") == "123"
    }

    def "_toSimpleClipboardPageSafe handles entries without getters"() {
        given:
        def page = new Page()
        def entry = new groovy.lang.GroovyObjectSupport() {
            @Override
            Object getProperty(String name) {
                if (name == "getKey" || name == "getValue") {
                    return null
                }
                if (name == "key") {
                    return "strangeKey"
                }
                if (name == "value") {
                    return "strangeValue"
                }
                throw new MissingPropertyException(name, getClass())
            }
        }
        def pseudoPage = new Object() {
            def entrySet() { [entry] }
        }

        when:
        def result = page._toSimpleClipboardPageSafe(pseudoPage)

        then:
        result instanceof SimpleClipboardPage
        result.getString("strangeKey") == "strangeValue"
    }
}

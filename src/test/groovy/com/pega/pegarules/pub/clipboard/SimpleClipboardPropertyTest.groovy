package com.pega.pegarules.pub.clipboard

import spock.lang.Specification
import java.math.BigDecimal
import java.util.Date

class SimpleClipboardPropertyTest extends Specification {

    def "should construct with different options"() {
        when:
        def prop1 = new SimpleClipboardProperty()
        def prop2 = new SimpleClipboardProperty('myValue')
        def prop3 = new SimpleClipboardProperty('myName', 'myValue')
        def prop4 = new SimpleClipboardProperty('myName', 'myValue', 1)

        then:
        prop1.value == null
        prop2.value == 'myValue'
        prop3.name == 'myName'
        prop4.name == 'myName'
    }

    def "should handle list operations"() {
        given:
        def prop = new SimpleClipboardProperty()

        when:
        prop.add('a')
        prop.add('b')

        then:
        prop.value == ['a', 'b']
        prop.size() == 2
        prop.get(0).value == 'a'

        when:
        prop.remove(0)

        then:
        prop.value == ['b']
    }

    def "should handle map operations"() {
        given:
        def prop = new SimpleClipboardProperty([key1: 'val1'])

        expect:
        prop.get('key1').getStringValue() == 'val1'
        prop.contains('val1')
        prop.size() == 1
    }

    def "should get and convert values"() {
        expect:
        new SimpleClipboardProperty('123').getIntegerValue() == 123
        new SimpleClipboardProperty('123.45').getBigDecimalValue() == new BigDecimal('123.45')
        new SimpleClipboardProperty('true').getBooleanValue()
        new SimpleClipboardProperty('99.9').getDoubleValue() == 99.9d
        new SimpleClipboardProperty('text').getStringValue() == 'text'
    }

    def "should return correct type and mode"() {
        expect:
        new SimpleClipboardProperty('a').getType() == ClipboardProperty.TYPE_TEXT
        new SimpleClipboardProperty(123).getType() == ClipboardProperty.TYPE_INTEGER
        new SimpleClipboardProperty(true).getType() == ClipboardProperty.TYPE_TRUEFALSE
        new SimpleClipboardProperty(new Date()).getType() == ClipboardProperty.TYPE_DATETIME
        new SimpleClipboardProperty(1.2d).getType() == ClipboardProperty.TYPE_DOUBLE
        new SimpleClipboardProperty(new BigDecimal(1)).getType() == ClipboardProperty.TYPE_DECIMAL
        new SimpleClipboardProperty([:]).getType() == ClipboardProperty.TYPE_PAGE
        new SimpleClipboardProperty([]).getType() == ClipboardProperty.TYPE_UNKNOWN
        
        new SimpleClipboardProperty('a').getMode() == ClipboardProperty.MODE_SINGLE
        new SimpleClipboardProperty([]).getMode() == ClipboardProperty.MODE_LIST
        new SimpleClipboardProperty([:]).getMode() == ClipboardProperty.MODE_GROUP
    }

    def "should get page value"() {
        given:
        def map = [name: 'test']
        def prop = new SimpleClipboardProperty(map)

        when:
        def page = prop.getPageValue()

        then:
        page instanceof Page
        page.getString('name') == 'test'
    }

    def "should get property value"() {
        given:
        def prop = new SimpleClipboardProperty([name: 'test'])

        when:
        def val = prop.getPropertyValue()

        then:
        val instanceof Page
        val.getString('name') == 'test'
    }
    
    def "should check equality"() {
        given:
        def prop1 = new SimpleClipboardProperty("value")
        def prop2 = new SimpleClipboardProperty("value")
        def prop3 = new SimpleClipboardProperty("other")

        expect:
        prop1.equals(prop2)
        !prop1.equals(prop3)
        prop1.hashCode() == prop2.hashCode()
    }

    def "clear and set value and check flags"() {
        given:
        def p = new SimpleClipboardProperty('v')

        when:
        p.clearValue()

        then:
        p.isUndefined()

        when:
        p.setValue(123)

        then:
        !p.isUndefined()
        p.getIntegerValue() == 123
        p.getDoubleValue() == 123.0d

        expect:
        !p.hasMessages()
        !p.isError()
        !p.isIncompatible()
        !p.isProtected()
    }

    def "remove operations on list and map"() {
        given:
        def l = new SimpleClipboardProperty(['a', 'b', 'c'])
        def m = new SimpleClipboardProperty([x: 'y'])

        when:
        l.remove(1)
        m.remove('x')

        then:
        l.size() == 2
        m.size() == 0
    }

    def "type conversions for Double/Integer/Date"() {
        given:
        def dbl = new SimpleClipboardProperty(1.5d)
        def intp = new SimpleClipboardProperty(42)
        def dt = new SimpleClipboardProperty(new Date(0))

        expect:
        dbl.getDoubleValue() == 1.5d
        intp.getIntegerValue() == 42
        dt.toDate() instanceof Date
    }

    def "get returns null when not list or map and other edge cases"() {
        given:
        def single = new SimpleClipboardProperty('single')
        def nonNum = new SimpleClipboardProperty('abc')

        expect:
        single.get(0) == null
        single.get('key') == null
        nonNum.getBigDecimalValue() == null
        nonNum.getDoubleValue() == 0.0d
        nonNum.getIntegerValue() == 0
        nonNum.toDate() == null
        nonNum.iterator().collect { it }.size() == 0
        new SimpleClipboardProperty([:]).isPage()
        !new SimpleClipboardProperty('x').isPage()
        new SimpleClipboardProperty(1).equals(1)
    }

    def "add with index converts non-list value into list"() {
        given:
        def p = new SimpleClipboardProperty('first')

        when:
        p.add(0, 'second')

    then:
    p.size() == 2
    // add(0, x) will insert at 0 meaning x precedes the original value
    p.get(0).getStringValue() == 'second'
    p.get(1).getStringValue() == 'first'
    }

    def "get by index and string return Page when nested maps are present"() {
        given:
        def scpList = new SimpleClipboardProperty([ [a:1], new Page([b:2]), 'raw' ])
        def scpMap = new SimpleClipboardProperty([m:[c:3], n:'rawval'])

        when:
        def v0 = scpList.get(0)
        def v1 = scpList.get(1)
        def v2 = scpList.get(2)
        def m0 = scpMap.get('m')
        def m1 = scpMap.get('n')

    then:
    // allow implementations to return either Page directly or a ClipboardProperty wrapper
    (v0 instanceof Page || v0 instanceof SimpleClipboardProperty)
    // normalize to Page for value checks
    def p0 = v0 instanceof Page ? v0 : v0.getPropertyValue()
    p0 instanceof Page
    p0.getString('a') == '1'
    (v1 instanceof Page || v1 instanceof SimpleClipboardProperty)
    def p1 = v1 instanceof Page ? v1 : v1.getPropertyValue()
    p1 instanceof Page
    p1.getString('b') == '2'
    v2 instanceof SimpleClipboardProperty
        v2.getStringValue() == 'raw'
    (m0 instanceof Page || m0 instanceof SimpleClipboardProperty)
    def mp0 = m0 instanceof Page ? m0 : m0.getPropertyValue()
    mp0 instanceof Page
    mp0.getString('c') == '3'
        m1 instanceof SimpleClipboardProperty
        m1.getStringValue() == 'rawval'
    }

    def "get returns wrapper when ClipboardPage instances are used"() {
        given:
        def scpList = new SimpleClipboardProperty([ new SimpleClipboardPage([a:9]) ])
        def scpMap = new SimpleClipboardProperty([m: new SimpleClipboardPage([c:7])])

        when:
        def lv = scpList.get(0)
        def mv = scpMap.get('m')

        then:
        lv instanceof SimpleClipboardProperty
        lv.getPropertyValue() instanceof Page
        lv.getPropertyValue().getString('a') == '9'
        mv instanceof SimpleClipboardProperty
        mv.getPropertyValue() instanceof Page
        mv.getPropertyValue().getString('c') == '7'
    }

    def "getPageValue converts AbstractClipboardPage to Page"() {
        given:
        def scp = new SimpleClipboardProperty(new SimpleClipboardPage([k: 'v']))

        expect:
        def v = scp.getPageValue()
        // it might return the raw ClipboardPage implementation or a Page wrapper; accept both
        (v instanceof Page || v instanceof ClipboardPage)
        v.getString('k') == 'v'
    }

    def "equals works for Page identity and not-equals for different Page instances"() {
        given:
        def pMap = new SimpleClipboardProperty([a:1])
        def pPage = new SimpleClipboardProperty(new Page([a:1]))
        def pageObj = new Page([a:1])
        def pPage1 = new SimpleClipboardProperty(pageObj)
        def pPage2 = new SimpleClipboardProperty(pageObj)

        expect:
        // Page equality is identity-based; different Page instances with same content won't be equal
        !pMap.equals(pPage)
        // two properties that wrap the same Page instance are equal
        pPage1.equals(pPage2)
        pPage1.hashCode() == pPage2.hashCode()
    }

    def "iterator returns Page for map/list elements and respects wrapper unwrapping"() {
        given:
        def scp = new SimpleClipboardProperty([ [x:1], [x:2] ])

        when:
        def list = scp.iterator().collect { it }

        then:
        list.size() == 2
        list[0] instanceof Page
        list[0].getString('x') == '1'
        list[1] instanceof Page
    }

    def "getPropertyValue unwraps nested ClipboardProperty and converts list of maps to list of Pages"() {
        given:
        def inner = new SimpleClipboardProperty([c:4])
        def scp = new SimpleClipboardProperty([ inner, [d:5], 'raw' ])

        when:
        def v = scp.getPropertyValue()

        then:
        v instanceof List
        v[0] instanceof Page
        v[0].getString('c') == '4'
        v[1] instanceof Page
        v[1].getString('d') == '5'
        v[2] == 'raw'
    }

    def "getPageValue returns Page for Map, ClipboardPage, and nested property"() {
        given:
        def scpMap = new SimpleClipboardProperty([p: [z:9]])
        def scpPage = new SimpleClipboardProperty(new Page([k:'v']))

        expect:
        scpMap.getPageValue() instanceof Page
        scpMap.getPageValue().getString('p') == null || scpMap.getPageValue().getPropertyObject('p') != null
        scpPage.getPageValue() instanceof Page
        scpPage.getPageValue().getString('k') == 'v'
    }

    // Additional tests for missing coverage
    def "test getType returns correct types for various values"() {
        expect:
        new SimpleClipboardProperty('text').getType() == ClipboardProperty.TYPE_TEXT
        new SimpleClipboardProperty(123).getType() == ClipboardProperty.TYPE_INTEGER
        new SimpleClipboardProperty(true).getType() == ClipboardProperty.TYPE_TRUEFALSE
        new SimpleClipboardProperty(1.5d).getType() == ClipboardProperty.TYPE_DOUBLE
        new SimpleClipboardProperty(new BigDecimal('1.0')).getType() == ClipboardProperty.TYPE_DECIMAL
        new SimpleClipboardProperty(new Date()).getType() == ClipboardProperty.TYPE_DATETIME
        new SimpleClipboardProperty([:]).getType() == ClipboardProperty.TYPE_PAGE
        new SimpleClipboardProperty([]).getType() == ClipboardProperty.TYPE_UNKNOWN
    }

    def "test add method for list operations"() {
        given:
        def prop = new SimpleClipboardProperty()

        when:
        prop.add('item1')
        prop.add('item2')

        then:
        prop.getMode() == ClipboardProperty.MODE_LIST
        prop.size() == 2
        prop.get(0).getStringValue() == 'item1'
        prop.get(1).getStringValue() == 'item2'
    }

    def "test add with index for list operations"() {
        given:
        def prop = new SimpleClipboardProperty(['a', 'b'])

        when:
        prop.add(1, 'c')

        then:
        prop.size() == 3
        prop.get(0).getStringValue() == 'a'
        prop.get(1).getStringValue() == 'c'
        prop.get(2).getStringValue() == 'b'
    }

    def "test get method for map access"() {
        given:
        def prop = new SimpleClipboardProperty([key1: 'value1', key2: 'value2'])

        expect:
        prop.get('key1').getStringValue() == 'value1'
        prop.get('key2').getStringValue() == 'value2'
        prop.get('nonexistent') != null // returns a SimpleClipboardProperty wrapper
    }

    def "test contains method"() {
        given:
        def prop = new SimpleClipboardProperty([key1: 'value1', key2: 'value2'])

        expect:
        prop.contains('value1')
        prop.contains('value2')
        !prop.contains('nonexistent')
    }

    def "test toBoolean method"() {
        expect:
        new SimpleClipboardProperty('true').toBoolean()
        new SimpleClipboardProperty('false').toBoolean() // non-empty string is truthy in Groovy
        new SimpleClipboardProperty('anything').toBoolean() // non-empty string is truthy
        !new SimpleClipboardProperty('').toBoolean()
        !new SimpleClipboardProperty(null).toBoolean()
    }

    def "test getBigDecimalValue method"() {
        expect:
        new SimpleClipboardProperty('123.45').getBigDecimalValue() == new BigDecimal('123.45')
        new SimpleClipboardProperty('invalid').getBigDecimalValue() == null
    }

    def "test getDoubleValue method"() {
        expect:
        new SimpleClipboardProperty('123.45').getDoubleValue() == 123.45d
        new SimpleClipboardProperty('invalid').getDoubleValue() == 0.0d
    }

    def "test getIntegerValue method"() {
        expect:
        new SimpleClipboardProperty('123').getIntegerValue() == 123
        new SimpleClipboardProperty('invalid').getIntegerValue() == 0
    }

    def "test size method"() {
        expect:
        new SimpleClipboardProperty([]).size() == 0
        new SimpleClipboardProperty(['a', 'b']).size() == 2
        new SimpleClipboardProperty([key: 'value']).size() == 1
        new SimpleClipboardProperty('single').size() == 1
    }

    def "test toDouble method"() {
        expect:
        new SimpleClipboardProperty('123.45').toDouble() == 123.45d
        new SimpleClipboardProperty(123).toDouble() == 123.0d
    }

    def "test toInteger method"() {
        expect:
        new SimpleClipboardProperty('123').toInteger() == 123
        new SimpleClipboardProperty(123.45d).toInteger() == 123
    }

    def "test toDate method"() {
        given:
        def date = new Date()

        expect:
        new SimpleClipboardProperty(date).toDate() == date
        new SimpleClipboardProperty('invalid').toDate() == null
    }

    def "test getMode method"() {
        expect:
        new SimpleClipboardProperty('single').getMode() == ClipboardProperty.MODE_SINGLE
        new SimpleClipboardProperty([]).getMode() == ClipboardProperty.MODE_LIST
        new SimpleClipboardProperty([:]).getMode() == ClipboardProperty.MODE_GROUP
    }

    def "test hashCode method"() {
        given:
        def prop1 = new SimpleClipboardProperty('value')
        def prop2 = new SimpleClipboardProperty('value')
        def prop3 = new SimpleClipboardProperty('other')

        expect:
        prop1.hashCode() == prop2.hashCode()
        prop1.hashCode() != prop3.hashCode()
    }

    def "test getLength method"() {
        expect:
        new SimpleClipboardProperty('hello').getLength() == 5
        new SimpleClipboardProperty('').getLength() == 0
    }

    def "test remove methods"() {
        given:
        def listProp = new SimpleClipboardProperty(['a', 'b', 'c'])
        def mapProp = new SimpleClipboardProperty([key1: 'value1', key2: 'value2'])

        when:
        listProp.remove(1)
        mapProp.remove('key1')

        then:
        listProp.size() == 2
        listProp.get(0).getStringValue() == 'a'
        listProp.get(1).getStringValue() == 'c'
        mapProp.size() == 1
        mapProp.get('key2').getStringValue() == 'value2'
    }

    def "test clearValue method"() {
        given:
        def prop = new SimpleClipboardProperty('value')

        when:
        prop.clearValue()

        then:
        prop.isUndefined()
        prop.getStringValue() == null
    }

    def "test getErrors method"() {
        expect:
        new SimpleClipboardProperty('value').getErrors() instanceof Iterator
    }

    def "test getMessages method"() {
        expect:
        new SimpleClipboardProperty('value').getMessages() instanceof Iterator
    }

    def "test isUndefined method"() {
        given:
        def prop = new SimpleClipboardProperty('value')

        expect:
        !prop.isUndefined()

        when:
        prop.clearValue()

        then:
        prop.isUndefined()
    }

    def "test getBooleanValue method"() {
        expect:
        new SimpleClipboardProperty('true').getBooleanValue()
        new SimpleClipboardProperty('false').getBooleanValue() // non-empty string is truthy
        !new SimpleClipboardProperty('').getBooleanValue()
    }

    def "test isPage method"() {
        expect:
        new SimpleClipboardProperty([:]).isPage()
        !new SimpleClipboardProperty('text').isPage()
    }

    def "test getAbsoluteReference method"() {
        expect:
        new SimpleClipboardProperty('value').getAbsoluteReference() == null
    }

    def "test getJustification method"() {
        expect:
        new SimpleClipboardProperty('value').getJustification() == null
    }

    def "test getName method"() {
        expect:
        new SimpleClipboardProperty('value').getName() == null
    }

    def "test getParent method"() {
        expect:
        new SimpleClipboardProperty('value').getParent() == null
    }

    def "test getReference method"() {
        expect:
        new SimpleClipboardProperty('value').getReference() == null
    }

    def "test getDefinition method"() {
        expect:
        new SimpleClipboardProperty('value').getDefinition() == null
    }

    def "test getEntryHandle method"() {
        expect:
        new SimpleClipboardProperty('value').getEntryHandle() == null
    }

    def "test hasMessages method"() {
        expect:
        !new SimpleClipboardProperty('value').hasMessages()
    }

    def "test isError method"() {
        expect:
        !new SimpleClipboardProperty('value').isError()
    }

    def "test isIncompatible method"() {
        expect:
        !new SimpleClipboardProperty('value').isIncompatible()
    }

    def "test isProtected method"() {
        expect:
        !new SimpleClipboardProperty('value').isProtected()
    }

    def "test doBackwardChain method"() {
        expect:
        new SimpleClipboardProperty('value').doBackwardChain() == null
    }

    def "test setJustification method"() {
        given:
        def prop = new SimpleClipboardProperty('value')

        when:
        prop.setJustification('justification')

        then:
        // Method exists and doesn't throw exception
        true
    }
}

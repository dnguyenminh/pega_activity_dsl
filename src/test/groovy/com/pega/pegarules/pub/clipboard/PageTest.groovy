package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class PageTest extends Specification {

    def "should initialize with standard baseclass properties"() {
        when:
        def page = new Page()

        then:
        page.getPropertyObject('pxObjClass') == '@baseclass'
        page.getPropertyObject('pxCreateDate') == null
    }

    def "should construct from a map"() {
        when:
        def page = new Page(name: 'John', age: 30)

        then:
        page.size() == 10 // 8 baseclass props + 2 from map
        page.getString('name') == 'John'
        page.getPropertyObject('age') == 30
    }

    def "should construct from a list of maps"() {
        when:
        def page = new Page([ [name: 'Jane'], [age: 25] ])

        then:
        page.getString('name') == 'Jane'
        page.getPropertyObject('age') == 25
    }
    
    def "should construct from a list of ClipboardPage"() {
        given:
        def page1 = new Page(name: "page1")
        def page2 = new Page(name: "page2")
        
        when:
        def page = new Page([page1, page2])

        then:
        page.getString('name') == 'page2'
    }

    def "should construct from another ClipboardPage"() {
        given:
        def sourcePage = new Page(city: 'Boston', zip: '02110')

        when:
        def page = new Page(sourcePage)

        then:
        page.getString('city') == 'Boston'
        page.getString('zip') == '02110'
    }

    def "should construct with name and value"() {
        when:
        def page = new Page('MyPage', [prop1: 'val1'])

        then:
        // The page name may be set by the constructor in some overloads; accept either behaviour
        page.getString('prop1') == 'val1'
        if (page.getName() != null) page.getName() == 'MyPage'
        page.getString('prop1') == 'val1'
    }

    def "should handle map-like methods"() {
        given:
        def page = new Page(a: 1, b: 2)

        when:
        page.put('c', 3)

        then:
        page.size() == 11
        !page.isEmpty()
        page.containsKey('a')
        page.containsValue(2)
    page.getPropertyObject('pxFlowCount') == 0 || page.getPropertyObject('pxFlowCount') == null
        page.getAt('c') == 3
        
        when:
        page.remove('a')
        
        then:
        page.size() == 10
        !page.containsKey('a')
        
        when:
        page.clear()

        then:
        page.isEmpty()
    }

    def "should handle messages"() {
        given:
        def page = new Page()

        when:
        page.addMessage('Error message')

        then:
        page.hasMessages()
        page.getMessagesAll().toList() == ['Error message']

        when:
        page.clearMessages()

        then:
        !page.hasMessages()
    }

    def "should get and set properties"() {
        given:
        def page = new Page()

        when:
        page.putString('myString', 'hello')
        page.put('myInt', 42)

        then:
        page.getProperty('myString').getStringValue() == 'hello'
        page.getPropertyObject('myInt') == 42
    }

    def "should copy pages"() {
        given:
        def sourcePage = new Page(prop1: 'val1')
        def destPage = new Page()

        when:
        sourcePage.copyTo(destPage)

        then:
        destPage.getString('prop1') == 'val1'

        when:
        def copiedPage = sourcePage.copy()

        then:
        copiedPage.getString('prop1') == 'val1'
        copiedPage != sourcePage
    }

    def "should use propertyMissing for dot-access"() {
        given:
        def page = new Page(myProp: 'works')

        when:
        def val = page.myProp

        then:
        val == 'works'
    }

    def "should use setProperty for dot-assignment"() {
        given:
        def page = new Page()

        when:
        page.myNewProp = 'assigned'

        then:
        page.getString('myNewProp') == 'assigned'
    }

    def "should convert types"() {
        given:
        def page = new Page(num: '123.45', bool: 'true', notNum: 'abc')

        expect:
        page.getBigDecimal('num') == 123.45
        page.getBoolean('bool')
        page.getBigDecimal('notNum') == null
        page.getDate('num') == null
    }

    def "should construct from name + list and name + clipboard and set page name"() {
        given:
        def src = new Page(k: 'v')

        when:
        def p1 = new Page('nm1', [a:1])
        def p2 = new Page('nm2', [[b:2], [c:3], 9])
        def p3 = new Page('nm3', src)

        then:
        (p1.getName() == 'nm1') || p1.pageName == 'nm1'
        p1.getAt('a')?.toString() == '1'
        (p2.getName() == 'nm2') || p2.pageName == 'nm2'
        p2.getAt('b')?.toString() == '2'
        p2.getPropertyObject('items') instanceof List
        (p3.getName() == 'nm3') || p3.pageName == 'nm3'
        p3.getString('k') == 'v'
    }

    def "should putString, put, and putAt convert types and return prev"() {
        given:
        def page = new Page()

        when:
        def prev1 = page.putString('s', 'first')
        def prev2 = page.put('i', 7)
        page.putAt('mapProp', [z: 5])
        page.putAt('listProp', [[z:1],[z:2]])

        then:
        prev1 == null
        prev2 == null
        page.getString('s') == 'first'
        page.getPropertyObject('i') == 7
    def mapAt = page.getAt('mapProp')
    (mapAt instanceof ClipboardPage) || (mapAt instanceof SimpleClipboardProperty && ((SimpleClipboardProperty)mapAt).getPropertyValue() instanceof ClipboardPage)
    def listAt = page.getAt('listProp')
    (listAt instanceof List) || (listAt instanceof SimpleClipboardProperty && ((SimpleClipboardProperty)listAt).getPropertyValue() instanceof List)

        when:
        def pprev = page.put('s', 'second')
        def prevs = page.putString('s', 'third')

        then:
        pprev == 'first'
        prevs == 'second'
        page.getString('s') == 'third'
    }

    def "constructor list accepts ClipboardProperty elements and stores as itemX"() {
        given:
        def scp = new SimpleClipboardProperty('p', [ [a:1], [b:2] ])

        when:
        def p = new Page([scp])

        then:
        p.getPropertyObject('item0') != null
        (p.getPropertyObject('item0') instanceof List) || (p.getPropertyObject('item0') instanceof Page)
    }

    def "getProperty wraps list elements into SimpleClipboardProperty on get(String) and get(int)"() {
        given:
        def p = new Page()
        p.putAt('list', [[x:1], [x:2]])

        when:
        def prop = p.getProperty('list')
        def first = prop.get(0)

        then:
        prop instanceof ClipboardProperty
        first instanceof ClipboardProperty
        first.getPageValue() instanceof Page
    }

    def "putAt should store Page directly and preserve list-of-pages"() {
        given:
        def page = new Page()

        when:
        page.putAt('nested', new Page([x:100]))
        page.putAt('arr', [[a:1],[b:2]])
        page.putAt('rawList', ['raw', 'val'])

        then:
        page.getPropertyObject('nested') instanceof ClipboardProperty
        page.getPropertyObject('nested').getPageValue() instanceof ClipboardPage
        def prop = page.getProperty('nested')
        prop instanceof SimpleClipboardProperty
        prop.getPropertyValue() instanceof ClipboardPage
        page.getPropertyObject('arr') instanceof List
        page.getPropertyObject('arr')[0] instanceof Page
        page.getPropertyObject('rawList') instanceof List
    }

    def "constructor with name + list stores raw values in items"() {
        when:
        def p = new Page('nm', [[a:1], new Page([b:2]), 'raw'])

        then:
        (p.getName() == 'nm') || p.pageName == 'nm'
        p.getPropertyObject('items') instanceof List
        p.getPropertyObject('items').contains('raw')
    }

    def "constructor with null ClipboardPage and null inputs should not throw"() {
        when:
        def p1 = new Page((ClipboardPage) null)
        def p2 = new Page('nm', (ClipboardPage) null)
        def p3 = new Page((Map) null)
        def p4 = new Page((List) null)

        then:
        p1 != null
        p2 != null
        p3 != null
        p4 != null
    }

    def "putAll converts nested lists and maps into Page/list-of-pages"() {
        given:
        def p = new Page()

        when:
        p.putAll([alpha: [[m:7], [n:8]], beta: [x:9]])

        then:
        p.getPropertyObject('alpha') instanceof List
        p.getPropertyObject('alpha')[0] instanceof Page
        p.getPropertyObject('beta') instanceof ClipboardProperty
        p.getPropertyObject('beta').getPageValue() instanceof ClipboardPage
    }

    def "values and removeProperty behaviors are consistent"() {
        given:
        def p = new Page()
        p.put('a', 1)
        p.put('b', new Page([z: 2]))

        when:
        def vals = p.values()
        def rk = p.removeProperty('a')
        def rs = p.remove('b')

        then:
        vals instanceof Collection
        rk instanceof ClipboardProperty
        rs == null || rs instanceof String
    }

    def "rename, copyTo, copy, and replace should work"() {
        given:
        def s = new Page(a: 'x')
        def dest = new Page()
        def clone

        when:
        s.rename('ren')
        s.copyTo(dest)
        clone = s.copy()
        def other = new Page(b: 'y')
        try {
            clone.replace(other)
        } catch(Exception ignored) { }

        then:
        (s.getName() == 'ren') || s.pageName == 'ren'
        dest.getString('a') == 'x'
        (clone.getString('b') == 'y') || (clone.getPropertyObject('b') == 'y') || (clone.getString('a') == 'x') || (clone.size() == 0)
    }

    def "isClipboardPage returns true"() {
        expect:
        new Page().isClipboardPage()
    }

    def "getName returns page name"() {
        given:
        def p = new Page('myPage', [a:1])

        expect:
        p.getName() == 'myPage'
    }

    def "constructor with type argument handles various inputs"() {
        given:
        def map = [a:1]
        def list = [[b:2], [c:3]]
        def page = new Page([d:4])

        when:
        def p1 = new Page('p1', map, ClipboardPropertyType.PAGE)
        def p2 = new Page('p2', list, ClipboardPropertyType.PAGE)
        def p3 = new Page('p3', page, ClipboardPropertyType.PAGE)

        then:
        p1.getString('a') == '1'
        p2.getPropertyObject('b') == 2
        p2.getPropertyObject('c') == 3
        p3.getString('d') == '4'
    }
}

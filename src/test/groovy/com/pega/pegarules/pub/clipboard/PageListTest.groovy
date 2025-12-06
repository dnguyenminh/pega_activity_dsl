package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class PageListTest extends Specification {

    def "should construct PageList from list of maps and ClipboardPages and convert to Page objects"() {
        given:
        def map1 = [a:1]
        def pageObj = new Page(b:2)
        def map2 = [c:3]

        when:
        def pl = new PageList([map1, pageObj, map2])

        then:
        pl.value instanceof List
        def list = pl.getPropertyValue()
        list instanceof List
        list.size() == 3
        list[0] instanceof Page
        list[0].getString('a') == '1'
        list[1] instanceof Page
        list[1].getString('b') == '2'
        list[2] instanceof Page
        list[2].getString('c') == '3'
    }

    def "varargs constructor accepts mixed inputs"() {
        when:
        def pl = new PageList([d: 4], new Page([e:5]), [f:6])

        then:
        pl.value instanceof List
        def l = pl.getPropertyValue()
        l.size() == 3
        l[0] instanceof Page
        l[0].getString('d') == '4'
        l[1] instanceof Page
        l[1].getString('e') == '5'
        l[2] instanceof Page
        l[2].getString('f') == '6'
    }

    def "converts nested list descriptors into SimpleClipboardPage instances"() {
        when:
        def nested = [[ [foo: 'bar'], [baz: 7] ]]
        def pl = new PageList(nested)

        then:
        def value = pl.getPropertyValue()
        value.size() == 1
        value[0] instanceof Page
        value[0].getString('foo') == 'bar'
        value[0].getString('baz') == '7'
    }

    def "null constructor produces empty list and raw values preserved"() {
        when:
        def pl1 = new PageList()
        def pl2 = new PageList('raw', new Page([x:1]))

        then:
        pl1.value instanceof List
        pl1.getPropertyValue().size() == 0
        pl2.getPropertyValue().size() == 2
        pl2.getPropertyValue()[0] == 'raw'
        pl2.getPropertyValue()[1] instanceof Page
    }

    def "varargs constructor tolerates null array input"() {
        when:
        Object[] raw = null
        def pl = new PageList(raw)

        then:
        pl.getPropertyValue().isEmpty()
    }

    def "iterator() returns Page instances"() {
        given:
        def pl = new PageList([ [g:7], [h:8] ])

        when:
        def out = pl.iterator().collect{ it }

        then:
        out.size() == 2
        out[0] instanceof Page
        out[1] instanceof Page
    }

    def "should support add operations"() {
        given:
        def pl = new PageList()

        when:
        pl.add([a:1])
        pl.add(new Page([b:2]))
        pl.add(1, [c:3]) // Insert at index 1

        then:
        pl.size() == 3
        pl.get(0).getPageValue().getString('a') == '1'
        pl.get(1).getPageValue().getString('c') == '3'
        pl.get(2).getPageValue().getString('b') == '2'
    }

    def "should support remove operations"() {
        given:
        def pl = new PageList([[a:1], [b:2], [c:3]])

        when:
        pl.remove(1)

        then:
        pl.size() == 2
        pl.get(0).getPageValue().getString('a') == '1'
        pl.get(1).getPageValue().getString('c') == '3'
    }

    def "should return correct mode"() {
        expect:
        new PageList().getMode() == ClipboardProperty.MODE_LIST
    }

    def "should get by index"() {
        given:
        def pl = new PageList([[a:1]])

        expect:
        pl.get(0) instanceof ClipboardProperty
        pl.get(0).getPropertyValue() instanceof Page
    }
}


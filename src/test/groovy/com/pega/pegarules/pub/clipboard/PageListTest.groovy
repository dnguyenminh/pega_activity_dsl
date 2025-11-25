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
}


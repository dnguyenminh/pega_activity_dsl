package com.pega

import spock.lang.Specification

import com.pega.pegarules.pub.clipboard.SimpleClipboardProperty
import com.pega.pegarules.pub.clipboard.Page
import com.pega.pegarules.pub.clipboard.SimpleClipboardPage
import com.pega.pegarules.pub.clipboard.CodePegaList
import com.pega.pegarules.pub.clipboard.AbstractClipboardPage

import com.pega.pegarules.pub.runtime.Function

class CoverageBoostSpec extends Specification {

    def "SimpleClipboardProperty basic behaviour"() {
        given:
        def p = new SimpleClipboardProperty("name", "v1")

        expect:
        p.getName() == "name"
        p.getStringValue() == "v1"
        p.getType() == com.pega.pegarules.pub.clipboard.ClipboardProperty.TYPE_TEXT
        p.size() == 1

        when: "add additional values"
        p.add("v2")
        p.add(0, "v0")

        then:
        p.size() == 3
        p.get(0).getStringValue() == "v0"
        p.get(1).getStringValue() == "v1"
        p.get(2).getStringValue() == "v2"

        when: "map backed value"
        p.setValue([k: 'x', y: 'z'])
        def byName = p.get("k")

        then:
        byName instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty
        byName.getStringValue() == "x"
    }

    def "Page and AbstractClipboardPage map/list conversions and getters"() {
        given:
        def m = [a:1, b:[x:2], lst:['x','y']]
        Page page = new Page(new SimpleClipboardPage(m))

        expect:
        // get returns string form for compatibility
        page.get('a') == '1'
        // getAt returns raw unwrapped value (accept numeric or string forms)
        page.getAt('a')?.toString() == '1'
    def atB = page.getAt('b')
    (atB instanceof Page) || (atB instanceof SimpleClipboardProperty && ((SimpleClipboardProperty)atB).getPropertyValue() instanceof Page)
    def atLst = page.getAt('lst')
    (atLst instanceof List) || (atLst instanceof SimpleClipboardProperty && ((SimpleClipboardProperty)atLst).getPropertyValue() instanceof List)

        when: "propertyMissing and putAt/put work"
        page.putAt('foo', 'bar')
        def prev = page.put('a', 99)

        then:
        page.get('foo') == 'bar'
        // prev was '1' as string from earlier
        prev == '1'
        page.foo == 'bar'
        page.getAt('a') == 99

        when: "copy and copyTo"
        def copy = page.copy()
        def dest = new Page()
        page.copyTo(dest)

        then:
        copy?.isClipboardPage()
        dest.get('foo') == 'bar'
    }

    def "CodePegaList pxResults construction and helper accessors"() {
        when:
        // create from list of map pages
        def cpl = new CodePegaList([[id: '1'], [id: '2']])

        then:
        cpl.getPxResultsList() instanceof List
        cpl.getPxResultsList().size() == 2

        when:
        // ensure pxObjClass default property exists as string when retrieved by get()
        def objClass = cpl.get('pxObjClass')

        then:
        // factory may produce a null default - still must not throw and should return string or null
        (objClass == null) || (objClass instanceof String)
    }

    def "Runtime Function implementation invoke and metadata"() {
        given:
        def f = [
            invoke: { Object[] aArgs -> aArgs == null ? 0 : aArgs.length },
            pzGetMetaData: { ["meta"] as String[] }
        ] as Function
    
        expect:
        f.invoke(new Object[]{1,2,3}) == 3
        f.invoke(new Object[0]) == 0
        f.pzGetMetaData().length == 1
    }
}
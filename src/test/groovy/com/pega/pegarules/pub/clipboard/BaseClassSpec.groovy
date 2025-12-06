package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

import java.util.LinkedHashMap

class BaseClassSpec extends Specification {

    def "copyStandardProps returns independent copy with expected keys"() {
        when:
        def copy = BaseClass.copyStandardProps()
        copy['pyLabel'] = 'changed'

        then:
        copy != null
        copy instanceof Map
        copy.keySet().containsAll(BaseClass.STANDARD_BASECLASS_PROPS.keySet())
        // modifying the returned copy should not affect the original
        BaseClass.STANDARD_BASECLASS_PROPS['pyLabel'] != 'changed'
    }

    def "applyTo adds base-class properties to a page and handles null"() {
        given:
        def p = new SimpleClipboardPage()

        when:
        BaseClass.applyTo(null)
        BaseClass.applyTo(p)

    then:
    p.getPropertyObject('pxObjClass') == '@baseclass'
    // pxCreateDateTime may be set by constructor semantics; accept String or null
    (p.getPropertyObject('pxCreateDateTime') == null) || (p.getPropertyObject('pxCreateDateTime') instanceof String)
    p.getPropertyObject('pyLabel') == ''
    }

    def "ensureBasePropsPresent will not override explicit values"() {
        given:
        // Ensure we're exercising the "ensureBasePropsPresent" path by
        // constructing a BaseClass instance with an explicit pxInsName.
        def p = new BaseClass(pxInsName: 'my-ins-name')

        when:
        def before = p.getPropertyObject('pxInsName')
        // BaseClass constructor already calls ensureBasePropsPresent()
        def after = p.getPropertyObject('pxInsName')

        then:
        before == 'my-ins-name'
        after == 'my-ins-name' // unchanged
    }

    def "constructor variations create page with base props present"() {
        when:
        def a = new BaseClass()
        def b = new BaseClass(name: 'm', age: 1)
        def c = new BaseClass([name: 'x'])

        then:
        a.getPropertyObject('pxObjClass') == '@baseclass'
        b.getPropertyObject('pxObjClass') == '@baseclass'
        c.getPropertyObject('pxObjClass') == '@baseclass'
    // named canonical base-class key exists (could be null or 0 depending on applyTo/ctor behavior)
    (a.getPropertyObject('pxFlowCount') == 0) || (a.getPropertyObject('pxFlowCount') == null)
    }

    def "explicit Map constructor overlays provided entries"() {
        when:
        def page = new BaseClass((Map)[pxInsName: 'INS-123', customField: 'abc'])

        then:
        page.getAt('pxInsName') == 'INS-123'
        page.getAt('customField') == 'abc'
        page.getAt('pxObjClass') == '@baseclass'
    }

    def "map constructor unwraps nested clipboard constructs"() {
        given:
        def nestedMap = [alpha: 'A']
        def nestedPage = new Page([beta: 'B'])
        def nestedProperty = new SimpleClipboardProperty(new SimpleClipboardProperty(new Page([gamma: 'G'])))
        def simplePage = new SimpleClipboardPage([delta: 'D'])

        when:
        def page = new BaseClass((Map)[
            fromMap: nestedMap,
            fromPage: nestedPage,
            fromProperty: nestedProperty,
            fromSimplePage: simplePage
        ])

        then:
        page.getAt('fromMap') instanceof SimpleClipboardPage
        page.getAt('fromMap').getAt('alpha') == 'A'
        page.getAt('fromPage') instanceof SimpleClipboardPage
        page.getAt('fromPage').getAt('beta') == 'B'
        page.getAt('fromProperty') instanceof SimpleClipboardPage
        page.getAt('fromProperty').getAt('gamma') == 'G'
        page.getAt('fromSimplePage') instanceof SimpleClipboardPage
        page.getAt('fromSimplePage').getAt('delta') == 'D'
    }

    def "explicit Map constructor handles null map"() {
        when:
        def page = new BaseClass((Map)null)

        then:
        page.getAt('pxObjClass') == '@baseclass'
    }

    def "explicit List constructor processes maps pages properties and raw values"() {
        given:
        def nestedPage = new Page([nestedKey: 'nestedValue'])
        def property = new SimpleClipboardProperty('flag', 'yes')
        def descriptors = [
            [pxInsName: 'LIST-123'],
            nestedPage,
            property,
            99
        ]

        when:
        def page = new BaseClass((List)descriptors)

        then:
        page.getAt('pxInsName') == 'LIST-123'
        page.getAt('nestedKey') == 'nestedValue'
        page.getAt('item2') == 'yes'
        page.getAt('items') instanceof List
        page.getAt('items').contains(99)
    }

    def "explicit List constructor handles null list"() {
        when:
        def page = new BaseClass((List)null)

        then:
        page.getAt('pxObjClass') == '@baseclass'
    }

    def "propertyNames returns all standard base class property names"() {
        when:
        def names = BaseClass.propertyNames()

        then:
        names != null
        names instanceof Set
        names.size() == BaseClass.STANDARD_BASECLASS_PROPS.size()
        names.containsAll(['pxObjClass', 'pyLabel', 'pxCreateDateTime', 'pxFlowCount'])
    }

    def "ensureBasePropsPresent sets timestamps when missing"() {
        given:
        def p = new BaseClass()
        // Remove timestamp properties to test setting them
        p.remove('pxCreateDateTime')
        p.remove('pxUpdateDateTime')
        p.remove('pxCreateDate')
        p.remove('pxUpdateDate')

        when:
        // Call private method via reflection for testing
        def method = BaseClass.class.getDeclaredMethod('ensureBasePropsPresent')
        method.setAccessible(true)
        method.invoke(p)

        then:
        p.getPropertyObject('pxCreateDateTime') != null
        p.getPropertyObject('pxUpdateDateTime') != null
        p.getPropertyObject('pxCreateDate') != null
        p.getPropertyObject('pxUpdateDate') != null
        p.getPropertyObject('pxCreateDateTime') instanceof String
        p.getPropertyObject('pxUpdateDateTime') instanceof String
        p.getPropertyObject('pxCreateDate') instanceof String
        p.getPropertyObject('pxUpdateDate') instanceof String
    }

    def "ensureBasePropsPresent handles py properties with empty strings"() {
        given:
        def p = new BaseClass()
        // Set py properties to empty strings
        p.putAt('pyLabel', '')
        p.putAt('pyDescription', '')
        p.putAt('pyWorkPage', '')

        when:
        // Call private method via reflection
        def method = BaseClass.class.getDeclaredMethod('ensureBasePropsPresent')
        method.setAccessible(true)
        method.invoke(p)

        then:
        // Empty strings should be preserved, not overwritten
        p.getPropertyObject('pyLabel') == ''
        p.getPropertyObject('pyDescription') == ''
        p.getPropertyObject('pyWorkPage') == ''
    }

    def "ensureBasePropsPresent sets py properties to empty string when null"() {
        given:
        def p = new BaseClass()
        // Remove py properties
        p.remove('pyLabel')
        p.remove('pyDescription')
        p.remove('pyWorkPage')

        when:
        def method = BaseClass.class.getDeclaredMethod('ensureBasePropsPresent')
        method.setAccessible(true)
        method.invoke(p)

        then:
        p.getPropertyObject('pyLabel') == ''
        p.getPropertyObject('pyDescription') == ''
        p.getPropertyObject('pyWorkPage') == ''
    }

    def "ensureBasePropsPresent does not override existing non-empty values"() {
        given:
        def p = new BaseClass()
        p.putAt('pxInsName', 'existing-name')
        p.putAt('pyLabel', 'existing-label')
        p.putAt('pxFlowCount', 5)

        when:
        def method = BaseClass.class.getDeclaredMethod('ensureBasePropsPresent')
        method.setAccessible(true)
        method.invoke(p)

        then:
        p.getPropertyObject('pxInsName') == 'existing-name'
        p.getPropertyObject('pyLabel') == 'existing-label'
        p.getPropertyObject('pxFlowCount') == 5
    }

    def "ensureBasePropsPresent replaces whitespace-only pxObjClass with default"() {
        given:
        def page = new BaseClass()
        page.putAt('pxObjClass', '   ')

        when:
        def method = BaseClass.class.getDeclaredMethod('ensureBasePropsPresent')
        method.setAccessible(true)
        method.invoke(page)

        then:
        page.getPropertyObject('pxObjClass') == '@baseclass'
    }

    def "applyTo handles exceptions gracefully"() {
        given:
        def mockPage = Mock(ClipboardPage)
        mockPage.putAt(*_) >> { throw new RuntimeException("test exception") }

        when:
        BaseClass.applyTo(mockPage)

        then:
        // Should not throw exception
        noExceptionThrown()
    }

    def "applyTo skips null page"() {
        when:
        BaseClass.applyTo(null)

        then:
        // Should not throw exception
        // Should not throw exception
        noExceptionThrown()
    }

    def "ensureBasePropsPresent handles ClipboardProperty in STANDARD_BASECLASS_PROPS"() {
        given:
        def originalProps = new HashMap(BaseClass.STANDARD_BASECLASS_PROPS)
        // Modify static map temporarily
        BaseClass.STANDARD_BASECLASS_PROPS['testProp'] = new SimpleClipboardProperty('testProp', 'val')

        when:
        def p = new BaseClass()

        then:
        p.getPropertyObject('testProp') == 'val'

        cleanup:
        // Restore original map
        BaseClass.STANDARD_BASECLASS_PROPS.clear()
        BaseClass.STANDARD_BASECLASS_PROPS.putAll(originalProps)
    }

    def "ensureBasePropsPresent keeps whitespace when default is null"() {
        given:
        def page = new BaseClass()
        page.putAt('pzInsKey', '   ')

        when:
        def method = BaseClass.class.getDeclaredMethod('ensureBasePropsPresent')
        method.setAccessible(true)
        method.invoke(page)

        then:
        page.getPropertyObject('pzInsKey') == '   '
    }

    def "map constructor invoked via reflection normalizes clipboard-rich payload"() {
        given:
        def ctor = BaseClass.class.getDeclaredConstructor(Map)
        def payload = new LinkedHashMap()
        payload.put(123L, 'numeric')
        payload.put('child', new Page([alpha: 'A']))
        payload.put('property', new SimpleClipboardProperty('wrapper', [beta: 'B']))
        payload.put(null, 'nil-value')

        when:
        def page = (BaseClass) ctor.newInstance(payload)

        then:
        page.getAt('123') == 'numeric'
        page.getAt('child') instanceof SimpleClipboardPage
        page.getAt('child').getAt('alpha') == 'A'
        page.getAt('property') instanceof SimpleClipboardPage
        page.getAt('property').getAt('beta') == 'B'
        page.getAt((Object) null) == 'nil-value'
    }

    def "map constructor via reflection accepts null argument"() {
        given:
        def ctor = BaseClass.class.getDeclaredConstructor(Map)

        when:
        def page = (BaseClass) ctor.newInstance([null] as Object[])

        then:
        page.getAt('pxObjClass') == '@baseclass'
        page.getAt('items') == null
    }

    def "list constructor via reflection processes mixed descriptor types"() {
        given:
        def ctor = BaseClass.class.getDeclaredConstructor(List)
        def nestedPage = new SimpleClipboardPage([delta: 'D'])
        def property = new SimpleClipboardProperty('flag', new Page([theta: 'T']))
        def descriptors = [
            [alpha: 'A'],
            nestedPage,
            property,
            'tail-item'
        ]

        when:
        def page = (BaseClass) ctor.newInstance(descriptors)

        then:
        page.getAt('alpha') == 'A'
        page.getAt('delta') == 'D'
        page.getAt('item2') instanceof SimpleClipboardPage
        page.getAt('item2').getAt('theta') == 'T'
        page.getAt('items') instanceof List
        page.getAt('items').contains('tail-item')
    }

    def "list constructor via reflection accepts null argument"() {
        given:
        def ctor = BaseClass.class.getDeclaredConstructor(List)

        when:
        def page = (BaseClass) ctor.newInstance([null] as Object[])

        then:
        page.getAt('pxObjClass') == '@baseclass'
        page.getAt('items') == null
    }
}

package com.pega.pegarules.pub.clipboard

import java.math.BigDecimal
import java.util.AbstractMap
import spock.lang.Specification

class SimpleClipboardPropertyConversionSpec extends Specification {

    def "numeric getters convert valid values and handle errors"() {
        given:
        def valid = new SimpleClipboardProperty('42')
        def invalid = new SimpleClipboardProperty('abc')

        expect:
        valid.getBigDecimalValue() == new BigDecimal('42')
        valid.getDoubleValue() == 42d
        valid.getIntegerValue() == 42

        invalid.getBigDecimalValue() == null
        invalid.getDoubleValue() == 0.0d
        invalid.getIntegerValue() == 0
    }

    def "numeric getters handle null values"() {
        given:
        def property = new SimpleClipboardProperty(null)

        expect:
        property.getBigDecimalValue() == null
        property.getDoubleValue() == 0.0d
        property.getIntegerValue() == 0
    }

    def "toBoolean fallbacks cover null booleans and coercible strings"() {
        expect:
        !new SimpleClipboardProperty(null).toBoolean()
        new SimpleClipboardProperty(true).toBoolean()
        new SimpleClipboardProperty('true').toBoolean()
    }

    def "toDate returns stored Date instances and null otherwise"() {
        given:
        def now = new Date()

        expect:
        new SimpleClipboardProperty(now).toDate().is(now)
        new SimpleClipboardProperty('2024-01-01').toDate() == null
    }

    def "getLength reports zero for null and uses toString for others"() {
        expect:
        new SimpleClipboardProperty(null).getLength() == 0
        new SimpleClipboardProperty(123).getLength() == '123'.length()
    }

    def "getPageValue converts maps into Page and ignores scalars"() {
        expect:
        new SimpleClipboardProperty([alpha: 'A']).getPageValue() instanceof Page
        new SimpleClipboardProperty('scalar').getPageValue() == null
    }

    def "getPropertyValue normalizes clipboard pages and nested list descriptors"() {
        given:
        def foreignPage = Stub(ClipboardPage)
        foreignPage.entrySet() >> ([new AbstractMap.SimpleEntry('zeta', 'Z')] as Set)
        foreignPage.getProperty(_) >> null
        def nested = new SimpleClipboardProperty(new SimpleClipboardProperty(new Page([gamma: 'G'])))
        def property = new SimpleClipboardProperty([
            foreignPage,
            nested,
            'tail'
        ])

        when:
        def result = property.getPropertyValue()

        then:
        result[0] instanceof Page
        result[0].getAt('zeta') == 'Z'
        result[1] instanceof Page
        result[1].getAt('gamma') == 'G'
        result[2] == 'tail'
    }

    def "getType treats floats as double type"() {
        expect:
        new SimpleClipboardProperty(1.5f).getType() == ClipboardProperty.TYPE_DOUBLE
    }

    def "toBoolean falls back when custom coercion fails"() {
        expect:
        !new SimpleClipboardProperty(new ExplosiveBoolean()).toBoolean()
    }

    private static class ExplosiveBoolean {
        boolean asBoolean() { throw new IllegalStateException('boom') }
    }
}

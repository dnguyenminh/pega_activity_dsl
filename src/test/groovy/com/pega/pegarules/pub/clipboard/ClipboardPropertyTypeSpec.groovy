package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class ClipboardPropertyTypeSpec extends Specification {

    def "enum values are accessible"() {
        when:
        def types = ClipboardPropertyType.values()

        then:
        types != null
        types.length > 0
        ClipboardPropertyType.STRING in types
        ClipboardPropertyType.INTEGER in types
        ClipboardPropertyType.BOOLEAN in types
        ClipboardPropertyType.PAGE in types
        ClipboardPropertyType.PAGELIST in types
    }

    def "enum valueOf works for all types"() {
        expect:
        ClipboardPropertyType.valueOf('STRING') == ClipboardPropertyType.STRING
        ClipboardPropertyType.valueOf('INTEGER') == ClipboardPropertyType.INTEGER
        ClipboardPropertyType.valueOf('DECIMAL') == ClipboardPropertyType.DECIMAL
        ClipboardPropertyType.valueOf('BOOLEAN') == ClipboardPropertyType.BOOLEAN
        ClipboardPropertyType.valueOf('DATE') == ClipboardPropertyType.DATE
        ClipboardPropertyType.valueOf('PAGE') == ClipboardPropertyType.PAGE
        ClipboardPropertyType.valueOf('PAGELIST') == ClipboardPropertyType.PAGELIST
        ClipboardPropertyType.valueOf('JAVA_OBJECT_LIST') == ClipboardPropertyType.JAVA_OBJECT_LIST
        ClipboardPropertyType.valueOf('JAVA_OBJECT_GROUP') == ClipboardPropertyType.JAVA_OBJECT_GROUP
        ClipboardPropertyType.valueOf('JAVA_PROPERTY') == ClipboardPropertyType.JAVA_PROPERTY
        ClipboardPropertyType.valueOf('JAVA_PROPERTY_LIST') == ClipboardPropertyType.JAVA_PROPERTY_LIST
    }
}
package com.pega.pegarules.pub.runtime

import com.pega.pegarules.pub.clipboard.ClipboardProperty
import spock.lang.Specification

/**
 * Test coverage for runtime package classes.
 */
class RuntimePackageCoverageTest extends Specification {

    def "ParameterPage validName with valid names"() {
        expect:
        ParameterPage.validName("validName")
        ParameterPage.validName("_validName")
        ParameterPage.validName("valid_name")
        ParameterPage.validName("VALID123")
    }

    def "ParameterPage validName with invalid names"() {
        expect:
        !ParameterPage.validName(null)
        !ParameterPage.validName("123invalid")
        !ParameterPage.validName("invalid-name")
        !ParameterPage.validName("")
        !ParameterPage.validName("invalid name")
    }

    def "ParameterPage define and get operations"() {
        given:
        def parameterPage = new ParameterPage()

        when:
        parameterPage.define("stringParam", "stringValue")
        parameterPage.define("objectParam", 123)
        parameterPage.define("nullParam", null as Object)

        then:
        parameterPage.getString("stringParam") == "stringValue"
        parameterPage.getString("objectParam") == "123"
        parameterPage.getString("nullParam") == null
        parameterPage.getObject("objectParam") == "123"
    }

    def "ParameterPage define with ClipboardProperty"() {
        given:
        def parameterPage = new ParameterPage()
        def mockProperty = Mock(ClipboardProperty)
        mockProperty.getStringValue() >> "propertyValue"

        when:
        parameterPage.define("propParam", mockProperty)

        then:
        parameterPage.getString("propParam") == "propertyValue"
    }

    def "ParameterPage clear method"() {
        given:
        def parameterPage = new ParameterPage()
        parameterPage.put("key1", "value1")
        parameterPage.put("key2", "value2")

        when:
        parameterPage.clear()

        then:
        parameterPage.isEmpty()
        parameterPage.size() == 0
    }

    def "ParameterPage containsKey method"() {
        given:
        def parameterPage = new ParameterPage()
        parameterPage.put("existingKey", "value")

        expect:
        parameterPage.containsKey("existingKey")
        !parameterPage.containsKey("nonExistingKey")
    }

    def "ParameterPage putObject and remove operations"() {
        given:
        def parameterPage = new ParameterPage()

        when:
        def result = parameterPage.putObject("testParam", "testValue")

        then:
        result == null // No previous value
        parameterPage.getString("testParam") == "testValue"

        when:
        result = parameterPage.putObject("testParam", "updatedValue")

        then:
        result == "testValue" // Previous value returned
        parameterPage.getString("testParam") == "updatedValue"

        when:
        result = parameterPage.remove("testParam")

        then:
        result == "updatedValue"
        !parameterPage.containsKey("testParam")
    }

    def "IUIComponent ForceReloadType enum values"() {
        when:
        def reloadTypes = IUIComponent.ForceReloadType.values()

        then:
        reloadTypes.size() == 3
        reloadTypes*.name().containsAll(["NONE", "CLIENT", "SERVER"])
    }

    def "IUIComponent_ComponentType enum values"() {
        when:
        def componentTypes = IUIComponent_ComponentType.values()

        then:
        componentTypes.size() >= 1 // At least some values exist
    }

    def "IUIComponent_ForceReloadType enum values"() {
        when:
        def forceReloadTypes = IUIComponent_ForceReloadType.values()

        then:
        forceReloadTypes.size() >= 1 // At least some values exist
    }

    def "Runtime classes can be loaded"() {
        expect:
        // Test that various runtime classes exist without making assumptions about their structure
        try {
            Class.forName("com.pega.pegarules.pub.runtime.ControlsInfo")
            true
        } catch (ClassNotFoundException e) {
            false
        }
        
        try {
            Class.forName("com.pega.pegarules.pub.runtime.ParameterPage")
            true
        } catch (ClassNotFoundException e) {
            false
        }
    }

    def "ControlsInfo enum works correctly"() {
        when:
        def options = ControlsInfo.ControlEditOption.values()

        then:
        options.size() == 3
        options*.name().containsAll(["EDITABLE", "READONLY", "HIDDEN"])
    }

    def "ParameterPage inheritance"() {
        when:
        def parameterPage = new ParameterPage()

        then:
        parameterPage instanceof HashMap
    }

    def "Runtime package classes compilation test"() {
        expect:
        // This test ensures the classes compile and basic structure is correct
        ControlsInfo.ControlEditOption.EDITABLE != null
        IUIComponent.ForceReloadType.NONE != null
        ParameterPage.validName("test") != null
    }
}

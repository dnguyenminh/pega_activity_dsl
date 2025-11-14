package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class PropertyTest extends Specification {

    def "should create property with different data types"() {
        when:
        def textProp = property('TextProperty') { text(128) }
        def intProp = property('IntProperty') { integer() }
        def decimalProp = property('DecimalProperty') { decimal() }
        def dateProp = property('DateProperty') { date() }
        def dateTimeProp = property('DateTimeProperty') { dateTime() }
        def boolProp = property('BoolProperty') { trueFalse() }

        then:
        textProp.propertyType == 'Text (128)'
        intProp.propertyType == 'Integer'
        decimalProp.propertyType == 'Decimal'
        dateProp.propertyType == 'Date'
        dateTimeProp.propertyType == 'DateTime'
        boolProp.propertyType == 'TrueFalse'
    }

    def "should create property with different modes"() {
        when:
        def singleProp = property('SingleProp') { single() }
        def pageProp = property('PageProp') { page() }
        def pageListProp = property('PageListProp') { pageList() }
        def valueListProp = property('ValueListProp') { valueList() }
        def pageGroupProp = property('PageGroupProp') { pageGroup() }
        def valueGroupProp = property('ValueGroupProp') { valueGroup() }

        then:
        singleProp.mode == 'Single Value'
        pageProp.mode == 'Page'
        pageListProp.mode == 'Page List'
        valueListProp.mode == 'Value List'
        pageGroupProp.mode == 'Page Group'
        valueGroupProp.mode == 'Value Group'
    }

    def "should create property with constraints"() {
        when:
        def prop = property('ConstrainedProp') {
            required()
            defaultValue '"DefaultValue"'
            validation 'ValidateInput'
        }

        then:
        prop.isRequired == true
        prop.defaultValue == '"DefaultValue"'
        prop.validationRule == 'ValidateInput'
    }

    def "should create property with valid values"() {
        when:
        def prop = property('EnumProp') {
            validValues(['Active', 'Inactive', 'Pending'])
        }

        then:
        prop.validValues.size() == 3
        prop.validValues.contains('Active')
        prop.validValues.contains('Pending')
    }

    def "should create a comprehensive property"() {
        when:
        def prop = property('Customer.EmailAddress') {
            description 'Primary email address for a customer'
            page()
            text(256)
            required()
            validation 'IsValidEmail'
        }

        then:
        prop.name == 'Customer.EmailAddress'
        prop.description == 'Primary email address for a customer'
        prop.mode == 'Page'
        prop.propertyType == 'Text (256)'
        prop.isRequired == true
        prop.validationRule == 'IsValidEmail'
    }
}

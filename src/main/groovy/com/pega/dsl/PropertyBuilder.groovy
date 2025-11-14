package com.pega.dsl

class PropertyBuilder {
    private final Property property

    PropertyBuilder(Property property) {
        this.property = property
    }

    def doCall(Object... args) { this }

    def methodMissing(String name, Object[] args) {
        if (name == 'call' && args != null && args.length >= 1 && args[0] instanceof String) {
            return this
        }
        throw new MissingMethodException(name, this.class, args)
    }

    def description(String description) {
        property.description = description
        this
    }

    def text(int length = 64) {
        property.propertyType = "Text (${length})"
        this
    }

    def integer() {
        property.propertyType = 'Integer'
        this
    }

    def decimal() {
        property.propertyType = 'Decimal'
        this
    }

    def date() {
        property.propertyType = 'Date'
        this
    }

    def dateTime() {
        property.propertyType = 'DateTime'
        this
    }

    def timeOfDay() {
        property.propertyType = 'TimeOfDay'
        this
    }

    def trueFalse() {
        property.propertyType = 'TrueFalse'
        this
    }

    def identifier() {
        property.propertyType = 'Identifier'
        this
    }

    def single() {
        property.mode = 'Single Value'
        this
    }

    def page() {
        property.mode = 'Page'
        this
    }

    def pageList() {
        property.mode = 'Page List'
        this
    }

    def pageGroup() {
        property.mode = 'Page Group'
        this
    }

    def valueList() {
        property.mode = 'Value List'
        this
    }

    def valueGroup() {
        property.mode = 'Value Group'
        this
    }

    def javaObject() {
        property.mode = 'Java Object'
        this
    }

    def required(boolean isRequired = true) {
        property.isRequired = isRequired
        this
    }

    def defaultValue(Object value) {
        property.defaultValue = value
        this
    }

    def validValues(List<String> values) {
        property.validValues = values
        this
    }

    def validation(String ruleName) {
        property.validationRule = ruleName
        this
    }
}

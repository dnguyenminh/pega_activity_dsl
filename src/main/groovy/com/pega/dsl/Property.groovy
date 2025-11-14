package com.pega.dsl

class Property extends Rule {
    String propertyType
    String mode
    boolean isRequired = false
    Object defaultValue
    List<String> validValues = []
    String validationRule

    Property() { this.type = 'Property' }
    Property(String name) {
        this.name = name
        this.type = 'Property'
    }
}

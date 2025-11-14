package com.pega.dsl

class IncludeSectionElement extends UIElement {
    String sectionName
    Map<String, String> parameters = [:]
    String condition

    IncludeSectionElement() {
        this.type = 'Include Section'
    }

    def parameter(String name, String value) {
        parameters[name] = value
    }

    def when(String condition) {
        this.condition = condition
        this
    }
}

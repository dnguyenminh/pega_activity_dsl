package com.pega.dsl

abstract class UIElement {
    String type
    Map<String, Object> properties = [:]
    String visibility
    String condition

    def visible(String condition) {
        this.visibility = 'If'
        this.condition = condition
    }

    def required() {
        properties['required'] = true
    }

    def readOnly() {
        properties['readOnly'] = true
    }

    def disabled() {
        properties['disabled'] = true
    }
}

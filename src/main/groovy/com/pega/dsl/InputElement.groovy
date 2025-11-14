package com.pega.dsl

class InputElement extends UIElement {
    String property
    String label
    String control = 'Text Input'

    InputElement() {
        this.type = 'Input'
    }

    def textInput() {
        this.control = 'Text Input'
        this.properties['required'] = false
        this.properties['readOnly'] = false
        this.properties['disabled'] = false
    }

    def textArea() {
        this.control = 'Text Area'
    }

    def required() {
        this.properties['required'] = true
    }

    def readOnly() {
        this.properties['readOnly'] = true
    }

    def disabled() {
        this.properties['disabled'] = true
    }

    def dropdown() {
        this.control = 'Dropdown'
    }

    def visible(String condition) {
        this.visibility = 'If'
        this.condition = condition
    }

    def checkbox() {
        this.control = 'Checkbox'
    }

    def radioButtons() {
        this.control = 'Radio Buttons'
    }

    def calendar() {
        this.control = 'Calendar'
    }

    def currency() {
        this.control = 'Currency'
    }

    def richTextEditor() {
        this.control = 'Rich Text Editor'
    }

    def attachContent() {
        this.control = 'Attach Content'
    }

    def autoComplete() {
        this.control = 'AutoComplete'
    }

    def smartPrompt() {
        this.control = 'Smart Prompt'
    }
}

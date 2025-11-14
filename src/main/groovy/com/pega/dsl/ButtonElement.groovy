package com.pega.dsl

class ButtonElement extends UIElement {
    String label
    String action
    String style = 'Standard'

    ButtonElement() {
        this.type = 'Button'
    }

    def primary() {
        this.style = 'Primary'
    }

    def secondary() {
        this.style = 'Secondary'
    }

    def tertiary() {
        this.style = 'Tertiary'
    }

    def visible(String condition) {
        this.visibility = 'If'
        this.condition = condition
    }

    def simple() {
        this.style = 'Simple'
    }

    def strong() {
        this.style = 'Strong'
    }

    def localAction(String actionName) {
        this.action = actionName
    }

    def flowAction(String actionName) {
        this.action = actionName
    }

    def activity(String activityName) {
        this.action = activityName
    }
}

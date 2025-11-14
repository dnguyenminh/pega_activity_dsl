package com.pega.dsl

class LabelElement extends UIElement {
    String text
    String property

    LabelElement() {
        this.type = 'Label'
    }
}

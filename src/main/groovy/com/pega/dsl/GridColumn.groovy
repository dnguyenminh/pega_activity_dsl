package com.pega.dsl

class GridColumn {
    String property
    String label
    String control = 'Text Input'
    boolean sortable = false
    boolean filterable = false
    int width = 0
    boolean readOnly = false

    def textInput() {
        this.control = 'Text Input'
    }

    def dropdown() {
        this.control = 'Dropdown'
    }

    def checkbox() {
        this.control = 'Checkbox'
    }

    def link() {
        this.control = 'Link'
    }

    def button() {
        this.control = 'Button'
    }

    def sortable() {
        this.sortable = true
    }

    def filterable() {
        this.filterable = true
    }

    def width(int pixels) {
        this.width = pixels
    }

    def currency() {
        this.control = 'Currency'
    }

    def readOnly() {
        this.readOnly = true
    }
}

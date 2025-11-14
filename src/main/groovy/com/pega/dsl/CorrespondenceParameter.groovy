package com.pega.dsl

class CorrespondenceParameter {
    String name
    String prompt
    String defaultValue
    String type = 'Text'

    def text() {
        this.type = 'Text'
    }

    def date() {
        this.type = 'Date'
    }

    def integer() {
        this.type = 'Integer'
    }

    def decimal() {
        this.type = 'Decimal'
    }
}


package com.pega.dsl

class FlowConnector {
    String from
    String to
    String condition
    String label

    def label(String label) {
        this.label = label
    }

    def when(String condition) {
        this.condition = condition
    }
}

package com.pega.dsl

class HarnessElement {
    String type
    String content
    Map<String, String> parameters = [:]

    def parameter(String name, String value) {
        parameters[name] = value
    }
}


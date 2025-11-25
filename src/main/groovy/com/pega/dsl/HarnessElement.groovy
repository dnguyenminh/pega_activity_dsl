package com.pega.dsl

class HarnessElement {
    String type
    String content
    Map<String, String> parameters = [:]

    def parameter(String name, String value) {
        parameters[name] = value
    }

    def propertyMissing(String name, value) {
        parameters[name] = value.toString()
    }

    def propertyMissing(String name) {
        parameters[name]
    }

    def methodMissing(String name, args) {
        if (args != null && args.length == 1) {
            parameters[name] = args[0].toString()
        } else {
            throw new MissingMethodException(name, this.class, args)
        }
    }
}
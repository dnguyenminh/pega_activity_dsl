package com.pega.dsl

abstract class FlowShape {
    String name
    String type
    Map<String, Object> properties = [:]

    def property(String key, Object value) {
        properties[key] = value
    }

    // Alias to avoid conflict with static property(String, Closure)
    def setShapeProperty(String key, Object value) {
        property(key, value)
    }
}

package com.pega.dsl

class DataTransformAction {
    String type
    String target
    String source
    String value
    String condition
    List<DataTransformAction> children = []
}

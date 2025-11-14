package com.pega.dsl

import groovy.transform.TupleConstructor

/**
 * Extracted DataTransform implementation (moved out of the large PegaDeveloperUtilitiesDsl file)
 */
class DataTransform extends Rule {
    List<DataTransformAction> actions = []

    DataTransform() {
        this.type = 'DataTransform'
    }
}
package com.pega.dsl

class WhenConditionClause {
    String property
    String operator
    String value
    String connector = 'AND'

    def setConnector(String connector) {
        this.connector = connector
    }
}


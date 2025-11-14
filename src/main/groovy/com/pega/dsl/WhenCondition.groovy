package com.pega.dsl

class WhenCondition extends Rule {
    List<WhenConditionClause> conditions = []

    WhenCondition() {
        this.type = 'When'
    }

    def condition(String property, String operator, String value, Closure closure = null) {
        def clause = new WhenConditionClause(
            property: property,
            operator: operator,
            value: value
        )
        if (closure) {
            closure.delegate = clause
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        conditions.add(clause)
        return clause
    }

    def and(String property, String operator, String value) {
        condition(property, operator, value) {
            connector = 'AND'
        }
    }

    def and(String condition) {
        // Handle single condition string like 'EXISTS(.Attachments.DocumentID)'
        def clause = new WhenConditionClause(
            property: condition,
            operator: '',
            value: '',
            connector: 'AND'
        )
        conditions.add(clause)
        return clause
    }

    def or(String property, String operator, String value) {
        condition(property, operator, value) {
            connector = 'OR'
        }
    }
}


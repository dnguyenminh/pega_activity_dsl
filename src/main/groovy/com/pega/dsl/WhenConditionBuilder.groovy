package com.pega.dsl

class WhenConditionBuilder {
    private final WhenCondition whenCondition

    WhenConditionBuilder(WhenCondition whenCondition) {
        this.whenCondition = whenCondition
    }

    def description(String description) {
        whenCondition.description = description
        this
    }

    def className(String className) {
        whenCondition.className = className
        this
    }

    def setStatus(String status) {
        whenCondition.status = status
        this
    }

    def setAvailable(boolean available) {
        whenCondition.isAvailable = available
        this
    }

    def setWhenProperty(String key, Object value) {
        if (!whenCondition.properties) whenCondition.properties = [:]
        whenCondition.properties[key] = value
        this
    }

    def condition(String property, String operator, String value) {
        def clause = new WhenConditionClause()
        clause.property = property
        clause.operator = operator
        clause.value = value
        whenCondition.conditions.add(clause)
        this
    }

    def and(String condition) {
        def clause = new WhenConditionClause(connector: 'AND')
        def parts = condition.split(' ', 3)
        if (parts.length == 3) {
            clause.property = parts[0]
            clause.operator = parts[1]
            clause.value = parts[2]
        } else {
            clause.property = condition
        }
        whenCondition.conditions.add(clause)
        this
    }

    def and(String property, String operator, String value) {
        def clause = new WhenConditionClause(connector: 'AND')
        clause.property = property
        clause.operator = operator
        clause.value = value
        whenCondition.conditions.add(clause)
        this
    }

    def or(String condition) {
        def clause = new WhenConditionClause(connector: 'OR')
        def parts = condition.split(' ', 3)
        if (parts.length == 3) {
            clause.property = parts[0]
            clause.operator = parts[1]
            clause.value = parts[2]
        } else {
            clause.property = condition
        }
        whenCondition.conditions.add(clause)
        this
    }

    def or(String property, String operator, String value) {
        def clause = new WhenConditionClause(connector: 'OR')
        clause.property = property
        clause.operator = operator
        clause.value = value
        whenCondition.conditions.add(clause)
        this
    }
}

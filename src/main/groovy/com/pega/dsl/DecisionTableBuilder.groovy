package com.pega.dsl

class DecisionTableBuilder {
    private final DecisionTable table

    DecisionTableBuilder(DecisionTable table) {
        this.table = table
    }

    def description(String description) {
        table.description = description
        this
    }

    def className(String className) {
        table.className = className
        this
    }

    def setStatus(String status) {
        table.status = status
        this
    }

    def setAvailable(boolean available) {
        table.isAvailable = available
        this
    }

    // Alias to avoid conflict with static property(String, Closure)
    def setTableProperty(String key, Object value) {
        if (!table.properties) table.properties = [:]
        table.properties[key] = value
        this
    }
    
    // Single-item helpers to mirror DecisionTable API and make DSL calls like:
    // decisionTable('Name') { condition '.Priority' ; result '.Route' }
    def condition(String property, String description = '') {
        table.condition(property, description)
        this
    }
    
    def conditions(List<String> conditions) {
        table.conditions.addAll(conditions)
        this
    }
    
    def result(String property, String description = '') {
        table.result(property, description)
        this
    }
    
    def results(List<String> results) {
        table.results.addAll(results)
        this
    }

    def row(Map<String, String> values) {
        table.rows.add(values)
        this
    }
    
    def otherwise(Map<String, String> values) {
        // 'otherwise' is just a special row
        def otherwiseRow = [:]
        values.each { key, value ->
            otherwiseRow[key] = value
        }
        otherwiseRow['__otherwise__'] = 'true'
        table.rows.add(otherwiseRow)
        this
    }
}

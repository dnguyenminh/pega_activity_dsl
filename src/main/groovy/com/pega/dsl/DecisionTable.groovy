package com.pega.dsl

class DecisionTable extends Rule {
    List<String> conditions = []
    List<String> results = []
    List<Map<String, String>> rows = []

    DecisionTable() {
        this.type = 'DecisionTable'
    }

    def condition(String property, String description = '') {
        conditions.add(property)
    }

    def result(String property, String description = '') {
        results.add(property)
    }

    def row(Map<String, String> values) {
        rows.add(values)
    }
}


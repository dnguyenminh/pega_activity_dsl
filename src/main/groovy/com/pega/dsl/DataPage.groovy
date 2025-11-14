package com.pega.dsl

class DataPage extends Rule {
    String dataSource
    String sourceType = 'Activity'
    Map<String, String> sourceParameters = [:]
    String refreshStrategy = 'Reload once per interaction'
    String scope = 'Requestor'
    int maxAge = 3600
    String className

    DataPage() {
        this.type = 'DataPage'
    }

    def source(String type, String name, Map params = [:]) {
        this.sourceType = type
        this.dataSource = name
        this.sourceParameters = params
        return this
    }

    def activity(String activityName, Map params = [:]) {
        source('Activity', activityName, params)
        return this
    }

    def sourceActivity(String activityName, Map params = [:]) {
        // This is an alias for the 'activity' method to avoid resolution conflicts
        // with the static 'activity(String, Closure)' method in tests.
        activity(activityName, params)
        return this
    }

    def connector(String connectorName, Map<String, String> params = [:]) {
        source('Connector', connectorName, params)
        return this
    }

    def reportDefinition(String reportName, Map params = [:]) {
        source('Report Definition', reportName, params)
        return this
    }

    def refresh(String strategy) {
        this.refreshStrategy = strategy
        return this
    }

    def scope(String scope) {
        this.scope = scope
        return this
    }

    def maxAge(int seconds) {
        this.maxAge = seconds
        return this
    }

    // Override parent property method to handle dual parameters
    def property(String key, String value) {
        properties[key] = value
        return this
    }
}

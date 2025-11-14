package com.pega.dsl

class DataPageBuilder {
    private final DataPage dataPage

    DataPageBuilder(DataPage dataPage) {
        this.dataPage = dataPage
    }

    def description(String description) {
        dataPage.description = description
        return this
    }

    def className(String className) {
        dataPage.className = className
        return this
    }

    def setStatus(String status) {
        dataPage.status = status
        return this
    }

    def setAvailable(boolean available) {
        dataPage.isAvailable = available
        return this
    }

    def source(String type, String name, Map params = [:]) {
        dataPage.source(type, name, params)
        return this
    }

    def activity(String activityName, Map params = [:]) {
        dataPage.source('Activity', activityName, params)
        return this
    }

    // Alias to avoid conflict with static activity(String, Closure)
    def sourceActivity(String activityName, Map params = [:]) {
        activity(activityName, params)
        return this
    }

    def connector(String connectorName, Map<String, String> params = [:]) {
        dataPage.source('Connector', connectorName, params)
        return this
    }

    def reportDefinition(String reportName, Map params = [:]) {
        dataPage.source('Report Definition', reportName, params)
        return this
    }

    def refresh(String strategy) {
        dataPage.refresh(strategy)
        return this
    }

    def scope(String scope) {
        dataPage.scope(scope)
        return this
    }

    def maxAge(int seconds) {
        dataPage.maxAge(seconds)
        return this
    }

    def property(String key, String value) {
        dataPage.property(key, value)
        return this
    }

    // Alias to avoid conflict with static property(String, Closure)
    def setDataProperty(String key, String value) {
        property(key, value)
        return this
    }
}

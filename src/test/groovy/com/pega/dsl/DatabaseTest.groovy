package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class DatabaseTest extends Specification {

    def "should create database with url and driver"() {
        when:
        def db = database('CustomerDB') {
            description 'Primary customer database'
            url 'jdbc:postgresql://localhost:5432/customers'
            driver 'org.postgresql.Driver'
        }

        then:
        db.name == 'CustomerDB'
        db.description == 'Primary customer database'
        db.url == 'jdbc:postgresql://localhost:5432/customers'
        db.driver == 'org.postgresql.Driver'
    }

    def "should set credentials"() {
        when:
        def db = database('BillingDB') {
            credentials 'dbuser', 'dbpassword'
        }

        then:
        db.username == 'dbuser'
        db.password == 'dbpassword'
    }

    def "should set additional properties"() {
        when:
        def db = database('AnalyticsDB') {
            setDatabaseProperty 'maxConnections', '100'
            setDatabaseProperty 'readOnly', 'true'
        }

        then:
        db.properties.size() == 2
        db.properties['maxConnections'] == '100'
        db.properties['readOnly'] == 'true'
    }

    def "should create a comprehensive database configuration"() {
        when:
        def db = database('WarehouseDB') {
            description 'Data warehouse connection'
            url 'jdbc:sqlserver://server.name:1433;databaseName=Warehouse'
            driver 'com.microsoft.sqlserver.jdbc.SQLServerDriver'
            credentials 'reportuser', 'complex_password_123'
            setDatabaseProperty 'loginTimeout', '30'
            setDatabaseProperty 'socketTimeout', '60'
        }

        then:
        db.name == 'WarehouseDB'
        db.url.contains('databaseName=Warehouse')
        db.driver == 'com.microsoft.sqlserver.jdbc.SQLServerDriver'
        db.username == 'reportuser'
        db.password == 'complex_password_123'
        db.properties['loginTimeout'] == '30'
    }
}

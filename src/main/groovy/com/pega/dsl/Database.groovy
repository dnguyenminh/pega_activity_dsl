package com.pega.dsl

class Database extends Rule {
    String name
    String url
    String driver
    String username
    String password
    Map<String, String> properties = [:]
    Database() { this.type = 'Database' }
    def url(String u) { this.url = u }
    def driver(String d) { this.driver = d }
    def credentials(String user, String pass) { this.username = user; this.password = pass }
    
    // Renamed to avoid conflict with static property(String, Closure)
    def setDatabaseProperty(String key, String value) {
        properties[key] = value
    }
}

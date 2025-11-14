package com.pega.dsl

class AccessRole extends Rule {
    Map<String, Integer> permissions = [:]

    AccessRole() { this.type = 'AccessRole' }
    AccessRole(String name) { this.name = name; this.type = 'AccessRole' }

    def grant(String permissionName, int level) {
        permissions[permissionName] = level
    }

    def deny(String permissionName) {
        permissions[permissionName] = 0
    }
}

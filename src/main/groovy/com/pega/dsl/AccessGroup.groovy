package com.pega.dsl

class AccessGroup extends Rule {
    List<String> roles = []
    List<String> portals = []
    List<String> workPools = []
    AccessGroup() { this.type = 'AccessGroup' }
    AccessGroup(String name) { this.name = name; this.type = 'AccessGroup' }

    def role(String roleName) { if (roleName) roles.add(roleName); return this }
    def portal(String p) { if (p) portals.add(p); return this }
    def workPool(String w) { if (w) workPools.add(w); return this }
}


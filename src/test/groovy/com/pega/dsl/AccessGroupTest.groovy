package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class AccessGroupTest extends Specification {

    def "should create access group with basic properties"() {
        when:
        def group = accessGroup('Managers') {
            description 'Access group for managers'
        }

        then:
        group.name == 'Managers'
        group.description == 'Access group for managers'
        group.type == 'AccessGroup'
    }

    def "should add a single role, portal, and work pool"() {
        when:
        def group = accessGroup('Developers') {
            role 'DeveloperRole'
            portal 'DeveloperPortal'
            workPool 'DevelopmentPool'
        }

        then:
        group.roles.size() == 1
        group.roles[0] == 'DeveloperRole'
        group.portals.size() == 1
        group.portals[0] == 'DeveloperPortal'
        group.workPools.size() == 1
        group.workPools[0] == 'DevelopmentPool'
    }

    def "should add multiple roles"() {
        when:
        def group = accessGroup('Admins') {
            role 'AdminRole'
            role 'UserRole'
            role 'AuditorRole'
        }

        then:
        group.roles.size() == 3
        group.roles.contains('AdminRole')
        group.roles.contains('UserRole')
        group.roles.contains('AuditorRole')
    }

    def "should create a comprehensive access group"() {
        when:
        def group = accessGroup('CustomerServiceReps') {
            description 'Main access group for CSRs'
            portal 'CSR_Portal'
            portal 'Mobile_Portal'
            workPool 'Service'
            workPool 'General'
            role 'CSR'
            role 'SelfServiceUser'
        }

        then:
        group.name == 'CustomerServiceReps'
        group.portals.size() == 2
        group.workPools.size() == 2
        group.roles.size() == 2
        group.portals.contains('Mobile_Portal')
        group.workPools.contains('Service')
        group.roles.contains('CSR')
    }

    def "should handle empty access group definition"() {
        when:
        def group = accessGroup('EmptyGroup') {
            // No configuration
        }

        then:
        group.name == 'EmptyGroup'
        group.roles.isEmpty()
        group.portals.isEmpty()
        group.workPools.isEmpty()
    }
}

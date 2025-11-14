package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class AccessRoleTest extends Specification {

    def "should create access role with basic properties"() {
        when:
        def role = accessRole('Manager') {
            description 'Role for line managers'
        }

        then:
        role.name == 'Manager'
        role.description == 'Role for line managers'
        role.type == 'AccessRole'
    }

    def "should grant permissions with levels"() {
        when:
        def role = accessRole('Editor') {
            grant 'Read', 5
            grant 'Write', 5
            grant 'Delete', 2
        }

        then:
        role.permissions.size() == 3
        role.permissions['Read'] == 5
        role.permissions['Write'] == 5
        role.permissions['Delete'] == 2
    }

    def "should deny permissions"() {
        when:
        def role = accessRole('Guest') {
            grant 'Read', 1
            deny 'Write'
            deny 'Delete'
        }

        then:
        role.permissions.size() == 3
        role.permissions['Read'] == 1
        role.permissions['Write'] == 0
        role.permissions['Delete'] == 0
    }

    def "should create a comprehensive access role"() {
        when:
        def role = accessRole('Administrator') {
            description 'Full access administrator role'
            grant 'All', 5
            deny 'BypassSecurity'
        }

        then:
        role.name == 'Administrator'
        role.description == 'Full access administrator role'
        role.permissions['All'] == 5
        role.permissions['BypassSecurity'] == 0
    }
    def "should handle grant with null and empty permission name"() {
        when:
        def role = accessRole('EdgeCase') {
            grant null, 3
            grant '', 2
        }

        then:
        role.permissions.containsKey(null)
        role.permissions[null] == 3
        role.permissions.containsKey('')
        role.permissions[''] == 2
    }
}

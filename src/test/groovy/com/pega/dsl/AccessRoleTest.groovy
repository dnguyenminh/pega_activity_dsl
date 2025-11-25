package com.pega.dsl

import spock.lang.Specification

class AccessRoleTest extends Specification {

    def "should create access role with a name"() {
        when:
        def role = new AccessRole('Admin')

        then:
        role.name == 'Admin'
        role.type == 'AccessRole'
    }

    def "should grant a permission"() {
        given:
        def role = new AccessRole('Editor')

        when:
        role.grant('edit-article', 5)

        then:
        role.permissions.size() == 1
        role.permissions['edit-article'] == 5
    }

    def "should deny a permission"() {
        given:
        def role = new AccessRole('Viewer')

        when:
        role.deny('edit-article')

        then:
        role.permissions.size() == 1
        role.permissions['edit-article'] == 0
    }

    def "should deny a previously granted permission"() {
        given:
        def role = new AccessRole('Contributor')
        role.grant('edit-article', 3)

        when:
        role.deny('edit-article')

        then:
        role.permissions['edit-article'] == 0
    }

    def "should create access role with no-arg constructor"() {
        when:
        def role = new AccessRole()

        then:
        role.name == null
        role.type == 'AccessRole'
        role.permissions.isEmpty()
    }
}
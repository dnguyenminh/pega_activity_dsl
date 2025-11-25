package com.pega.dsl

import spock.lang.Specification

class AccessGroupSpec extends Specification {

    def "add roles portals and workPools"() {
        given:
        def ag = new AccessGroup('ag1')

        when:
        ag.role('r1')
        ag.portal('p1')
        ag.workPool('w1')

        then:
        ag.name == 'ag1'
        ag.roles == ['r1']
        ag.portals == ['p1']
        ag.workPools == ['w1']
    }

    def "null or empty inputs not added"() {
        given:
        def ag = new AccessGroup()

        when:
        ag.role(null)
        ag.portal('')
        ag.workPool(null)

        then:
        ag.roles.isEmpty()
        ag.portals.isEmpty()
        ag.workPools.isEmpty()
    }
}

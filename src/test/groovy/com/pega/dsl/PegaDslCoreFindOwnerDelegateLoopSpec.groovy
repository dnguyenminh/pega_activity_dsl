package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreFindOwnerDelegateLoopSpec extends Specification {

    def "findOwnerDelegateOfType finds delegate deeper in closure owner chain"() {
        given:
        // deeper delegate we expect to find
        def deepMap = [found: true]

        // owner2 has delegate deepMap
    def owner2 = { -> 'owner2' }
    // rehydrate(delegate, owner, thisObject) - set delegate to deepMap and owner self
    owner2 = owner2.rehydrate(deepMap, owner2, owner2)

    // owner1's owner is owner2, owner1.delegate = null
    def owner1 = { -> 'owner1' }
    owner1 = owner1.rehydrate(null, owner2, owner1)

    // closure whose immediate owner is owner1
    def c = { -> 'c' }
    c = c.rehydrate(null, owner1, c)

        when:
        println "owner1 -> ${owner1}, delegate=${owner1.delegate}, owner=${owner1.owner}"
        println "owner2 -> ${owner2}, delegate=${owner2.delegate}, owner=${owner2.owner}"
        println "c.owner -> ${c.owner}, c.delegate -> ${c.delegate}"
        def found = PegaDslCore.findOwnerDelegateOfType(c, Map)

        then:
        // diagnostic + assertion: prefer instanceof check and equality when available
        println "found => ${found}"
        found instanceof Map
        found == deepMap
    }
}

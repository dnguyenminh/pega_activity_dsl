package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreCallWithDelegateSpec extends Specification {

    static class Greeter {
        String who() { 'greeter' }
    }

    def "callWithDelegate returns delegate when closure is null"() {
        given:
        def d = new Greeter()

        expect:
        PegaDslCore.callWithDelegate(d, null) == d
    }

    def "callWithDelegate rehydrates closure so delegate methods are callable"() {
        given:
        def d = new Greeter()
        def c = { -> who() }

        when:
        def res = PegaDslCore.callWithDelegate(d, c)

        then:
        res == 'greeter'
    }

    def "callWithDelegate handles null closure without throwing"() {
        when:
        PegaDslCore.callWithDelegate(new Object(), null, 0)

        then:
        noExceptionThrown()
    }

}

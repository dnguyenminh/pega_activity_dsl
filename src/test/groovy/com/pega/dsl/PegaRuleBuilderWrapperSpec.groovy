package com.pega.dsl

import spock.lang.Specification

class PegaRuleBuilderWrapperSpec extends Specification {

    def "wrapper methods delegate to PegaDslCore"() {
        given:
        def b = new PegaRuleBuilder()

        expect:
        b.normalizeCandidate(' a.b ') == 'a.b'
        b.findOwnerDelegateOfType(null, String) == null
    }

    def "withPrevDelegate restores previous delegate when present"() {
        given:
        def b = new PegaRuleBuilder()
        // set a previous delegate so withPrevDelegate should restore it
        b.CURRENT_DELEGATE.set('PREV')

        when:
        def rc = b.restConnector('R1', { -> delegate.name = 'n' })

        then:
        // previous delegate should be restored
        b.CURRENT_DELEGATE.get() == 'PREV'
        rc != null
    }

    def "withPrevDelegate removes previous delegate when absent"() {
        given:
        def b = new PegaRuleBuilder()
        // ensure no previous delegate
        b.CURRENT_DELEGATE.remove()

        when:
        def rc = b.restConnector('R2', { -> delegate.name = 'n' })

        then:
        // no previous delegate should remain
        b.CURRENT_DELEGATE.get() == null
        rc != null
    }
}

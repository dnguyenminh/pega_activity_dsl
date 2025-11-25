package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderExtraBranchCoverageTest extends Specification {

    def "varargs null array returns builder for forwardable methods"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        expect:
        b.connectREST((Object[])null).is(b)
        b.connectSOAP((Object[])null).is(b)
        b.loadDataPage((Object[])null).is(b)
        b.queue((Object[])null).is(b)
        a.steps.size() == 0
    }

    def "no-arg varargs returns builder for forwardable methods"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        expect:
        b.connectREST().is(b)
        b.connectSOAP().is(b)
        b.loadDataPage().is(b)
        b.queue().is(b)
        a.steps.size() == 0
    }

    def "queueVarargs with explicit null second arg uses empty map and marker not set"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        when:
        b.queueVarargs('QNull', null)
        then:
        a.steps.find{ it.method=='Queue' && it.parameters['Activity']=='QNull' } != null
        b.__test_marker_269__ == false
    }

    def "step without closure creates step and does not attempt rehydration"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        when:
        b.step('OnlyStep')
        then:
        a.steps.find{ it.method=='OnlyStep' } != null
    }

}
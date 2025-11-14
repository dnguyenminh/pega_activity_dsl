package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderVarargsExplicitTest extends Specification {

    def "explicit varargs calls for REST/SOAP/DataPage"() {
        given:
        def activity = new com.pega.dsl.Activity()
        def b = new com.pega.dsl.ActivityBuilder(activity)

        when:
        b.connectREST((Object[]) [] )
        b.connectREST((Object[]) ['r1'] as Object[])
        b.connectREST((Object[]) ['r2', [timeout:7]] as Object[])

        b.connectSOAP((Object[]) [] )
        b.connectSOAP((Object[]) ['s1'] as Object[])
        b.connectSOAP((Object[]) ['s2', [retries:2]] as Object[])

        b.loadDataPage((Object[]) [] )
        b.loadDataPage((Object[]) ['dp1'] as Object[])
        b.loadDataPage((Object[]) ['dp2', [p:'v']] as Object[])

        then:
        activity.steps.count{ it.method=='Connect-REST' } == 2
        activity.steps.find{ it.method=='Connect-REST' && it.parameters['connector']=='r1' } != null
        activity.steps.find{ it.method=='Connect-REST' && it.parameters['connector']=='r2' }.parameters['timeout'] == 7

        activity.steps.count{ it.method=='Connect-SOAP' } == 2
        activity.steps.find{ it.method=='Connect-SOAP' && it.parameters['connector']=='s1' } != null
        activity.steps.find{ it.method=='Connect-SOAP' && it.parameters['connector']=='s2' }.parameters['retries'] == 2

        activity.steps.count{ it.method=='Load-DataPage' } == 2
        activity.steps.find{ it.method=='Load-DataPage' && it.parameters['DataPageName']=='dp1' } != null
        activity.steps.find{ it.method=='Load-DataPage' && it.parameters['DataPageName']=='dp2' }.parameters['p'] == 'v'
    }

    def "explicit varargs queue behavior"() {
        given:
        def activity = new com.pega.dsl.Activity()
        def b = new com.pega.dsl.ActivityBuilder(activity)

        when:
        b.queue((Object[]) [])
        b.queue((Object[]) ['q1'] as Object[])
        b.queue((Object[]) ['q2', [x:5]] as Object[])

        then:
        activity.steps.count{ it.method=='Queue' } == 2
        activity.steps.find{ it.method=='Queue' && it.parameters['Activity']=='q1' } != null
        activity.steps.find{ it.method=='Queue' && it.parameters['Activity']=='q2' }.parameters['x'] == 5
    }

}
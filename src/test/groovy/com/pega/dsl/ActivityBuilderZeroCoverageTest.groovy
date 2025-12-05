package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderZeroCoverageTest extends Specification {

    def "exercise ActivityBuilder public methods and branches"() {
        given:
        def activity = new Activity(name: 'TestActivity')
        def builder = new ActivityBuilder(activity)

        when:
        builder.callActivity('OtherActivity', [param1: 'v', closureParam: { -> delegate.parameters['closureSet'] = 'yes' }])
        builder.addCallStep('Other2', [:])
        builder.description('desc')
        builder.description('desc2', [a:1])
        builder.localVariable('var','String')
        builder.propertySet('prop','val')
        builder.propertySet('prop2', [Param: 'v'])
        builder.propertySet([Key:'V'])
        builder.pageNew('Page','Class')
        builder.objOpen('obj','READ')
        builder.objSave()
        builder.addComment('c')
        builder.propertyRemove('p')
        builder.waitSeconds(5)
        builder.connectREST('conn', [p:1])
        builder.connectREST('onlyString')
        builder.connectSOAP('soap', [p:2])
        builder.applyDataTransform('dt','s','t')
        builder.loadDataPage('dp', [p:3])
        builder.loadDataPage('dpOnly')
        builder.showPage('page','PDF')
        builder.branch('act','cond')
        builder.logMessage('msg','WARN')
        builder.queueVarargs('q', [map:1])
        def marker = builder.__test_marker_269__
        builder.queue('q2', [a:2])
        builder.commit()
        builder.rollback()
        builder.step('Custom') { -> delegate.parameters['inside'] = 'ok' }
        builder.setStatus('Active')
        builder.setAvailable(false)

        then:
        notThrown(Exception)
        activity.steps.size() >= 1
        marker == true
        activity.status == 'Active'
        activity.isAvailable == false
        // Verify that a step created by the closure contains our parameter
        activity.steps.find { it.method == 'Custom' }?.parameters['inside'] == 'ok'
    }

    def "queueVarargs and queue shapes exercise parseStringAndMapArgs results"() {
        given:
        def activity = new Activity(name: 'QActivity')
        def b = new ActivityBuilder(activity)

        when:
        // vararg with string only
        b.queueVarargs('onlyName')
        // vararg with string + map -> should set marker
        b.queueVarargs('withMap', [k: 'v'])
        def markerSet = b.__test_marker_269__

        // connectREST with vararg string should add a step
        b.connectREST('restName')
        b.connectSOAP('soapName', null)
        b.loadDataPage('dpName', null)

        then:
        notThrown(Exception)
        activity.steps.size() >= 1
        markerSet == true
    }
}

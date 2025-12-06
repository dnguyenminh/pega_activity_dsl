package com.pega.dsl

import spock.lang.Specification

class FlowAdditionalCoverageSpec extends Specification {

    def cleanup() {
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
    }

    def "flow type helpers toggle modes"() {
        given:
        def flow = new Flow('ToggleFlow')

        when:
        flow.screen()

        then:
        flow.flowType == 'Screen'

        when:
        flow.subFlow()

        then:
        flow.flowType == 'SubFlow'

        when:
        flow.work()

        then:
        flow.flowType == 'Work'
    }

    def "assignment decision and end without closures leave delegate untouched"() {
        given:
        def flow = new Flow('NoClosures')
        def marker = new Object()
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(marker)

        when:
        def assignmentShape = flow.assignment('AssignOnly')
        def decisionShape = flow.decision('DecideOnly')
        def endShape = flow.end('EndOnly')

        then:
        assignmentShape instanceof AssignmentShape
        decisionShape instanceof DecisionShape
        endShape instanceof EndShape
        flow.shapes*.name.containsAll(['AssignOnly', 'DecideOnly', 'EndOnly'])
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get() is marker
    }

    def "start without closure uses defaults and does not alter delegate"() {
        given:
        def flow = new Flow('StartDefaults')
        def marker = new Object()
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(marker)

        when:
        def startShape = flow.start()

        then:
        startShape.name == 'Start'
        flow.shapes.last().is(startShape)
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get() is marker
    }

    def "utility without closure records activity and leaves delegate alone"() {
        given:
        def flow = new Flow('UtilityDefaults')
        def marker = new Object()
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(marker)

        when:
        def utilShape = flow.utility('UtilOnly', 'MyActivity')

        then:
        utilShape instanceof UtilityShape
        utilShape.activity == 'MyActivity'
        PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get() is marker
    }

    def "connect stores connectors including optional condition"() {
        given:
        def flow = new Flow('ConnectorFlow')

        when:
        flow.connect('Start', 'Assign')
        flow.connect('Assign', 'End', 'WhenDone')

        then:
        flow.connectors.size() == 2
        flow.connectors[0].condition == ''
        flow.connectors[1].condition == 'WhenDone'
        flow.connectors*.from == ['Start', 'Assign']
        flow.connectors*.to == ['Assign', 'End']
    }
}

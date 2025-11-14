package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class FlowTest extends Specification {

    def "should create flow with different types"() {
        when:
        def workFlow = flow('WorkFlow') { work() }
        def screenFlow = flow('ScreenFlow') { screen() }
        def subFlow = flow('SubFlow') { subFlow() }

        then:
        workFlow.flowType == 'Work'
        screenFlow.flowType == 'Screen'
        subFlow.flowType == 'SubFlow'
    }

    def "should create flow with all shape types"() {
        when:
        def flow = flow('AllShapes') {
            start('Begin')
            assignment('Assign1')
            decision('Decision1')
            utility('Utility1', 'Activity1')
            connector('Connector1', 'Conn1')
            subProcess('SubProcess1', 'Flow1')
            end('End1')
        }

        then:
        flow.shapes.size() == 7
        flow.shapes.collect { it.type } == ['Start', 'Assignment', 'Decision', 'Utility', 'Connector', 'SubProcess', 'End']
    }

    def "should configure assignment routing"() {
        when:
        def flow = flow('RoutingFlow') {
            assignment('ToWorklist') { worklist() }
            assignment('ToWorkbasket') { workbasket('WB1') }
            assignment('ToOperator') { operator('user@pega.com') }
            assignment('ToRouteTo') { routeTo('Manager') }
        }

        then:
        def assignments = flow.shapes.findAll { it.type == 'Assignment' }
        assignments.size() == 4
        assignments[0].routeTo == 'worklist'
        assignments[1].routeTo == 'WB1'
        assignments[2].routeTo == 'user@pega.com'
        assignments[3].routeTo == 'Manager'
    }

    def "should configure decision shape types"() {
        when:
        def flow = flow('DecisionTypes') {
            decision('WhenDecision') { when('WhenRule1') }
            decision('TableDecision') { decisionTable('Table1') }
            decision('TreeDecision') { decisionTree('Tree1') }
            decision('ActivityDecision') { activity('Activity1') }
        }

        then:
        def decisions = flow.shapes.findAll { it.type == 'Decision' }
        decisions.size() == 4
        decisions[0].when == 'WhenRule1'
        decisions[1].decisionTable == 'Table1'
        decisions[2].decisionTree == 'Tree1'
        decisions[3].activity == 'Activity1'
    }

    def "should configure end shape statuses"() {
        when:
        def flow = flow('EndStatuses') {
            end('Completed') { resolved() }
            end('Cancelled') { cancelled() }
            end('Withdrawn') { withdrawn() }
            end('Custom') { status('Resolved-Custom') }
        }

        then:
        def endShapes = flow.shapes.findAll { it.type == 'End' }
        endShapes.size() == 4
        endShapes[0].status == 'Resolved-Completed'
        endShapes[1].status == 'Resolved-Cancelled'
        endShapes[2].status == 'Resolved-Withdrawn'
        endShapes[3].status == 'Resolved-Custom'
    }

    def "should create connectors between shapes"() {
        when:
        def flow = flow('ConnectorFlow') {
            start('A')
            assignment('B')
            end('C')

            connect('A', 'B')
            connect('B', 'C', 'Submit')
        }

        then:
        flow.connectors.size() == 2
        flow.connectors[0].from == 'A'
        flow.connectors[0].to == 'B'
        flow.connectors[0].condition == ''
        
        flow.connectors[1].from == 'B'
        flow.connectors[1].to == 'C'
        flow.connectors[1].condition == 'Submit'
    }

    def "should set properties on shapes"() {
        when:
        def flow = flow('ShapeProperties') {
            start('Start') {
                setShapeProperty('x', 100)
                setShapeProperty('y', 50)
            }
            assignment('Task') {
                setShapeProperty('width', 200)
            }
        }

        then:
        flow.shapes[0].properties['x'] == 100
        flow.shapes[0].properties['y'] == 50
        flow.shapes[1].properties['width'] == 200
    }
}

package com.pega.dsl

import spock.lang.Specification

class AssignmentShapeTest extends Specification {
    def "should set section, harness, flowAction, routeTo, routingActivity"() {
        when:
        def shape = new AssignmentShape()
        shape.section("MainSection")
        shape.harness("MainHarness")
        shape.flowAction("Approve")
        shape.flowAction("Reject")
        shape.routeTo("OperatorA")
        shape.routingActivity("RouteActivity")
        then:
        shape.type == "Assignment"
        shape.section == "MainSection"
        shape.harness == "MainHarness"
        shape.flowActions == ["Approve", "Reject"]
        shape.routeTo == "OperatorA"
        shape.routingActivity == "RouteActivity"
    }

    def "should use worklist, workbasket, operator helpers"() {
        when:
        def shape = new AssignmentShape()
        shape.worklist()
        def shape2 = new AssignmentShape()
        shape2.workbasket("BasketA")
        def shape3 = new AssignmentShape()
        shape3.operator("OperatorB")
        then:
        shape.routeTo == "worklist"
        shape2.routeTo == "BasketA"
        shape3.routeTo == "OperatorB"
    }
}

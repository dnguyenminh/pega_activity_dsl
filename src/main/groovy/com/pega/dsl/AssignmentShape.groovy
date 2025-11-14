package com.pega.dsl

class AssignmentShape extends FlowShape {
    String section
    String harness
    List<String> flowActions = []
    String routeTo
    String routingActivity

    AssignmentShape() {
        this.type = 'Assignment'
    }

    def section(String sectionName) {
        this.section = sectionName
    }

    def harness(String harnessName) {
        this.harness = harnessName
    }

    def flowAction(String actionName) {
        flowActions.add(actionName)
    }

    def routeTo(String operator) {
        this.routeTo = operator
    }

    def routingActivity(String activity) {
        this.routingActivity = activity
    }

    def worklist() {
        routeTo('worklist')
    }

    def workbasket(String workbasketName) {
        routeTo(workbasketName)
    }

    def operator(String operatorId) {
        routeTo(operatorId)
    }
}

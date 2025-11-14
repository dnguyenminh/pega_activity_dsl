package com.pega.dsl

class DecisionShape extends FlowShape {
    String when
    String decisionTable
    String decisionTree
    String activity

    DecisionShape() {
        this.type = 'Decision'
    }

    def when(String whenRule) {
        this.when = whenRule
    }

    def decisionTable(String tableName) {
        this.decisionTable = tableName
    }

    def decisionTree(String treeName) {
        this.decisionTree = treeName
    }

    def activity(String activityName) {
        this.activity = activityName
    }
}

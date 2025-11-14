package com.pega.dsl

class Activity extends Rule {
    List<ActivityStep> steps = []
    Map<String, String> localVariables = [:]

    Activity() {
        this.type = 'Activity'
    }

    List<ActivityStep> getSteps() {
        return steps
    }
}
package com.pega.dsl

import spock.lang.Specification

class ActivitySimpleTest extends Specification {

    def "Test getSteps() on a new Activity object"() {
        given:
        def activity = new Activity()
        println "DEBUG: Activity object class: ${activity.getClass()}"
        println "DEBUG: Activity object classloader: ${activity.getClass().getClassLoader()}"

        when:
        def stepsList = activity.getSteps()

        then:
        stepsList != null
        stepsList.isEmpty()
    }
}

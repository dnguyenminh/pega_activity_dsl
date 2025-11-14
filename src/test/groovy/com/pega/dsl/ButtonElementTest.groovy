package com.pega.dsl

import spock.lang.Specification

class ButtonElementTest extends Specification {
    def "should set style using helpers"() {
        when:
        def btn = new ButtonElement()
        btn.primary()
        def btn2 = new ButtonElement()
        btn2.secondary()
        def btn3 = new ButtonElement()
        btn3.tertiary()
        def btn4 = new ButtonElement()
        btn4.simple()
        def btn5 = new ButtonElement()
        btn5.strong()
        then:
        btn.style == "Primary"
        btn2.style == "Secondary"
        btn3.style == "Tertiary"
        btn4.style == "Simple"
        btn5.style == "Strong"
    }

    def "should set visibility and condition"() {
        when:
        def btn = new ButtonElement()
        btn.visible(".IsVisible == true")
        then:
        btn.visibility == "If"
        btn.condition == ".IsVisible == true"
    }

    def "should set action using helpers"() {
        when:
        def btn = new ButtonElement()
        btn.localAction("LocalAction1")
        def btn2 = new ButtonElement()
        btn2.flowAction("FlowAction1")
        def btn3 = new ButtonElement()
        btn3.activity("Activity1")
        then:
        btn.action == "LocalAction1"
        btn2.action == "FlowAction1"
        btn3.action == "Activity1"
    }
}

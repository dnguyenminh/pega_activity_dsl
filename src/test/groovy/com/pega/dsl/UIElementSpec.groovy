package com.pega.dsl

import spock.lang.Specification

class UIElementSpec extends Specification {

    def "visible sets visibility and condition"() {
        given:
        def el = new UIElement() {}

        when:
        el.visible('user.isAdmin')

        then:
        el.visibility == 'If'
        el.condition == 'user.isAdmin'
    }

    def "required, readOnly and disabled set properties map"() {
        given:
        def el = new UIElement() {}

        when:
        el.required()
        el.readOnly()
        el.disabled()

        then:
        el.properties['required'] == true
        el.properties['readOnly'] == true
        el.properties['disabled'] == true
    }
}

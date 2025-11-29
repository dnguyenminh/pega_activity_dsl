package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class ExceptionsSpec extends Specification {

    def "InvalidParameterException should be instantiable"() {
        when:
        def ex = new InvalidParameterException()

        then:
        ex != null
        ex instanceof RuntimeException
    }

    def "WrongModeException should be instantiable"() {
        when:
        def ex = new WrongModeException()

        then:
        ex != null
        ex instanceof RuntimeException
    }
}

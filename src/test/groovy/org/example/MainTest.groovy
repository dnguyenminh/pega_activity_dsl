package org.example

import spock.lang.Specification

class MainTest extends Specification {
    def "main executes without throwing"() {
        when:
        Main.main(new String[0])

        then:
        notThrown(Exception)
    }
}

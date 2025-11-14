package com.pega.dsl

import spock.lang.Specification

class PegaDslCoreSpec extends Specification {

    def "normalizeCandidate handles HTML entities, dot/space tokens and surrounding quotes"() {
        when:
        def inputs = [
            '  %20Name%20 ',
            '__dot__Field__space__Name',
            '\$dot\$something',
            '&#65;&#66;&#67;',
            '\\u0041\u0042',
            '"QuotedValue"',
            "(Wrapped)"
        ]

        then:
        def outputs = inputs.collect { PegaDslCore.normalizeCandidate(it) }
        outputs[0] == 'Name'
        outputs[1] == '.Field Name'
        // $dot token becomes '.' and leftover text preserved
        outputs[2].contains('.')
        outputs[3] == 'ABC'
        // one of the unicode escapes should become AB
        outputs[4].contains('AB')
        outputs[5] == 'QuotedValue'
        outputs[6] == 'Wrapped'
    }
}


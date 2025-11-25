package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class FlowCoverageTest extends Specification {

    def "exercise finally branch when previous delegate exists"() {
    given: "a non-null current delegate so the finally branch sets it back"
    PegaDslCore.CURRENT_DELEGATE.set(new Object())

        when: "we create a flow and call several shape builders with closures"
        def f = flow('CoverageFlow') {
            start('S1') { setShapeProperty('dummy', 1) }
            assignment('A1') { worklist() }
            decision('D1') { when('WhenRule') }
            utility('U1', 'Act1') { /* configure utility */ }
            connector('C1', 'Conn1') { /* connector closure */ }
            subProcess('SP1', 'SomeFlow') { /* subProcess closure */ }
            end('E1') { resolved() }
        }

        then: "flow built and shapes present"
        f.shapes.size() >= 7

    cleanup:
    // restore thread-local
    PegaDslCore.CURRENT_DELEGATE.remove()
    }
}

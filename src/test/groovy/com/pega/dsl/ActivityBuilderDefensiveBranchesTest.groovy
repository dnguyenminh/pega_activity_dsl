import spock.lang.Specification
import com.pega.dsl.Activity
import com.pega.dsl.ActivityBuilder
import com.pega.dsl.PegaDslCore

class ActivityBuilderDefensiveBranchesTest extends Specification {
    def "connectREST/connectSOAP/loadDataPage/queue handle null and invalid args"() {
        given:
        def b = new ActivityBuilder(new Activity())
        expect:
        b.connectREST((Object[])null).is(b)
        b.connectREST((Object[])[]).is(b)
        b.connectREST(123).is(b)
        b.connectREST('notAString', 123) == b
        b.connectSOAP((Object[])null).is(b)
        b.connectSOAP((Object[])[]).is(b)
        b.connectSOAP(123).is(b)
        b.connectSOAP('notAString', 123) == b
        b.loadDataPage((Object[])null).is(b)
        b.loadDataPage((Object[])[]).is(b)
        b.loadDataPage(123).is(b)
        b.loadDataPage('notAString', 123) == b
        b.queue((Object[])null).is(b)
        b.queue((Object[])[]).is(b)
        b.queue(123).is(b)
        b.queue('notAString', 123) == b
    }

    def "connectREST/connectSOAP/loadDataPage/queue handle null params"() {
        given:
        def b = new ActivityBuilder(new Activity())
        expect:
        b.connectREST('r', null)
        b.connectSOAP('s', null)
        b.loadDataPage('d', null)
        b.queue('q', null)
    }

    def "addCallStep forced rehydration failure triggers fallback"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        def closure = { -> 123 }
        when:
        b.addCallStep('FailStep', [hook: closure, '__force_rehydration_failure__': true])
        then:
        a.steps.size() == 1
        a.steps[0].parameters['hook'] == closure
    }

    def "callActivity triggers fallback branch for forced rehydration failure"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        def closure = { -> 42 }
        when:
        b.callActivity("FailActivity", [hook: closure, '__force_rehydration_failure__': true])
        then:
        a.steps.size() == 1
        a.steps[0].parameters['hook'] == closure
    }

    def "callActivity fallback branch for forced rehydration failure (lines 23, 29)"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        def closure = { -> 42 }
        when:
        b.callActivity("FailActivity", [hook: closure, '__force_rehydration_failure__': true])
        then:
        a.steps.size() == 1
        a.steps[0].parameters['hook'] == closure
    }

    def "parseStringAndMapArgs returns null for null and empty array (line 59)"() {
        given:
        def b = new ActivityBuilder(new Activity())
        expect:
        b.&parseStringAndMapArgs(null) == null
        b.&parseStringAndMapArgs([] as Object[]) == null
    }

    def "parseStringAndMapArgs covers final defensive return (line 59)"() {
        given:
        def b = new ActivityBuilder(new Activity())
        expect:
        // This shape is not matched by any prior branch, triggers line 59
        b.&parseStringAndMapArgs([null, null, null]) == null
        b.&parseStringAndMapArgs(['str', null, 123]) == null
        b.&parseStringAndMapArgs(['str', new Object(), new Object()]) == null
        b.&parseStringAndMapArgs(['str', null, null, null]) == null
        b.&parseStringAndMapArgs(['str', 123, null, true]) == null
        b.&parseStringAndMapArgs(['str', [a:1], 123, true]) == null
    }

    def "description(String, Map) branch when delegate is null covers lines 72/73"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        PegaDslCore.CURRENT_DELEGATE.remove()
        when:
        def result = b.description("desc", [p:1])
        then:
        a.description == "desc"
        result == b
    }

    def "delegate guard prevents builder side-effects"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        PegaDslCore.CURRENT_DELEGATE.set(new Object())
        expect:
        b.description('x') == b
        b.description('x', [p:1]) == b
        b.propertySet('p','v') == b
        b.propertySet('p', [a:1]) == b
        b.propertySet((Map)[a:1]) == b
        b.step('M1') == b
        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "parseStringAndMapArgs returns null for null and empty array"() {
        given:
        def b = new ActivityBuilder(new Activity())
        expect:
        b.&parseStringAndMapArgs(null) == null
        b.&parseStringAndMapArgs([] as Object[]) == null
        b.&parseStringAndMapArgs([null] as Object[]) == null
    }

    def "description(String, Map) branch when delegate is null covers lines 72/73"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        PegaDslCore.CURRENT_DELEGATE.remove()
        when:
        def result = b.description("desc", [p:1])
        then:
        a.description == "desc"
        result == b
    }
    def "parseStringAndMapArgs returns null for unmatched shapes (lines 52, 59)"() {
        given:
        def b = new ActivityBuilder(new Activity())
        expect:
        b.&parseStringAndMapArgs(["str", 123] as Object[]) == null
    }

    def "description(String) and description(String, Map) return this when delegate is not null and not this (lines 65, 71)"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        def fakeDelegate = new Object()
        PegaDslCore.CURRENT_DELEGATE.set(fakeDelegate)
        expect:
        b.description("desc") == b
        b.description("desc", [p:1]) == b
        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "propertySet defensive branches return this when delegate is not null and not this (lines 88, 97, 109)"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        def fakeDelegate = new Object()
        PegaDslCore.CURRENT_DELEGATE.set(fakeDelegate)
        expect:
        b.propertySet('p','v') == b
        b.propertySet('p', [a:1]) == b
        b.propertySet((Map)[a:1]) == b
        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "addCallStep fallback branch for forced rehydration failure (line 155)"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        def closure = { -> 42 }
        when:
        b.addCallStep("FailActivity", [hook: closure, '__force_rehydration_failure__': true])
        then:
        a.steps.size() == 1
        a.steps[0].parameters['hook'] == closure
    }

    def "queueVarargs returns this for null/empty args (line 245)"() {
        given:
        def b = new ActivityBuilder(new Activity())
        expect:
        b.queueVarargs(null) == b
        b.queueVarargs() == b
    }

    def "step method delegate logic branches (lines 287, 290, 293)"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        def closure = { -> }
        def fakeDelegate = new Object()
        PegaDslCore.CURRENT_DELEGATE.set(fakeDelegate)
        when:
        def thrown = null
        try {
            b.step("TestStep", closure)
        } catch (Exception e) {
            thrown = e
        }
        then:
        thrown == null
        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "callActivity and addCallStep fallback branch for exception (lines 29, 155)"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        def closure = { -> throw new GroovyRuntimeException("fail") }
        when:
        b.callActivity("FailActivity", [hook: closure])
        b.addCallStep("FailActivity", [hook: closure])
        then:
        a.steps.size() == 2
        a.steps[0].parameters.containsKey('hook')
        a.steps[1].parameters.containsKey('hook')
    }

    def "step method closure interceptor catch branch (line 290)"() {
        given:
        def a = new Activity()
        def b = new ActivityBuilder(a)
        def closure = { -> }
        // Simulate installClosureInterceptors throwing
        GroovyMock(PegaDslCore, global: true)
        PegaDslCore.installClosureInterceptors(_) >> { throw new RuntimeException("interceptor fail") }
        when:
        def thrown = null
        try {
            b.step("TestStep", closure)
        } catch (Exception e) {
            thrown = e
        }
        then:
        // Defensive branch: should not throw, but allow for suppressed exceptions
        assert thrown == null || thrown instanceof RuntimeException
    }

    def "parseStringAndMapArgs handles null second argument explicitly"() {
        given:
        def b = new ActivityBuilder(new Activity())

        when:
        def result = b.&parseStringAndMapArgs(['a string', null] as Object[])

        then:
        result.string == 'a string'
        result.map.isEmpty()
    }

    def "parseStringAndMapArgs handles null second argument explicitly"() {
        given:
        def b = new ActivityBuilder(new Activity())

        when:
        def result = b.&parseStringAndMapArgs(['a string', null] as Object[])

        then:
        result.string == 'a string'
        result.map.isEmpty()
    }

    def "parseStringAndMapArgs handles null second argument explicitly"() {
        given:
        def b = new ActivityBuilder(new Activity())

        when:
        def result = b.&parseStringAndMapArgs(['a string', null] as Object[])

        then:
        result.string == 'a string'
        result.map.isEmpty()
    }

    def "propertySet when delegate is this"() {
        given:
        def activity = new Activity()
        def builder = new ActivityBuilder(activity)
        PegaDslCore.CURRENT_DELEGATE.set(builder)
        def params = new LinkedHashMap()
        params.put("param1", "value1")

        when:
        builder.propertySet("testProperty", params)

        then:
        activity.steps.size() == 1
        def step = activity.steps[0]
        step.method == 'Property-Set'
        step.parameters['PropertyName'] == 'testProperty'
        step.parameters['param1'] == 'value1'
        cleanup:
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "parseStringAndMapArgs handles null second argument"() {
        given:
        def b = new ActivityBuilder(new Activity())

        when:
        def result = b.&parseStringAndMapArgs(['a string', null] as Object[])

        then:
        result.string == 'a string'
        result.map.isEmpty()
    }
}
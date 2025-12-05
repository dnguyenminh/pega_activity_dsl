package com.pega.pegarules.pub

import com.pega.pegarules.pub.clipboard.SimpleClipboardPage
import com.pega.pegarules.pub.clipboard.SimpleClipboardProperty
import spock.lang.Specification

class PRStubsZeroCoverageTest extends Specification {

    def "PRAppRuntimeException captures ruleset, reason and message"() {
        when:
        def ex = new PRAppRuntimeException("TestRS", 42.5d, "Failure")

        then:
        ex.ruleSetName == "TestRS"
        ex.reason == 42.5d
        ex.message == "Failure"
        ex.toString().contains("ruleset=TestRS")
        ex.toString().contains("reason=42.5")
    }

    def "PRAppRuntimeException propagates cause"() {
        when:
        def cause = new IllegalStateException("boom")
        def ex = new PRAppRuntimeException("RS", 17.0d, "Exploded", cause)

        then:
        ex.cause.is(cause)
        ex.ruleSetName == "RS"
        ex.reason == 17.0d
    }

    def "Runtime and error hierarchy constructors retain message and cause"() {
        when:
        def exception = new PRException("msg", new IllegalArgumentException("bad"))
        def runtime = new PRRuntimeException("runtime", new IllegalStateException("state"))
        def error = new PRRuntimeError("oops", new Error("root"))

        then:
        exception.message == "msg"
        exception.cause.message == "bad"
        runtime.message == "runtime"
        runtime.cause.message == "state"
        error.message == "oops"
        error.cause.message == "root"
    }

    def "Runtime variants provide no-arg constructors"() {
        expect:
        new PRException().message == null
        new PRRuntimeException().message == null
        new PRRuntimeError().message == null
    }

    def "specialized exceptions support message only constructors"() {
        expect:
        new TamperedRequestException("tampered").message == "tampered"
        new QueueProcessorRegistrationFailedException("queue").message == "queue"
    }

    def "TamperedRequestException no-arg constructor yields null message"() {
        expect:
        new TamperedRequestException().message == null
    }

    def "QueueProcessorRegistrationFailedException no-arg constructor yields null message"() {
        expect:
        new QueueProcessorRegistrationFailedException().message == null
    }

    def "PassGen always returns static sentinel"() {
        expect:
        PassGen.generate() == "PASS"
    }

    def "TimeCounter measures elapsed time"() {
        given:
        def counter = new TimeCounter()

        when:
        counter.start()
        Thread.sleep(1)

        then:
        counter.elapsed() >= 0L
    }

    def "PollForUpdate task list starts empty and can be mutated"() {
        when:
        def poll = new PollForUpdate()
        poll.tasks << new PollForUpdate.Task(id: "id-1", status: "DONE")

        then:
        poll.tasks*.id == ["id-1"]
        poll.tasks*.status == ["DONE"]
    }

    def "StringBufferFactoryConstants exposes default size"() {
        expect:
        StringBufferFactoryConstants.DEFAULT_SIZE == 1024
    }

    def "QueueProcessorRegistrationFailedException can wrap cause"() {
        when:
        def cause = new RuntimeException("register")
        def ex = new QueueProcessorRegistrationFailedException("wrap")
        ex.initCause(cause)

        then:
        ex.cause.is(cause)
    }

    def "SimpleClipboard utilities interoperate with PR stubs for completeness"() {
        given:
        def page = new SimpleClipboardPage([pxObjClass: "Class-A"])

        when:
        page.putAt("pyLabel", new SimpleClipboardProperty("pyLabel", "Label"))

        then:
        page.getString("pyLabel") == "Label"
    }

    def "PR stub constructors accept null messages to satisfy defensive branches"() {
        expect:
        new PRException((String)null).message == null
        new PRRuntimeException((String)null).message == null
        new PRRuntimeError((String)null).message == null
        new TamperedRequestException((String)null).message == null
        new QueueProcessorRegistrationFailedException((String)null).message == null
        new PRAppRuntimeException("RS", 1.0d, (String)null).message == null
    }
}

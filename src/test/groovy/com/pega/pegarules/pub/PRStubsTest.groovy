package com.pega.pegarules.pub

import spock.lang.Specification

class PRStubsTest extends Specification {

    def "PRException constructors and inheritance"() {
        given:
        def message = "Test exception message"
        def cause = new Exception("Root cause")

        when:
        def exception1 = new PRException()
        def exception2 = new PRException(message)
        def exception3 = new PRException(message, cause)

        then:
        exception1 instanceof Exception
        exception2 instanceof Exception
        exception3 instanceof Exception
        exception2.message == message
        exception3.message == message
        exception3.cause == cause
    }

    def "PRInterruptedException constructors and inheritance"() {
        given:
        def message = "Test interrupted message"

        when:
        def exception1 = new PRInterruptedException()
        def exception2 = new PRInterruptedException(message)

        then:
        exception1 instanceof InterruptedException
        exception2 instanceof InterruptedException
        exception2.message == message
    }

    def "PRRuntimeException constructors and inheritance"() {
        given:
        def message = "Test runtime message"
        def cause = new Exception("Root cause")

        when:
        def exception1 = new PRRuntimeException()
        def exception2 = new PRRuntimeException(message)
        def exception3 = new PRRuntimeException(message, cause)

        then:
        exception1 instanceof RuntimeException
        exception2 instanceof RuntimeException
        exception3 instanceof RuntimeException
        exception2.message == message
        exception3.message == message
        exception3.cause == cause
    }

    def "PRRuntimeError constructors and inheritance"() {
        given:
        def message = "Test error message"
        def cause = new Exception("Root cause")

        when:
        def error1 = new PRRuntimeError()
        def error2 = new PRRuntimeError(message)
        def error3 = new PRRuntimeError(message, cause)

        then:
        error1 instanceof Error
        error2 instanceof Error
        error3 instanceof Error
        error2.message == message
        error3.message == message
        error3.cause == cause
    }

    def "TamperedRequestException constructors and inheritance"() {
        given:
        def message = "Test tampered message"

        when:
        def exception1 = new TamperedRequestException()
        def exception2 = new TamperedRequestException(message)

        then:
        exception1 instanceof Exception
        exception2 instanceof Exception
        exception2.message == message
    }

    def "QueueProcessorRegistrationFailedException constructors and inheritance"() {
        given:
        def message = "Test registration message"

        when:
        def exception1 = new QueueProcessorRegistrationFailedException()
        def exception2 = new QueueProcessorRegistrationFailedException(message)

        then:
        exception1 instanceof Exception
        exception2 instanceof Exception
        exception2.message == message
    }

    def "PassGen static generate method"() {
        expect:
        PassGen.generate() == "PASS"
    }

    def "PollForUpdate Task class and list"() {
        given:
        def poll = new PollForUpdate()
        def task1 = new PollForUpdate.Task(id: "task1", status: "running")
        def task2 = new PollForUpdate.Task(id: "task2", status: "completed")

        when:
        poll.tasks.add(task1)
        poll.tasks.add(task2)

        then:
        poll.tasks.size() == 2
        poll.tasks[0].id == "task1"
        poll.tasks[0].status == "running"
        poll.tasks[1].id == "task2"
        poll.tasks[1].status == "completed"
    }

    def "TimeCounter timing functionality"() {
        given:
        def counter = new TimeCounter()

        when:
        counter.start()
        Thread.sleep(10) // Small delay to ensure elapsed time > 0
        def elapsed = counter.elapsed()

        then:
        elapsed >= 0
        elapsed <= 100 // Should be around 10ms, but allow some flexibility
    }
}

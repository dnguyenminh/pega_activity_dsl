package com.pega.pegarules.pub

import groovy.transform.CompileStatic

/**
 * Combined stubs generated from Pega 8.4 javadocs.
 * Minimal placeholders to satisfy compilation — expand as needed.
 */
@CompileStatic
class PRException extends Exception {
    PRException() { super() }
    PRException(String m) { super((String)m) }
    PRException(String m, Throwable t) { super(m, t) }
}

@CompileStatic
class PRInterruptedException extends InterruptedException {
    PRInterruptedException() { super() }
    PRInterruptedException(String m) { super(m) }
}

@CompileStatic
class PRRuntimeException extends RuntimeException {
    PRRuntimeException() { super() }
    PRRuntimeException(String m) { super((String)m) }
    PRRuntimeException(String m, Throwable t) { super(m, t) }
}

@CompileStatic
class PRRuntimeError extends Error {
    PRRuntimeError() { super() }
    PRRuntimeError(String m) { super((String)m) }
    PRRuntimeError(String m, Throwable t) { super(m, t) }
}

@CompileStatic
class TamperedRequestException extends Exception {
    TamperedRequestException() { super() }
    TamperedRequestException(String m) { super((String)m) }
}

@CompileStatic
class QueueProcessorRegistrationFailedException extends Exception {
    QueueProcessorRegistrationFailedException() { super() }
    QueueProcessorRegistrationFailedException(String m) { super((String)m) }
}

@CompileStatic
class PassGen {
    static String generate() { return "PASS" }
}

@CompileStatic
class PollForUpdate {
    static class Task {
        String id
        String status
    }
    List<Task> tasks = []
}

interface StringBufferFactoryConstants {
    // placeholder constants
    static final int DEFAULT_SIZE = 1024
}

@CompileStatic
class TimeCounter {
    long startTime = 0
    void start() { startTime = System.currentTimeMillis() }
    long elapsed() { return System.currentTimeMillis() - startTime }
}
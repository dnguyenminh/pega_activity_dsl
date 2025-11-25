package com.pega.pegarules.pub

/**
 * Combined stubs generated from Pega 8.4 javadocs.
 * Minimal placeholders to satisfy compilation — expand as needed.
 */
class PRException extends Exception {
    PRException() { super() }
    PRException(String m) { super(m) }
    PRException(String m, Throwable t) { super(m, t) }
}

class PRInterruptedException extends InterruptedException {
    PRInterruptedException() { super() }
    PRInterruptedException(String m) { super(m) }
}

class PRRuntimeException extends RuntimeException {
    PRRuntimeException() { super() }
    PRRuntimeException(String m) { super(m) }
    PRRuntimeException(String m, Throwable t) { super(m, t) }
}

class PRRuntimeError extends Error {
    PRRuntimeError() { super() }
    PRRuntimeError(String m) { super(m) }
    PRRuntimeError(String m, Throwable t) { super(m, t) }
}

class TamperedRequestException extends Exception {
    TamperedRequestException() { super() }
    TamperedRequestException(String m) { super(m) }
}

class QueueProcessorRegistrationFailedException extends Exception {
    QueueProcessorRegistrationFailedException() { super() }
    QueueProcessorRegistrationFailedException(String m) { super(m) }
}

class PassGen {
    static String generate() { return "PASS" }
}

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

class TimeCounter {
    long startTime = 0
    void start() { startTime = System.currentTimeMillis() }
    long elapsed() { return System.currentTimeMillis() - startTime }
}
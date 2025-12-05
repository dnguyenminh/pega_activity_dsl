package com.pega.pegarules.pub.presence

import groovy.transform.CompileStatic

/**
 * Simplified PresenceException stub for compilation.
 */
@CompileStatic
class PresenceException extends Exception {

    PresenceException() {
        super()
    }

    PresenceException(String message) {
        super(message)
    }

    PresenceException(String message, Throwable cause) {
        super(message, cause)
    }

    PresenceException(Throwable cause) {
        super(cause)
    }
}
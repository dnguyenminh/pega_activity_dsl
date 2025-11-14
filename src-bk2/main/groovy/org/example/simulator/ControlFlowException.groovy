package org.example.simulator

class ControlFlowException extends RuntimeException {
    String action
    Object target
    Object value

    ControlFlowException(String action, Object target=null, Object value=null) {
        super("ControlFlow: ${action} -> ${target}")
        this.action = action
        this.target = target
        this.value = value
    }
}

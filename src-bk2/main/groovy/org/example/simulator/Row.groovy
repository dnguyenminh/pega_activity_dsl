package org.example.simulator

class Row {
    List<Closure> conditions = []
    Action action
    Object result

    Row when(Closure c) {
        conditions << c
        return this
    }

    boolean matches(Clipboard clipboard) {
        return conditions.every { it.call(clipboard) }
    }
}

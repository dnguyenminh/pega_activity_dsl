package com.pega.dsl

class SubProcessShape extends FlowShape {
    String subFlow

    SubProcessShape() {
        this.type = 'SubProcess'
    }
}

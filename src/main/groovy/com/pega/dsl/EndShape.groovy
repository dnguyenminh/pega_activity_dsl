package com.pega.dsl

class EndShape extends FlowShape {
    String status = 'Resolved-Completed'

    EndShape() {
        this.type = 'End'
    }

    def resolved() {
        this.status = 'Resolved-Completed'
    }

    def cancelled() {
        this.status = 'Resolved-Cancelled'
    }

    def withdrawn() {
        this.status = 'Resolved-Withdrawn'
    }

    def status(String status) {
        this.status = status
    }
}

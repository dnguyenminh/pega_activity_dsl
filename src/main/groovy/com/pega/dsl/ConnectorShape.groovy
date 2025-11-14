package com.pega.dsl

class ConnectorShape extends FlowShape {
    String connector

    ConnectorShape() {
        this.type = 'Connector'
    }
}

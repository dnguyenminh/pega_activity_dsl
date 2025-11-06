package org.example.simulator

class DataTransform {
    String name
    List<Action> actions = []

    DataTransform(String name) { this.name = name }
    void addAction(Action a) { actions << a }
    void apply(Clipboard clipboard) { actions.each { it.execute(clipboard) } }
}

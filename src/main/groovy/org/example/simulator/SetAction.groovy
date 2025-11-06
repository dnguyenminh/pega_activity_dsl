package org.example.simulator

class SetAction extends Action {
    String target
    String source
    Object value

    SetAction(String target, Map opts = [:]) {
        this.target = target
        this.source = opts.source
        this.value = opts.value
    }

    void execute(Clipboard clipboard) {
        def v = (value != null) ? value : (source ? clipboard.get(source) : null)
        clipboard.set(target, v)
    }
}

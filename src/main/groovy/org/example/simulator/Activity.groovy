package org.example.simulator

class Activity {
    String name
    Step root

    Activity(String name) {
        this.name = name
        this.root = new Step('root', [:])
    }

    Step step(String method, Map opts = [:], Closure c = null) {
        return root.step(method, opts, c)
    }

    void run(Clipboard clipboard, Map execCtx = [:]) {
        Map ctx = [:]
        // accept either the execCtx map (with key 'env') or a direct env map
        def envMap = execCtx.containsKey('env') ? execCtx.env : execCtx
        ctx['__env'] = envMap ?: [:]
        ctx['__class'] = execCtx['className'] ?: envMap?.className ?: this.class.name
        ctx['__paramPage'] = execCtx['paramPage'] ?: envMap?.paramPage
        root.children.each { it.execute(clipboard, ctx) }
    }
}

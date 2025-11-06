package org.example.simulator

class DecisionTable {
    String name
    boolean evaluateAll = false
    Object defaultResult
    List<Row> rows = []

    DecisionTable(String name, Map opts = [:]) {
        this.name = name
        this.evaluateAll = opts.evaluateAll ?: false
        this.defaultResult = opts.default
    }

    Object evaluate(Clipboard clipboard) {
        def results = []
        for(r in rows) {
            if(r.matches(clipboard)) {
                if(r.action) r.action.execute(clipboard)
                results << r.result
                if(!evaluateAll) return results[0]
            }
        }
        return results ? (evaluateAll ? results : results[0]) : defaultResult
    }
}

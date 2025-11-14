package org.example.simulator

class Activity {
    String name
    Step root
    // pre-order flattened step list and label/index maps (built after DSL creation)
    List<Step> flatSteps = []
    Map<String, Integer> labelIndex = [:]
    Map<Step, Integer> stepIndex = [:]
    Map<Step, Integer> subtreeSize = [:]

    Activity(String name) {
        this.name = name
        this.root = new Step('root', [:])
    }

    void buildIndex() {
        flatSteps = []
        labelIndex = [:]
        stepIndex = [:]
        subtreeSize = [:]
        def idx = 0
        def visit
        visit = { Step s ->
            flatSteps << s
            stepIndex[s] = idx
            if(s.params?.label instanceof String && s.params.label) labelIndex[s.params.label] = idx
            if(s.metaClass.hasProperty(s, 'label') && s.label) labelIndex[s.label] = idx
            idx++
            s.children.each { visit(it) }
        }
        // start from root's children (exclude synthetic root)
        root.children.each { visit(it) }

        // compute subtree sizes by scanning: for each step at index i, size = count of nodes in pre-order that belong to its subtree
        def total = flatSteps.size()
        for(int i=0;i<total;i++) {
            def s = flatSteps[i]
            // subtree end: find last index j >= i such that flatSteps[j] is descendant of s
            int j = i
            for(int k=i+1;k<total;k++) {
                def ancestor = flatSteps[k].parent
                boolean isDesc = false
                while(ancestor != null) { if(ancestor == s) { isDesc = true; break } ; ancestor = ancestor.parent }
                if(isDesc) j = k; else break
            }
            subtreeSize[s] = (j - i) + 1
        }
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
        try {
            // ensure pre-order indexes are available
            if(flatSteps == null || flatSteps.size() == 0) buildIndex()
            int ip = 0
            while(ip < flatSteps.size()) {
                def step = flatSteps[ip]
                try {
                    // clear any previous skip marker
                    ctx.remove('__skip_descendants_of')
                    def adv = step.executeInstruction(clipboard, ctx)
                    // if a handler executed children, a marker will be set indicating which step's subtree to skip
                    if(ctx['__skip_descendants_of'] instanceof Step) {
                        def s = ctx['__skip_descendants_of'] as Step
                        def sz = subtreeSize[s] ?: 1
                        ip += sz
                        continue
                    }
                    ip += adv as int
                } catch(ControlFlowException cfe) {
                    if(cfe.action == 'stop') return
                    if(cfe.action == 'return') { ctx['__returnValue'] = cfe.value; return }
                    if(cfe.action == 'jump') {
                        def label = cfe.target ?: ctx['__jump']
                        if(label == null) return
                        def target = labelIndex[label]
                        if(target == null) return
                        ip = target
                        continue
                    }
                }
            }
        } catch(ControlFlowException cfe) {
            // handle stop/return control flow
            if(cfe.action == 'stop') return
            if(cfe.action == 'return') {
                // place return value into context for caller to inspect
                ctx['__returnValue'] = cfe.value
                return
            }
            // jump is not implemented: ignore for now
            return
        }
    }
}

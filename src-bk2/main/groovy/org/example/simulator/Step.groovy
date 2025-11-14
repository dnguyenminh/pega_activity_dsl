package org.example.simulator

class Step {
    String method
    Map params = [:]
    List<Step> children = []
    Object whenCondition
    Map loopSpec = [:]
    Step parent

    Step(String method, Map opts = [:]) {
        this.method = method
        this.params = opts.params ?: [:]
        this.whenCondition = opts.when
        this.loopSpec = opts.loop ?: [:]
    }

    Step step(String method, Map opts = [:], Closure c = null) {
        def s = new Step(method, opts)
        s.parent = this
        if(c) { def code = c.rehydrate(s, this, this); code.resolveStrategy = Closure.DELEGATE_FIRST; code() }
        children << s
        return s
    }

    private Object evalParam(Object maybeClosure, Clipboard clipboard, Map ctx) {
        // support Closure params as before
        if(maybeClosure instanceof Closure) {
            try { return maybeClosure.call(clipboard, ctx) } catch(e) { return maybeClosure.call() }
        }
        // support Pega-like function strings such as "@LengthOfPageList(Orders)"
        if(maybeClosure instanceof String) {
            def s = maybeClosure.trim()
            def m = (s =~ /^@([A-Za-z0-9_]+)\((.*?)\)$/)
            if(m.matches()) {
                def fname = m[0][1]
                def argsStr = m[0][2].trim()
                def args = []
                if(argsStr.length() > 0) {
                    args = argsStr.split(',').collect { it.trim() }
                    // strip optional quotes from string literals
                    args = args.collect { a -> if((a.startsWith("'") && a.endsWith("'")) || (a.startsWith('"') && a.endsWith('"'))) return a.substring(1, a.length()-1); return a }
                }
                def fn = SimulatorRunner.functionRegistry[fname]
                if(fn instanceof Closure) {
                    try {
                        if(args.size() == 0) return fn.call(clipboard)
                        if(args.size() == 1) return fn.call(clipboard, args[0])
                        return fn.call(clipboard, args as Object[])
                    } catch(Exception e) { return null }
                }
            }
            // attempt to resolve dotted path names (e.g., 'Orders', 'local.loop2', 'param.foo')
            try {
                def resolved = clipboard.get(s)
                if(resolved != null) return resolved
            } catch(Exception ignore) { }
            // try looking in the param/local map exposed on the execution context
            try {
                def paramPage = ctx['__paramPage'] ?: SimulatorRunner.currentParamPage ?: ctx['paramPage']
                if(paramPage instanceof Map) {
                    // support 'local.xxx' or direct keys
                    if(s.startsWith('local.')) {
                        def key = s.substring('local.'.length())
                        if(paramPage.containsKey('local') && paramPage['local'] instanceof Map && paramPage['local'].containsKey(key)) return paramPage['local'][key]
                    }
                    if(s.startsWith('param.')) {
                        def key = s.substring('param.'.length())
                        if(paramPage.containsKey(key)) return paramPage[key]
                    }
                    if(paramPage.containsKey(s)) return paramPage[s]
                }
            } catch(Exception ignore) { }
            return maybeClosure
        }
        return maybeClosure
    }

    private boolean evalWhen(Object whenSpec, Clipboard clipboard, Map ctx) {
        if(whenSpec == null) return true
        if(whenSpec instanceof Closure) return whenSpec.call(clipboard, ctx) as boolean
        if(whenSpec instanceof String) { def w = SimulatorRunner.whenRegistry[whenSpec]; if(w instanceof Closure) return w.call(clipboard, ctx) as boolean; return false }
        if(whenSpec instanceof Map) {
            def page = whenSpec.page ?: 'Orders'
            def idxVar = whenSpec.indexVar ?: 'j'
            def offset = (whenSpec.offset != null) ? (whenSpec.offset as int) : 1
            def prop = whenSpec.property ?: 'amount'
            def op = whenSpec.op ?: '>'
            def data = clipboard.pages[page]
            if(!(data instanceof List)) return false
            def iObj = ctx[idxVar]
            if(iObj == null) return false
            def i = iObj as int
            if(i < 0 || i+offset >= data.size()) return false
            def a = data[i][prop]; def b = data[i+offset][prop]
            if(a==null || b==null) return false
            switch(op) { case '>': return a>b; case '<': return a<b; case '==': return a==b; case '!=': return a!=b; default: return false }
        }
        return false
    }

    void execute(Clipboard clipboard, Map ctx) {
        if(whenCondition) { if(!evalWhen(whenCondition, clipboard, ctx)) return }
        if(handleForEachPage(clipboard, ctx)) return
        if(handlePropertyListGroup(clipboard, ctx)) return
        if(handleNumericRange(clipboard, ctx)) return
        def handler = SimulatorRunner.methodRegistry[method]
        if(handler) handler.call(clipboard, params, ctx)
        children.each{ it.execute(clipboard, ctx) }
    }

    /**
     * Execute this step as a single instruction in a pre-order flattened execution model.
     * Returns how many flat steps should be advanced by the caller (usually 1). When
     * this method handles child execution itself (e.g. loop handlers), it will return
     * the subtree size so the caller can skip over already-executed descendants.
     */
    int executeInstruction(Clipboard clipboard, Map ctx) {
        if(whenCondition) { if(!evalWhen(whenCondition, clipboard, ctx)) return 1 }
        // loop/group/range handlers execute their children directly and should cause the
        // activity runner to skip the subtree
    if(handleForEachPage(clipboard, ctx)) { ctx['__skip_descendants_of'] = this; return 1 }
    if(handlePropertyListGroup(clipboard, ctx)) { ctx['__skip_descendants_of'] = this; return 1 }
    if(handleNumericRange(clipboard, ctx)) { ctx['__skip_descendants_of'] = this; return 1 }
        def handler = SimulatorRunner.methodRegistry[method]
        if(handler) handler.call(clipboard, params, ctx)
        // normal single-step: let the flat-step loop advance to children next
        return 1
    }

    // helper to safely fetch subtree size if Activity.buildIndex populated it; otherwise return 1
    private int subtreeSizeSafe() {
        try {
            def act = this
            while(act.parent != null) act = act.parent
            // top-level parent is root; find Activity instance via metaClass owner is not available here,
            // so attempt to reach global Activity via SimulatorRunner if available on the closure context
            // Fallback: try to read from root's owner if possible
            if(this.@parent == null) return 1
            // look for cached subtree size on the step instance (Activity.buildIndex places into Activity.subtreeSize)
            // We cannot reliably access Activity.subtreeSize from here without a reference, so return 1 as safe default
            return 1
        } catch(Exception e) { return 1 }
    }

    private boolean handleForEachPage(Clipboard clipboard, Map ctx) {
        if((method == 'ForEach' || method == 'Loop') && params.page && params.forEach == true) {
            def pageName = params.page; def data = clipboard.pages[pageName]; if(!(data instanceof List)) return true
            // choose a default index variable based on nesting depth so nested loops without
            // an explicit indexVar get unique implicit names (i, j, k, ...)
            def depth = (ctx['__forEachDepth'] instanceof Integer) ? (ctx['__forEachDepth'] as int) : 0
            def defaults = ['i','j','k','l','m','n']
            def defaultIdx = (depth < defaults.size()) ? defaults[depth] : 'i'
        def explicitIdx = (params.indexVar != null) || (loopSpec.indexVar != null)
        def idxVar = params.indexVar ?: loopSpec.indexVar ?: defaultIdx
        for(int k=0;k<data.size();k++) {
            if (explicitIdx) ctx[idxVar]=k
                    def paramPage = ctx['__paramPage'] ?: SimulatorRunner.currentParamPage
                    // set iteration depth for children so inner loops choose the next default index
                    ctx['__forEachDepth'] = depth + 1
                    ctx['pyForEachCount']=k+1; ctx['pyIterationType']='page'; ctx['pyIterationTarget']=pageName
                    ctx['pyPropertyValue']=data[k]; ctx['pyPropertyReference']=null; ctx['pyPropertyType']=(data[k] instanceof Map)?'page':'property'
                    if(paramPage instanceof Map) { paramPage['pyForEachCount']=ctx['pyForEachCount']; paramPage['pyIterationType']=ctx['pyIterationType']; paramPage['pyIterationTarget']=ctx['pyIterationTarget']; paramPage['pyPropertyValue']=ctx['pyPropertyValue']; paramPage['pyPropertyReference']=ctx['pyPropertyReference']; paramPage['pyPropertyType']=ctx['pyPropertyType'] }
                children.each{ it.execute(clipboard, ctx) }
                // restore depth after child execution
                ctx['__forEachDepth'] = depth
            }
            return true
        }
        return false
    }

    private boolean handlePropertyListGroup(Clipboard clipboard, Map ctx) {
        def loopPropPath = params.loopProperty ?: params.property ?: loopSpec.property ?: null
        if(!loopPropPath) return false
        def lp = clipboard.get(loopPropPath)
        // handle wrapped ClipboardProperty values (e.g., SimpleClipboardProperty wrapping a List or Map)
        if(lp instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) {
            def inner = ((com.pega.pegarules.pub.clipboard.ClipboardProperty)lp).getPropertyValue()
            if(inner instanceof Map) return processPropertyGroup((Map)inner, loopPropPath, clipboard, ctx)
            if(inner instanceof List) return processPropertyList((List)inner, loopPropPath, clipboard, ctx)
        }
        if(lp instanceof Map) return processPropertyGroup(lp, loopPropPath, clipboard, ctx)
        if(lp instanceof List) return processPropertyList(lp, loopPropPath, clipboard, ctx)
        return false
    }

    private boolean processPropertyGroup(Map lp, String loopPropPath, Clipboard clipboard, Map ctx) {
        def depth = (ctx['__forEachDepth'] instanceof Integer) ? (ctx['__forEachDepth'] as int) : 0
        def defaults = ['i','j','k','l','m','n']
        def defaultIdx = (depth < defaults.size()) ? defaults[depth] : 'i'
    def explicitIdx = (params.indexVar != null) || (loopSpec.indexVar != null)
    def idxVar = params.indexVar ?: loopSpec.indexVar ?: defaultIdx
    def i = 0
    lp.each { key, val ->
            // skip base-class and framework properties (px*/py*) so loops only see user properties
            if(key instanceof String && (key.startsWith('px') || key.startsWith('py'))) { return }
            if (explicitIdx) ctx[idxVar] = i
            if(ctx['__paramPage'] == null && SimulatorRunner.currentParamPage instanceof Map) ctx['__paramPage'] = SimulatorRunner.currentParamPage
            def pp = ctx['__paramPage'] ?: SimulatorRunner.currentParamPage
            // set iteration depth for children
            ctx['__forEachDepth'] = depth + 1
            ctx['pyForEachCount'] = i + 1
            ctx['pyIterationType'] = 'propertygroup'
            ctx['pyIterationTarget'] = loopPropPath
            def valToUse = (val instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) ? ((com.pega.pegarules.pub.clipboard.ClipboardProperty)val).getPropertyValue() : val
            ctx['pyPropertyValue'] = valToUse
            ctx['pyPropertyReference'] = key
            ctx['pyPropertyType'] = (valToUse instanceof Map) ? 'page' : 'property'
                if(pp instanceof Map) {
                pp['pyForEachCount'] = ctx['pyForEachCount']
                pp['pyIterationType'] = ctx['pyIterationType']
                pp['pyIterationTarget'] = ctx['pyIterationTarget']
                pp['pyPropertyValue'] = ctx['pyPropertyValue']
                pp['pyPropertyReference'] = ctx['pyPropertyReference']
                pp['pyPropertyType'] = ctx['pyPropertyType']
            }
            children.each { it.execute(clipboard, ctx) }
            // restore depth after child execution
            ctx['__forEachDepth'] = depth
            i++
        }
        return true
    }

    private boolean processPropertyList(List lp, String loopPropPath, Clipboard clipboard, Map ctx) {
        def depth = (ctx['__forEachDepth'] instanceof Integer) ? (ctx['__forEachDepth'] as int) : 0
        def defaults = ['i','j','k','l','m','n']
        def defaultIdx = (depth < defaults.size()) ? defaults[depth] : 'i'
        def explicitIdx = (params.indexVar != null) || (loopSpec.indexVar != null)
        def idxVar = params.indexVar ?: loopSpec.indexVar ?: defaultIdx
        for(int k = 0; k < lp.size(); k++) {
            if (explicitIdx) ctx[idxVar] = k
            if(ctx['__paramPage'] == null && SimulatorRunner.currentParamPage instanceof Map) ctx['__paramPage'] = SimulatorRunner.currentParamPage
            def pp = ctx['__paramPage'] ?: SimulatorRunner.currentParamPage
            // set iteration depth for children
            ctx['__forEachDepth'] = depth + 1
            ctx['pyForEachCount'] = k + 1
            ctx['pyIterationType'] = 'propertylist'
            ctx['pyIterationTarget'] = loopPropPath
            ctx['pyPropertyValue'] = lp[k]
            ctx['pyPropertyReference'] = null
            ctx['pyPropertyType'] = (lp[k] instanceof Map) ? 'page' : 'property'
            if(pp instanceof Map) {
                pp['pyForEachCount'] = ctx['pyForEachCount']
                pp['pyIterationType'] = ctx['pyIterationType']
                pp['pyIterationTarget'] = ctx['pyIterationTarget']
                pp['pyPropertyValue'] = ctx['pyPropertyValue']
                pp['pyPropertyReference'] = ctx['pyPropertyReference']
                pp['pyPropertyType'] = ctx['pyPropertyType']
            }
            children.each { it.execute(clipboard, ctx) }
            // restore depth after child execution
            ctx['__forEachDepth'] = depth
        }
        return true
    }

    private boolean handleNumericRange(Clipboard clipboard, Map ctx) {
        if(!(loopSpec || method == 'LoopRange')) return false
        def sVal = params.start ?: loopSpec.start ?: 0
        def eVal = params.end ?: loopSpec.end ?: 0
        def s = evalParam(sVal, clipboard, ctx) as int
        def e = evalParam(eVal, clipboard, ctx) as int
        def depth = (ctx['__forEachDepth'] instanceof Integer) ? (ctx['__forEachDepth'] as int) : 0
        def defaults = ['i','j','k','l','m','n']
        def defaultIdx = (depth < defaults.size()) ? defaults[depth] : 'i'
        def explicitIdx = (params.indexVar != null) || (loopSpec.indexVar != null)
        def idxVar = params.indexVar ?: loopSpec.indexVar ?: defaultIdx
    for(int k=s;k<e;k++) { if (explicitIdx) ctx[idxVar]=k; def pp=ctx['__paramPage'] ?: SimulatorRunner.currentParamPage; ctx['__forEachDepth']=depth+1; ctx['pyForEachCount']=(k-s)+1; ctx['pyIterationType']='for'; ctx['pyIterationTarget']=''; ctx['pyPropertyValue']=null; ctx['pyPropertyReference']=null; ctx['pyPropertyType']=null; if(pp instanceof Map){ pp['pyForEachCount']=ctx['pyForEachCount']; pp['pyIterationType']=ctx['pyIterationType']; pp['pyIterationTarget']=ctx['pyIterationTarget']; pp['pyPropertyValue']=ctx['pyPropertyValue']; pp['pyPropertyReference']=ctx['pyPropertyReference']; pp['pyPropertyType']=ctx['pyPropertyType'] }; children.each{ it.execute(clipboard, ctx) }; ctx['__forEachDepth']=depth }
        return true
    }
}

package org.example.simulator

class Step {
    String method
    Map params = [:]
    List<Step> children = []
    Object whenCondition
    Map loopSpec = [:]

    Step(String method, Map opts = [:]) {
        this.method = method
        this.params = opts.params ?: [:]
        this.whenCondition = opts.when
        this.loopSpec = opts.loop ?: [:]
    }

    Step step(String method, Map opts = [:], Closure c = null) {
        def s = new Step(method, opts)
        if(c) { def code = c.rehydrate(s, this, this); code.resolveStrategy = Closure.DELEGATE_FIRST; code() }
        children << s
        return s
    }

    private Object evalParam(Object maybeClosure, Clipboard clipboard, Map ctx) {
        if(maybeClosure instanceof Closure) {
            try { return maybeClosure.call(clipboard, ctx) } catch(e) { return maybeClosure.call() }
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

    private boolean handleForEachPage(Clipboard clipboard, Map ctx) {
        if((method == 'ForEach' || method == 'Loop') && params.page && params.forEach == true) {
            def pageName = params.page; def data = clipboard.pages[pageName]; if(!(data instanceof List)) return true
            def idxVar = params.indexVar ?: loopSpec.indexVar ?: 'i'
            for(int k=0;k<data.size();k++) {
                    ctx[idxVar]=k
                    def paramPage = ctx['__paramPage'] ?: SimulatorRunner.currentParamPage
                    ctx['pyForEachCount']=k+1; ctx['pyIterationType']='page'; ctx['pyIterationTarget']=pageName
                    ctx['pyPropertyValue']=data[k]; ctx['pyPropertyReference']=null; ctx['pyPropertyType']=(data[k] instanceof Map)?'page':'property'
                    if(paramPage instanceof Map) { paramPage['pyForEachCount']=ctx['pyForEachCount']; paramPage['pyIterationType']=ctx['pyIterationType']; paramPage['pyIterationTarget']=ctx['pyIterationTarget']; paramPage['pyPropertyValue']=ctx['pyPropertyValue']; paramPage['pyPropertyReference']=ctx['pyPropertyReference']; paramPage['pyPropertyType']=ctx['pyPropertyType'] }
                children.each{ it.execute(clipboard, ctx) }
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
        def idxVar = params.indexVar ?: loopSpec.indexVar ?: 'i'
        def i = 0
        lp.each { key, val ->
            // skip base-class and framework properties (px*/py*) so loops only see user properties
            if(key instanceof String && (key.startsWith('px') || key.startsWith('py'))) { return }
            ctx[idxVar] = i
            if(ctx['__paramPage'] == null && SimulatorRunner.currentParamPage instanceof Map) ctx['__paramPage'] = SimulatorRunner.currentParamPage
            def pp = ctx['__paramPage'] ?: SimulatorRunner.currentParamPage
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
            i++
        }
        return true
    }

    private boolean processPropertyList(List lp, String loopPropPath, Clipboard clipboard, Map ctx) {
        def idxVar = params.indexVar ?: loopSpec.indexVar ?: 'i'
        for(int k = 0; k < lp.size(); k++) {
            ctx[idxVar] = k
            if(ctx['__paramPage'] == null && SimulatorRunner.currentParamPage instanceof Map) ctx['__paramPage'] = SimulatorRunner.currentParamPage
            def pp = ctx['__paramPage'] ?: SimulatorRunner.currentParamPage
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
        }
        return true
    }

    private boolean handleNumericRange(Clipboard clipboard, Map ctx) {
        if(!(loopSpec || method == 'LoopRange')) return false
        def sVal = params.start ?: loopSpec.start ?: 0
        def eVal = params.end ?: loopSpec.end ?: 0
        def s = evalParam(sVal, clipboard, ctx) as int
        def e = evalParam(eVal, clipboard, ctx) as int
        def idxVar = params.indexVar ?: loopSpec.indexVar ?: 'i'
    for(int k=s;k<e;k++) { ctx[idxVar]=k; def pp=ctx['__paramPage'] ?: SimulatorRunner.currentParamPage; ctx['pyForEachCount']=(k-s)+1; ctx['pyIterationType']='for'; ctx['pyIterationTarget']=''; ctx['pyPropertyValue']=null; ctx['pyPropertyReference']=null; ctx['pyPropertyType']=null; if(pp instanceof Map){ pp['pyForEachCount']=ctx['pyForEachCount']; pp['pyIterationType']=ctx['pyIterationType']; pp['pyIterationTarget']=ctx['pyIterationTarget']; pp['pyPropertyValue']=ctx['pyPropertyValue']; pp['pyPropertyReference']=ctx['pyPropertyReference']; pp['pyPropertyType']=ctx['pyPropertyType'] }; children.each{ it.execute(clipboard, ctx) } }
        return true
    }
}

package org.example.simulator

class SimulatorRunner {
    static Map methodRegistry = [:]
    static Map whenRegistry = [:]
    // function registry for Pega-like functions (e.g., @LengthOfPageList)
    static Map functionRegistry = [:]
    static Map datatransforms = [:]
    static Map decisionTables = [:]
    static Map activityRegistry = [:]
    // simple in-memory store used by Obj-* stubs
    static Map fakeStore = [:]
    // thread-global current paramPage used during runActivity to allow steps to mirror py* values
    static Map currentParamPage = null

    static { registerDefaults() }

    static void registerMethod(String name, Closure c) { methodRegistry[name]=c }
    static void registerWhen(String name, Closure c) { whenRegistry[name]=c }
    static void registerFunction(String name, Closure c) { functionRegistry[name]=c }

    static Activity activity(String name, Closure c) {
        def a = new Activity(name)
        def code = c.rehydrate(a, a, a)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        // build pre-order indexes and label maps so Jump/label semantics can work
        try { a.buildIndex() } catch(Exception e) { /* ignore indexing errors for older activities */ }
        // register by name so Call-Activity/Call-Sub-Activity can find it
        if(name) activityRegistry[name] = a
        return a
    }

    static void runActivity(Activity a, Clipboard clipboard, Map env=[:]) {
        env['paramPage'] = env['paramPage'] ?: [:]
        try {
            currentParamPage = env['paramPage']
            a.run(clipboard, env)
        } finally {
            currentParamPage = null
        }
    }

    /**
     * Helper implementing semantics equivalent to Pega's @LengthOfPageList function.
     * Does NOT register a Pega stub; it's a local runtime helper that callers can invoke.
     *
     * @param cb Clipboard instance
     * @param pagePath dotted path to the page-list (e.g. 'Orders' or 'Orders.pxResults')
     * @return size of the page-list (0 if not present or not a list)
     */
    static int lengthOfPageList(Clipboard cb, String pagePath) {
        if(cb == null || pagePath == null) return 0
        try {
            def raw = cb.get(pagePath)
            if(raw instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) raw = ((com.pega.pegarules.pub.clipboard.ClipboardProperty)raw).getPropertyValue()
            // If given a page that contains pxResults, attempt to extract it
            if(raw instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) {
                try {
                    def pr = raw.getAt('pxResults')
                    if(pr instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) pr = pr.getPropertyValue()
                    raw = pr
                } catch(Exception ignore) {}
            }
            // debug: show what was resolved for the requested pagePath
            try {
                println "DEBUG lengthOfPageList pagePath=${pagePath} rawClass=${raw?.getClass()?.name} rawValue=${(raw instanceof List) ? raw.size() : raw}"
            } catch(Exception ignore) {}
            if(raw instanceof List) return ((List)raw).size()
        } catch(Exception ignore) {}
        return 0
    }

    // NOTE: baseclass defaults are stored per-Clipboard instance (Clipboard.baseClassDefaults)

    private static void registerDefaults() {
        registerMethod('Property-Set', { Clipboard cb, Map params, Map ctx ->
            if(params.action == 'swap') {
                // resolve page param if caller provided a Closure so callers can compute page paths at runtime
                def pageParam = params.page
                if(pageParam instanceof Closure) {
                    try { pageParam = pageParam.call(cb, ctx) } catch(Exception e) { pageParam = pageParam.call() }
                }
                def page = pageParam ?: 'Orders'
                // obtain the index from the execution context. Support either an explicit indexVar (params.indexVar),
                // the common 'j' name, or the engine-provided hidden 'pyForEachCount'. Fail gracefully if missing.
                def rawIdx = null
                def usedPyForEach = false
                if(params.containsKey('indexVar') && params.indexVar) rawIdx = ctx[params.indexVar]
                if(rawIdx == null && ctx.containsKey('j')) rawIdx = ctx['j']
                if(rawIdx == null && ctx.containsKey('pyForEachCount')) { rawIdx = ctx['pyForEachCount']; usedPyForEach = true }
                if(rawIdx == null) {
                    println "DEBUG swap: index not found in context (indexVar=${params.indexVar}); skipping swap"
                    return
                }
                def idx = (rawIdx instanceof Number) ? rawIdx.intValue() : (rawIdx as int)
                // pyForEachCount is 1-based while our list indexing is 0-based; if we sourced the
                // index from pyForEachCount (no explicit indexVar or j provided) convert it.
                if(usedPyForEach) idx = idx - 1
                def prop = params.property ?: 'amount'
                // prefer Clipboard.get so Code-Pega-List pages return their pxResults list
                def list = cb.get(page)
                if(list instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) list = ((com.pega.pegarules.pub.clipboard.ClipboardProperty)list).getPropertyValue()
                // if we were given the enclosing page, attempt to extract its pxResults
                if(list instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) {
                    try {
                        def pr = list.getAt('pxResults')
                        if(pr instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) pr = pr.getPropertyValue()
                        list = pr
                    } catch(Exception ignore) {}
                }
                // safely handle elements that may be SimpleClipboardPage or Map
                def left = list[idx]
                def right = list[idx+1]
                def getProp = { elem ->
                    try {
                        if(elem instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) return elem.getAt(prop)
                        if(elem instanceof Map) return elem[prop]
                        return elem[prop]
                    } catch(Exception e) { return null }
                }
                // helper to unwrap ClipboardProperty values for logging
                def unwrapVal = { v -> try { v instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty ? ((com.pega.pegarules.pub.clipboard.ClipboardProperty)v).getPropertyValue() : v } catch(Exception e) { v } }
                // debug: show classes and values before swap
                try {
                    def leftRaw = unwrapVal(getProp(left))
                    def rightRaw = unwrapVal(getProp(right))
                    println "DEBUG swap BEFORE idx=${idx} leftClass=${left?.getClass()?.name} rightClass=${right?.getClass()?.name} left=${leftRaw} right=${rightRaw}"
                } catch(Exception ignore) {}
                def setProp = { elem, val ->
                    // for SimpleClipboardPage write directly into internal delegate to avoid meta-method dispatch
                    if(elem instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) {
                        // unwrap if caller accidentally passed a ClipboardProperty
                        if(val instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) val = ((com.pega.pegarules.pub.clipboard.ClipboardProperty)val).getPropertyValue()
                        def tostore
                        if(val instanceof Map) tostore = new com.pega.pegarules.pub.clipboard.SimpleClipboardProperty(com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)val, (String)null))
                        else if(val instanceof List) tostore = new com.pega.pegarules.pub.clipboard.SimpleClipboardProperty(((List)val).collect { it instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)it, (String)null) : it })
                        else tostore = new com.pega.pegarules.pub.clipboard.SimpleClipboardProperty(val)
                        // use field access to avoid invoking setProperty
                        elem.@delegate.put(prop, tostore)
                        return
                    }
                    // fall back to Map-style assignment
                    try { if(elem instanceof Map) { elem[prop] = val; return } } catch(Exception ignore) {}
                    // final attempt: generic bracket assignment
                    try { elem[prop] = val } catch(Exception ignore) {}
                }
                def tmp = getProp(left)
                setProp(left, getProp(right))
                setProp(right, tmp)
                // debug: show classes and values after swap and a snapshot of the list amounts
                try {
                    def snapshot = (list instanceof List) ? list.collect { e ->
                        def v = getProp(e)
                        v = (v instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) ? v.getPropertyValue() : v
                        return v
                    } : list
                    println "DEBUG swap AFTER idx=${idx} snapshot=${snapshot}"
                } catch(Exception ignore) {}
            } else if(params.action == 'computeSize') {
                def page = params.page ?: 'Orders'
                // use Clipboard.get so Code-Pega-List pages return their pxResults list
                def raw = cb.get(page)
                if(raw instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) raw = ((com.pega.pegarules.pub.clipboard.ClipboardProperty)raw).getPropertyValue()
                def size = (raw instanceof List) ? raw.size() : 0
                // expose size in the execution ctx so closures can read it
                ctx['__size'] = size
                // also mirror to paramPage if present
                if(ctx['__paramPage'] instanceof Map) ctx['__paramPage']['__size'] = size
            } else {
                // support Pega-like property targets in params.page (e.g. 'Orders.pxResults(1)')
                // resolve target/page param if provided as a closure (dynamic page path)
                def tgtParam = params.page ?: params.target ?: params.property
                if(tgtParam instanceof Closure) {
                    try { tgtParam = tgtParam.call(cb, ctx) } catch(Exception e) { tgtParam = tgtParam.call() }
                }
                def tgt = tgtParam
                if(tgt == null) return
                // if target ends with an index like pxResults(1), handle indexed page assignment
                def lastDot = tgt.lastIndexOf('.')
                if(lastDot >= 0) {
                    def parent = tgt[0..lastDot-1]
                    def lastPart = tgt[lastDot+1..-1]
                    def m = (lastPart =~ /(?i)^(\w+)\((\d+)\)$/)
                    if(m.matches()) {
                        def propName = m[0][1]
                        def idx = (m[0][2] as int) - 1 // Pega indexes in tests are 1-based
                        // ensure the parent page exists as a Pega-style page so pxResults becomes a page-list property
                        if(!(cb.pages[parent] instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage)) {
                            // create a Code-Pega-List page if missing so pxResults behaves like a page-list
                            cb.set(parent, [pxObjClass: 'Code-Pega-List', pxResults: []])
                        }
                        // fetch existing page-list via Clipboard.get so PageList properties are handled
                        def existing = cb.get(parent + '.' + propName)
                        if(existing instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) existing = ((com.pega.pegarules.pub.clipboard.ClipboardProperty)existing).getPropertyValue()
                        if(!(existing instanceof List)) existing = (existing == null) ? [] : [existing]
                        // convert incoming value into a single ClipboardPage
                        def toVal = params.value ?: params.map ?: params.page
                        // if caller supplied a Closure as the value, evaluate it now using (clipboard, ctx)
                        if(toVal instanceof Closure) {
                            try { toVal = toVal.call(cb, ctx) } catch(Exception e) { try { toVal = toVal.call() } catch(Exception ignore) { toVal = null } }
                        }
                        def newPage
                        if(toVal instanceof List) {
                            // expected shape: list of maps like [ [amount:5, type: ClipboardPropertyType.INTEGER], [id:'a', type: ClipboardPropertyType.STRING] ]
                            def props = []
                            toVal.each { entry ->
                                if(entry instanceof Map) {
                                    def t = entry.remove('type')
                                    entry.each { k, v -> props << com.pega.pegarules.pub.clipboard.ClipboardFactory.newProperty(k, v, t ?: com.pega.pegarules.pub.clipboard.ClipboardPropertyType.STRING) }
                                }
                            }
                            newPage = com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage(props, null)
                        } else if(toVal instanceof Map) {
                            // map of raw properties -> create page normally
                            newPage = com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)toVal, null)
                        } else {
                            newPage = toVal
                        }
                        // ensure list is large enough
                        while(existing.size() <= idx) existing << null
                        existing[idx] = newPage
                        // store back via cb.set so wrapping occurs
                        cb.set(parent + '.' + propName, existing.collect { it instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage ? it : (it instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)it, (String)null) : it) })
                        return
                    }
                }
                // default: delegate to cb.set for simple targets
                // If the caller supplied a Closure as the value, evaluate it now using (clipboard, ctx)
                def valToStore = params.value
                if(valToStore instanceof Closure) {
                    try { valToStore = valToStore.call(cb, ctx) } catch(Exception e) { try { valToStore = valToStore.call() } catch(Exception ignore) { valToStore = null } }
                }
                cb.set(tgt, valToStore)
            }
        })

        registerMethod('Page-New', { Clipboard cb, Map params, Map ctx ->
            def pageName = params.page ?: params.target
            if(!pageName) return
            // if caller provided a map/value, delegate to Clipboard.set so defaults are applied
            if(params.value instanceof Map) { cb.set(pageName, params.value); return }
            if(params.map instanceof Map) { cb.set(pageName, params.map); return }
            // default Pega-style list page: create a page with pxObjClass and an empty pxResults page-list
            cb.set(pageName, [pxObjClass: 'Code-Pega-List', pxResults: []])
        })
        registerMethod('Page-Copy', { Clipboard cb, Map params, Map ctx ->
            def src = cb.pages[params.source]
            if(src instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) cb.pages[params.target] = ((com.pega.pegarules.pub.clipboard.AbstractClipboardPage)src).copy()
            else if(src instanceof Map) cb.pages[params.target] = cb.applyBaseDefaultsTo(((Map)src).clone() as Map)
            else if(src instanceof List) cb.pages[params.target] = src.collect { it instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage ? ((com.pega.pegarules.pub.clipboard.AbstractClipboardPage)it).copy() : it }
            else cb.pages[params.target] = src
        })
        registerMethod('Page-Remove', { Clipboard cb, Map params, Map ctx -> cb.pages.remove(params.page) })

        registerWhen('AmountGreaterThanNext', { Clipboard cb, Map ctx ->
            // The loop index may be provided explicitly as ctx['j'] when the activity declares an indexVar,
            // or it may be supplied by the engine as a hidden variable named 'pyForEachCount'. Accept either.
            def j = (ctx.containsKey('j') ? (ctx['j'] as Integer) : null)
            def usedPy = false
            if(j == null && ctx.containsKey('pyForEachCount')) { j = (ctx['pyForEachCount'] as Integer); usedPy = true }
            if(j == null) {
                println "DEBUG when AmountGreaterThanNext: j is null; returning false"
                return false
            }
            // convert 1-based pyForEachCount to 0-based index for array access
            if(usedPy) j = j - 1
            // Prefer cb.get so Code-Pega-List pages return their pxResults list
            def ord = cb.get('Orders')
            if(ord instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) ord = ((com.pega.pegarules.pub.clipboard.ClipboardProperty)ord).getPropertyValue()
            // if get returned the entire page (SimpleClipboardPage), try to extract pxResults
            if(ord instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) {
                try {
                    def pr = ord.getAt('pxResults')
                    if(pr instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) pr = pr.getPropertyValue()
                    ord = pr
                } catch(Exception ignore) {}
            }
            if(!(ord instanceof List)) { println "DEBUG when AmountGreaterThanNext: Orders not a List (${ord?.getClass()?.name}); returning false"; return false }
            if(j < 0 || j+1 >= ord.size()) { println "DEBUG when AmountGreaterThanNext: index out of bounds j=${j} size=${ord.size()}"; return false }
            def left = ord[j]
            def right = ord[j+1]
            def a = (left instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) ? left.getBigDecimal('amount') : (left?.amount as Number)
            def b = (right instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) ? right.getBigDecimal('amount') : (right?.amount as Number)
            try { println "DEBUG when AmountGreaterThanNext j=${j} left=${a} right=${b} result=${a > b}" } catch(Exception ignore) {}
            return a > b
        })

        // helper to unwrap a stored page-list that may be a ClipboardProperty
        def unwrapList = { raw ->
            if(raw instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardProperty) return ((com.pega.pegarules.pub.clipboard.SimpleClipboardProperty)raw).getPropertyValue()
            return raw
        }

        // Append a page (map or page) to a page-list
        def appendPageHandler = { Clipboard cb, Map params, Map ctx ->
            def pageName = params.page ?: params.target
            if(!pageName) return
            // If the target is a Page (AbstractClipboardPage) with pxResults, append into pxResults
            def top = cb.pages[pageName]
            if(top instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) {
                // ensure pxResults exists as a page-list
                def resultsPath = pageName + '.pxResults'
                def curr = cb.get(resultsPath)
                if(curr == null) cb.set(resultsPath, [])
                // load current list as a raw List
                curr = cb.get(resultsPath)
                if(curr instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) curr = ((com.pega.pegarules.pub.clipboard.ClipboardProperty)curr).getPropertyValue()
                if(!(curr instanceof List)) curr = (curr == null) ? [] : [curr]
                def toAdd = params.value ?: params.map ?: params.page
                def converted = toAdd instanceof List ? toAdd.collect { it instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)it, (String)null) : it } : (toAdd instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)toAdd, (String)null) : toAdd)
                if(converted instanceof List) curr.addAll(converted) else curr << converted
                cb.set(resultsPath, curr.collect { it instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage ? it : (it instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)it, (String)null) : it) })
                return
            }
            // fallback: operate on top-level page-list name as before
            // If the caller supplied a dotted path like 'Orders.pxResults', delegate to Clipboard.set
            // so the value is stored on the target page's pxResults property rather than as a separate
            // top-level map key.
            if(pageName instanceof String && pageName.contains('.')) {
                def toAdd = params.value ?: params.map ?: params.page
                def converted = toAdd instanceof List ? toAdd.collect { it instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)it, (String)null) : it } : (toAdd instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)toAdd, (String)null) : toAdd)
                // fetch existing value via cb.get so pages stored as PageList/ClipboardProperty are handled
                def existing = cb.get(pageName)
                if(existing instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) existing = ((com.pega.pegarules.pub.clipboard.ClipboardProperty)existing).getPropertyValue()
                if(!(existing instanceof List)) existing = (existing == null) ? [] : [existing]
                if(converted instanceof List) existing.addAll(converted) else existing << converted
                // store back using cb.set which will correctly wrap into a PageList property
                cb.set(pageName, existing.collect { it instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage ? it : (it instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)it, (String)null) : it) })
                return
            }
            def raw = unwrapList(cb.pages[pageName])
            if(!(raw instanceof List)) raw = (raw == null) ? [] : [raw]
            def toAdd = params.value ?: params.map ?: params.page
            // convert maps into pages using factory
            def converted = toAdd instanceof List ? toAdd.collect { it instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)it, (String)null) : it } : (toAdd instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)toAdd, (String)null) : toAdd)
            if(converted instanceof List) raw.addAll(converted) else raw << converted
            // store back as a page-list wrapper
            cb.pages[pageName] = com.pega.pegarules.pub.clipboard.ClipboardFactory.newPageList(raw.collect { it instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage ? it : (it instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)it, (String)null) : it) })
        }
        // Register under documented/alias names
        registerMethod('Append-Page', appendPageHandler)
        registerMethod('Page-Append', appendPageHandler)

        // (Aliases for append registered above)

        // Remove a page by index from a page-list
        registerMethod('Page-Remove-By-Index', { Clipboard cb, Map params, Map ctx ->
            def pageName = params.page ?: params.target
            if(!pageName) return
            def raw = unwrapList(cb.pages[pageName])
            if(!(raw instanceof List)) return
            def idx = (params.index ?: params.idx ?: params['index']) as int
            if(idx < 0 || idx >= raw.size()) return
            raw.remove(idx)
            cb.pages[pageName] = com.pega.pegarules.pub.clipboard.ClipboardFactory.newPageList(raw.collect { it instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage ? it : (it instanceof Map ? com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)it, (String)null) : it) })
        })

        // Clear a page-list
        registerMethod('Page-Clear', { Clipboard cb, Map params, Map ctx ->
            def pageName = params.page ?: params.target
            if(!pageName) return
            cb.pages[pageName] = com.pega.pegarules.pub.clipboard.ClipboardFactory.newPageList([])
        })

        // register Pega-like functions into the functionRegistry so expressions like
        // "@LengthOfPageList(Orders)" can be resolved at runtime by Step.evalParam
        registerFunction('LengthOfPageList', { Clipboard cb, String pagePath ->
            return SimulatorRunner.lengthOfPageList(cb, pagePath)
        })

        // Call a data transform registered in SimulatorRunner.datatransforms
        registerMethod('Call-Data-Transform', { Clipboard cb, Map params, Map ctx ->
            def name = params.name ?: params.datatransform
            def dt = datatransforms[name]
            if(dt instanceof Closure) return dt(cb, params, ctx)
            return null
        })

        // Call a decision table registered in SimulatorRunner.decisionTables
        registerMethod('Call-Decision-Table', { Clipboard cb, Map params, Map ctx ->
            def name = params.name ?: params.decisionTable
            def dt = decisionTables[name]
            if(dt instanceof Closure) return dt(cb, params, ctx)
            return null
        })

        // Call a rule utility (delegate to methodRegistry if present)
        registerMethod('Call-Rule-Utility', { Clipboard cb, Map params, Map ctx ->
            def name = params.name ?: params.rule
            def util = methodRegistry[name]
            if(util instanceof Closure) return util(cb, params, ctx)
            return null
        })

        // Simple connector/HTTP stub: place a stubbed response into target
        registerMethod('Call-Connector', { Clipboard cb, Map params, Map ctx ->
            def target = params.target ?: params.result
            def resp = [status: 'OK', data: params.payload ?: params.body ?: [:]]
            if(target) cb.set(target, resp)
            return resp
        })

        // Object operations: Obj-Save, Obj-Browse, Obj-Delete — provide simple in-memory stubs
        // Use a simulator-local in-memory store
        if(!SimulatorRunner.metaClass.hasProperty(SimulatorRunner, 'fakeStore')) SimulatorRunner.fakeStore = [:]
        registerMethod('Obj-Save', { Clipboard cb, Map params, Map ctx ->
            def id = params.id ?: UUID.randomUUID().toString()
            SimulatorRunner.fakeStore[id] = params.object ?: params.value ?: params.data
            return id
        })
        registerMethod('Obj-Browse', { Clipboard cb, Map params, Map ctx ->
            def id = params.id
            return SimulatorRunner.fakeStore[id]
        })
        registerMethod('Obj-Delete', { Clipboard cb, Map params, Map ctx ->
            def id = params.id
            SimulatorRunner.fakeStore.remove(id)
            return true
        })

        // Generic no-op handler for less-common methods: register several aliases to no-op
        ['Page-List','Page-Set','Page-RemoveAll','Page-RemoveByIndex','Append-Page','Page-Append','Page-Put'].each { name ->
            registerMethod(name, { Clipboard cb, Map params, Map ctx ->
                // best-effort map -> set semantics
                if(params.target || params.page) {
                    def tgt = params.target ?: params.page
                    if(params.value != null) cb.set(tgt, params.value)
                }
                return null
            })
        }

        // Call another activity by name or Activity instance
        registerMethod('Call-Activity', { Clipboard cb, Map params, Map ctx ->
            def aName = params.name ?: params.activity
            def actObj = null
            if(aName instanceof String) actObj = activityRegistry[aName]
            else if(aName instanceof Activity) actObj = aName
            else if(aName instanceof Closure) actObj = SimulatorRunner.activity('inline', aName)
            if(actObj instanceof Activity) {
                // run sub-activity with a fresh execCtx
                def exec = [env: ctx['__env'] ?: [:], className: params.className ?: ctx['__class'], paramPage: ctx['__paramPage']]
                actObj.run(cb, exec)
            }
        })

        // Call sub-activity (same as Call-Activity but expected to behave as a subroutine)
        registerMethod('Call-Sub-Activity', { Clipboard cb, Map params, Map ctx ->
            def aName = params.name ?: params.activity
            def actObj = null
            if(aName instanceof String) actObj = activityRegistry[aName]
            else if(aName instanceof Activity) actObj = aName
            else if(aName instanceof Closure) actObj = SimulatorRunner.activity('inline', aName)
            if(actObj instanceof Activity) {
                def exec = [env: ctx['__env'] ?: [:], className: params.className ?: ctx['__class'], paramPage: ctx['__paramPage']]
                actObj.run(cb, exec)
            }
        })

        // Control flow methods: Jump, Return, Stop
        registerMethod('Jump', { Clipboard cb, Map params, Map ctx ->
            def label = params.label ?: params.target ?: params.to
            // not fully supported; record intention and throw to allow Activity.run to handle
            ctx['__jump'] = label
            throw new ControlFlowException('jump', label, null)
        })

        registerMethod('Return', { Clipboard cb, Map params, Map ctx ->
            def val = params.value ?: params.returnValue
            throw new ControlFlowException('return', null, val)
        })

        registerMethod('Stop', { Clipboard cb, Map params, Map ctx ->
            throw new ControlFlowException('stop', null, null)
        })
    }
}

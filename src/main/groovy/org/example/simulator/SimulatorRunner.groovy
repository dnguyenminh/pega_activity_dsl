package org.example.simulator

class SimulatorRunner {
    static Map methodRegistry = [:]
    static Map whenRegistry = [:]
    static Map datatransforms = [:]
    static Map decisionTables = [:]
    // thread-global current paramPage used during runActivity to allow steps to mirror py* values
    static Map currentParamPage = null

    static { registerDefaults() }

    static void registerMethod(String name, Closure c) { methodRegistry[name]=c }
    static void registerWhen(String name, Closure c) { whenRegistry[name]=c }

    static Activity activity(String name, Closure c) {
        def a = new Activity(name)
        def code = c.rehydrate(a, a, a)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
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

    // NOTE: baseclass defaults are stored per-Clipboard instance (Clipboard.baseClassDefaults)

    private static void registerDefaults() {
        registerMethod('Property-Set', { Clipboard cb, Map params, Map ctx ->
            if(params.action == 'swap') {
                def page = params.page ?: 'Orders'
                def idx = ctx[params.indexVar ?: 'j'] as int
                def prop = params.property ?: 'amount'
                def list = cb.pages[page]
                // unwrap if someone stored a wrapped property as the page list
                if(list instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardProperty) list = ((com.pega.pegarules.pub.clipboard.SimpleClipboardProperty)list).getPropertyValue()
                // safely handle elements that may be SimpleClipboardPage or Map
                def left = list[idx]
                def right = list[idx+1]
                def getProp = { elem ->
                    try {
                        if(elem instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardPage) return elem.getAt(prop)
                        if(elem instanceof Map) return elem[prop]
                        return elem[prop]
                    } catch(Exception e) { return null }
                }
                def setProp = { elem, val ->
                    // for SimpleClipboardPage write directly into internal delegate to avoid meta-method dispatch
                    if(elem instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardPage) {
                        // unwrap if caller accidentally passed a ClipboardProperty
                        if(val instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) val = ((com.pega.pegarules.pub.clipboard.ClipboardProperty)val).getPropertyValue()
                        def tostore
                        if(val instanceof Map) tostore = new com.pega.pegarules.pub.clipboard.SimpleClipboardProperty(new com.pega.pegarules.pub.clipboard.SimpleClipboardPage((Map)val))
                        else if(val instanceof List) tostore = new com.pega.pegarules.pub.clipboard.SimpleClipboardProperty(((List)val).collect { it instanceof Map ? new com.pega.pegarules.pub.clipboard.SimpleClipboardPage((Map)it) : it })
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
            } else if(params.action == 'computeSize') {
                def page = params.page ?: 'Orders'
                def raw = cb.pages[page]
                if(raw instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardProperty) raw = ((com.pega.pegarules.pub.clipboard.SimpleClipboardProperty)raw).getPropertyValue()
                def size = (raw instanceof List) ? raw.size() : 0
                // expose size in the execution ctx so closures can read it
                ctx['__size'] = size
                // also mirror to paramPage if present
                if(ctx['__paramPage'] instanceof Map) ctx['__paramPage']['__size'] = size
            } else {
                cb.set(params.target ?: params.property, params.value)
            }
        })

        registerMethod('Page-New', { Clipboard cb, Map params, Map ctx -> cb.pages[params.page]=[] })
        registerMethod('Page-Copy', { Clipboard cb, Map params, Map ctx ->
            def src = cb.pages[params.source]
            if(src instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardPage) cb.pages[params.target] = ((com.pega.pegarules.pub.clipboard.SimpleClipboardPage)src).copy()
            else if(src instanceof Map) cb.pages[params.target] = cb.applyBaseDefaultsTo(((Map)src).clone() as Map)
            else if(src instanceof List) cb.pages[params.target] = src.collect { it instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardPage ? ((com.pega.pegarules.pub.clipboard.SimpleClipboardPage)it).copy() : it }
            else cb.pages[params.target] = src
        })
        registerMethod('Page-Remove', { Clipboard cb, Map params, Map ctx -> cb.pages.remove(params.page) })

        registerWhen('AmountGreaterThanNext', { Clipboard cb, Map ctx ->
            def j = ctx['j'] as int
            def ord = cb.pages['Orders']
            // unwrap if stored as a SimpleClipboardProperty wrapping a page-list
            if(ord instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardProperty) ord = ((com.pega.pegarules.pub.clipboard.SimpleClipboardProperty)ord).getPropertyValue()
            if(!(ord instanceof List)) return false
            def left = ord[j]
            def right = ord[j+1]
            def a = (left instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardPage) ? left.getBigDecimal('amount') : (left.amount as Number)
            def b = (right instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardPage) ? right.getBigDecimal('amount') : (right.amount as Number)
            return a > b
        })
    }
}

package org.example.simulator

class Clipboard {
    Map pages = [:]
    // Note: base-class defaults are defined on the Pega ClipboardPage class (com.pega...ClipboardPage.baseClassDefaults)

    Object get(String path) {
        if(!path) return null
        def parts = path.tokenize('.')
        def cur = pages
        parts.each { p ->
            if(cur == null) return null
            if(cur instanceof Map) cur = cur[p]
            else if(cur instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) cur = cur.getAt(p)
            else return null
        }
        // If this page represents a Pega-style list (pxObjClass == 'Code-Pega-List'), return its pxResults page-list by default
        try {
            try { println "DEBUG Clipboard.get: curClass=${cur?.getClass()?.name}, cur=${cur}" } catch(Exception ignore) {}
            if(cur instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) {
                def objClass = cur.getAt('pxObjClass')
                // if objClass is wrapped in a ClipboardProperty for any reason, unwrap it to compare
                try {
                    if(objClass instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) objClass = ((com.pega.pegarules.pub.clipboard.ClipboardProperty)objClass).getPropertyValue()
                } catch(Exception ignore) {}
                try { println "DEBUG Clipboard.get: detected objClass=${objClass?.getClass()?.name} -> ${objClass}" } catch(Exception ignore) {}
                if(objClass == 'Code-Pega-List') {
                    // Return the PageList property itself (do not blindly unwrap to a raw List).
                    // Use getProperty so callers receive a ClipboardProperty (PageList) which
                    // exposes page-list semantics.
                    def prop = cur.getProperty('pxResults')
                    try { println "DEBUG Clipboard.get: returning pxResults prop=${prop?.getClass()?.name}, innerType=${prop instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty ? ((com.pega.pegarules.pub.clipboard.ClipboardProperty)prop).getPropertyValue()?.getClass()?.name : 'N/A'}" } catch(Exception ignore) {}
                    return prop
                }
            }
        } catch(Exception ignored) {}
        return cur
    }
    

    void set(String path, Object value) {
        if(!path) return
        def parts = path.tokenize('.')
        if(parts.size() == 1) {
            def existed = pages.containsKey(parts[0])
            if(!existed && value instanceof Map) {
                pages[parts[0]] = applyBaseDefaultsTo((Map)value)
            } else if(!existed && value instanceof List) {
                // convert list of maps into list of pages and store as a page-list ClipboardProperty
                def lst = value.collect { it instanceof Map ? applyBaseDefaultsTo((Map)it) : it }
                pages[parts[0]] = com.pega.pegarules.pub.clipboard.ClipboardFactory.newPageList(lst)
            } else {
                pages[parts[0]] = value
            }
            return
        }
        def cur = pages
        def intermediates = parts[0..-2]
        intermediates.each { p ->
            // descend into Map or AbstractClipboardPage
            if(cur instanceof Map) {
                if(!(cur[p] instanceof Map) && !(cur[p] instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage)) cur[p] = [:]
                cur = cur[p]
            } else if(cur instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) {
                // descend into a page; keep cur as the page instance for final put
                cur = cur
            } else {
                // cannot descend further, create a Map slot
                cur[p] = [:]
                cur = cur[p]
            }
        }
        def last = parts[-1]
        // If cur is a Page instance, set the property on the page; otherwise treat as Map
        if(cur instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) {
            def existed = cur.getPropertyObject(last) != null
            if(!existed && value instanceof Map) {
                cur.putAt(last, applyBaseDefaultsTo((Map)value))
            } else if(!existed && value instanceof List) {
                def lst = value.collect { it instanceof Map ? applyBaseDefaultsTo((Map)it) : it }
                cur.putAt(last, com.pega.pegarules.pub.clipboard.ClipboardFactory.newPageList(lst))
            } else {
                cur.putAt(last, value)
            }
        } else {
            def existed = cur.containsKey(last)
            if(!existed && value instanceof Map) {
                cur[last] = applyBaseDefaultsTo((Map)value)
            } else if(!existed && value instanceof List) {
                def lst = value.collect { it instanceof Map ? applyBaseDefaultsTo((Map)it) : it }
                cur[last] = com.pega.pegarules.pub.clipboard.ClipboardFactory.newPageList(lst)
            } else {
                cur[last] = value
            }
        }
    }

    Map applyBaseDefaultsTo(Map page) {
        if(page == null) return null
        // create a SimpleClipboardPage merging defaults and provided map
        def defaults = com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults
    def outPage = com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((String)null)
        // copy defaults (if ClipboardPage) into outPage
        // Build a merged raw map of property names -> raw values, then construct a SimpleClipboardPage(map)
        def merged = [:]
        try {
            if(defaults instanceof com.pega.pegarules.pub.clipboard.ClipboardPage) {
                def cp = (com.pega.pegarules.pub.clipboard.ClipboardPage)defaults
                for(def k : (Iterable)cp.keySet()) {
                    try {
                        def prop = cp.getProperty(k)
                        if(prop == null) continue
                        merged[k] = prop.getPropertyValue()
                    } catch(Exception ignore) { }
                }
            } else if(defaults instanceof Map) {
                ((Map)defaults).each { k, v -> if(v != null) merged[k] = (v instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) ? ((com.pega.pegarules.pub.clipboard.ClipboardProperty)v).getPropertyValue() : v }
            }
        } catch(Exception ex) { /* ignore */ }
        // overlay provided entries (raw values) into merged
        page.each { k, v -> merged[k] = v }
        // debug: show merged raw map before constructing final page
        try {
            merged.each { kk, vv -> println "DEBUG applyBaseDefaultsTo: merged key=${kk}, type=${vv?.getClass()?.name}, value=${vv}" }
        } catch(Exception ignore) { }
    // construct a ClipboardPage from merged raw values using the factory (will wrap correctly and set pxObjClass)
    def finalPage = com.pega.pegarules.pub.clipboard.ClipboardFactory.newPage((Map)merged, (String)null)
        // add instance-level metaClass getProperty so Groovy dot-access (page.prop) resolves to raw values
        try {
            finalPage.metaClass.getProperty = { o, name ->
                try { println "DEBUG instance metaClass.getProperty called for name=${name}" } catch(Exception ignore) {}
                return o.getAt(name)
            }
            merged.each { kk, vv ->
                try {
                    def getter = 'get' + (kk[0].toUpperCase() + kk.substring(1))
                    finalPage.metaClass."${getter}" = { -> finalPage.getAt(kk) }
                } catch(Exception ignore) { }
            }
            finalPage.@delegate.each { kk, vv ->
                def inner = (vv instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) ? ((com.pega.pegarules.pub.clipboard.ClipboardProperty)vv).getPropertyValue() : vv
                println "DEBUG applyBaseDefaultsTo: finalPage delegate key=${kk}, valueType=${vv?.getClass()?.name}, inner=${inner}"
            }
        } catch(Exception ignore) { }
        return finalPage
    }
}

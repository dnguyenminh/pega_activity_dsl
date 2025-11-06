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
            else return null
        }
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
                pages[parts[0]] = new com.pega.pegarules.pub.clipboard.SimpleClipboardProperty(lst)
            } else {
                pages[parts[0]] = value
            }
            return
        }
        def cur = pages
        parts[0..-2].each { p -> if(!(cur[p] instanceof Map)) cur[p] = [:]; cur = cur[p] }
        def last = parts[-1]
        def existed = cur.containsKey(last)
        if(!existed && value instanceof Map) {
            cur[last] = applyBaseDefaultsTo((Map)value)
        } else if(!existed && value instanceof List) {
            // nested page-list: store as a ClipboardProperty wrapping a list of pages
            def lst = value.collect { it instanceof Map ? applyBaseDefaultsTo((Map)it) : it }
            cur[last] = new com.pega.pegarules.pub.clipboard.SimpleClipboardProperty(lst)
        } else {
            cur[last] = value
        }
    }

    Map applyBaseDefaultsTo(Map page) {
        if(page == null) return null
        // create a SimpleClipboardPage merging defaults and provided map
        def defaults = com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults
        def outPage = new com.pega.pegarules.pub.clipboard.SimpleClipboardPage()
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
        // construct a SimpleClipboardPage from merged raw values (SimpleClipboardPage(Map) will wrap correctly)
        def finalPage = new com.pega.pegarules.pub.clipboard.SimpleClipboardPage(merged)
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

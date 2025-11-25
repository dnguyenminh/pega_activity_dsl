package com.pega.pegarules.pub.clipboard

/** Thin wrapper representing a page group (map of pages). */
class PageGroup extends AbstractClipboardPage {
    PageGroup() {
        super()
    }

    PageGroup(Map m) {
        // Initialize base then store entries directly into the internal delegate
        // to avoid triggering metaClass property interceptors that may receive
        // non-String 'name' params and cause ClassCastException.
        super()
        if (m == null) return
        for (Map.Entry entry : m.entrySet()) {
            def key = (entry.getKey() == null) ? null : entry.getKey().toString()
            def val = entry.getValue()
            if (val instanceof Map) {
                // convert nested map to Page before storing
                this.@delegate.put(key, new Page((Map)val))
            } else if (val instanceof ClipboardPage || val instanceof ClipboardProperty) {
                // store as-is if already a clipboard construct
                this.@delegate.put(key, val)
            } else {
                // wrap raw values in a SimpleClipboardProperty to match AbstractClipboardPage expectations
                this.@delegate.put(key, new SimpleClipboardProperty(val))
            }
        }
    }

    PageGroup(List props) {
        super(props)
    }

    @Override
    String getName() {
        return "PageGroup"
    }
}

package com.pega.pegarules.pub.clipboard

/**
 * Compatibility alias used by older code: SimpleClipboardPage behaves like Page but
 * provides the constructors that other classes expect (no-arg, Map, List).
 */
class SimpleClipboardPage extends Page {
    SimpleClipboardPage() {
        super()
    }

    SimpleClipboardPage(Map m) {
        // delegate to Page(Map) which will call AbstractClipboardPage(Map)
        super((Map)m)
    }

    SimpleClipboardPage(List props) {
        super((List)props)
    }

    // helper constructor to match potential existing callsites
    SimpleClipboardPage(Object v) {
        super()
        if (v instanceof ClipboardPage) {
            try { this.copyFrom((ClipboardPage)v) } catch(Exception ignored) {}
            this.type = ClipboardPropertyType.PAGE
        }
    }
}

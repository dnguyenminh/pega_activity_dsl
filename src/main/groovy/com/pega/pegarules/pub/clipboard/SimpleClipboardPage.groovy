package com.pega.pegarules.pub.clipboard

/**
 * Compatibility alias used by older code: SimpleClipboardPage behaves like Page but
 * provides the constructors that other classes expect (no-arg, Map, List).
 * Reworked to extend AbstractClipboardPage so Page can extend SimpleClipboardPage
 * and callers receiving SimpleClipboardPage always get a ClipboardPage implementation.
 */
class SimpleClipboardPage extends AbstractClipboardPage implements ClipboardPage {
    SimpleClipboardPage() {
        super()
    }

    static {
        // SimpleClipboardPage.metaClass.getProperty = { o, name -> ... } removed as it conflicts with ClipboardPage interface
    }

    SimpleClipboardPage(Map m) {
        super((Map)m)
    }
    
    SimpleClipboardPage(List props) {
        super((List)props)
    }
    
    // avoid ambiguous Object constructor that can cause Groovy coercion errors in tests
    // keep a helper that accepts ClipboardPage explicitly
    SimpleClipboardPage(ClipboardPage p) {
        super()
        if (p != null) {
            this.copyFrom(p)
        }
    }

    // helper constructor to match potential existing callsites
    SimpleClipboardPage(Object v) {
        super()
        if (v instanceof ClipboardPage) {
            this.copyFrom((ClipboardPage)v)
        }
    }

    @Override
    Object getAt(Object k) {
        // println "MY_DEBUG: SimpleClipboardPage.getAt(Object) called with ${k}"
        def r = super.getAt(k)
        return _wrapResult(r)
    }

    Object getAt(String k) {
        // println "MY_DEBUG: SimpleClipboardPage.getAt(String) called with ${k}"
        def r = super.getAt(k)
        return _wrapResult(r)
    }

    private Object _wrapResult(Object r) {
        try {
            if (r instanceof SimpleClipboardPage) return r
            if (r instanceof Page) return new SimpleClipboardPage((ClipboardPage)r)
            if (r instanceof ClipboardPage) return new SimpleClipboardPage((ClipboardPage)r)
            if (r instanceof Map) return new SimpleClipboardPage((Map)r)
            if (_isClipboardProperty(r)) {
                def pv = _getPropertyValueSafe(r)
                if (pv instanceof ClipboardPage) return new SimpleClipboardPage((ClipboardPage)pv)
                if (pv instanceof Map) return new SimpleClipboardPage((Map)pv)
                return pv
            }
        } catch(Exception ignored) {}
        return r
    }

    @Override
    String getName() {
        // Delegate to AbstractClipboardPage's pageName — historically this returned null
        // but tests and Page expect a name to be preserved after rename; return pageName to
        // align with Page semantics and improve test stability.
        return super.getName()
    }
}

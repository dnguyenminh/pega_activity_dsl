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

    // Ensure bracket access returns normalized Page/List/Page values as tests expect.
    @Override
    Object getAt(Object k) {
        def key = (k == null) ? null : k.toString()
        // Fast-path: if delegate already stores a Page or a SimpleClipboardProperty wrapping a Page,
        // return a SimpleClipboardPage to satisfy callers that expect SimpleClipboardPage instances.
        try {
            def raw = this.@delegate.get(key)
            if (raw instanceof com.pega.pegarules.pub.clipboard.Page) {
                return new SimpleClipboardPage((ClipboardPage)raw) // Ensure it's the right classloader's Page
            }
            if (raw instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardProperty) {
                try {
                    def pv = ((com.pega.pegarules.pub.clipboard.SimpleClipboardProperty)raw).getPropertyValue()
                    if (pv instanceof com.pega.pegarules.pub.clipboard.Page) {
                        return new SimpleClipboardPage((ClipboardPage)pv) // Ensure it's the right classloader's Page
                    }
                } catch(Exception ignored) { /* fall through */ }
            }
        } catch(Exception ignored) { /* fall back to generic handling */ }
        // Delegate to AbstractClipboardPage.getAt so both implementations use identical unwrapping logic.
        def res = super.getAt(key)
        // If super returned a Page, convert it to SimpleClipboardPage so callers of this class get the expected type.
        if (res instanceof com.pega.pegarules.pub.clipboard.Page) {
            return new SimpleClipboardPage((ClipboardPage)res)
        }
        return res
    }

    @Override
    String getName() {
        // Delegate to AbstractClipboardPage's pageName — historically this returned null
        // but tests and Page expect a name to be preserved after rename; return pageName to
        // align with Page semantics and improve test stability.
        return super.getName()
    }
}

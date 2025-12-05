package com.pega.pegarules.pub.clipboard

/** Thin wrapper representing a page-valued property. */
class Page extends SimpleClipboardPage implements ClipboardPage {

    boolean isClipboardPage() { return true }

    @Override
    String getName() { return super.getName() }


    Page(Object v = null) {
        super()
        if (v instanceof ClipboardPage) {
            copyFromClipboardPageSafe((ClipboardPage)v)
        } else if (v instanceof Map) {
            this.putAll(v)
        }
    }

    // explicit typed constructors to avoid ambiguous bytecode for super(...) calls
    Page(Map m) {
        super(m)
    }

    Page(List props) {
        super(props)
    }
    Page(String name, Object v, ClipboardPropertyType type = ClipboardPropertyType.PAGE) { // 'type' parameter is now unused.
        super()
        try {
            if (v instanceof ClipboardPage) {
                copyFromClipboardPageSafe((ClipboardPage)v)
            } else if (v instanceof Map) {
                this.putAll((Map)v)
            } else if (v instanceof List) {
                // Each element in list may be a Map or ClipboardPage descriptor
                ((List)v).eachWithIndex { e, idx ->
                    if (e instanceof ClipboardPage) {
                        copyFromClipboardPageSafe((ClipboardPage)e)
                    } else if (e instanceof Map) {
                        this.putAll((Map)e)
                    } else {
                        // raw values appended to 'items'
                        def existing = this.getPropertyObject("items")
                        if (!(existing instanceof List)) existing = []
                        existing << e
                        this.putAt("items", existing)
                    }
                }
            }
        } catch(Exception ignored) {}
        this.pageName = name
    }

    Page(ClipboardPage p) {
        super()
        copyFromClipboardPageSafe(p)
    }

    Page(String name, ClipboardPage p) {
        super()
        if (p != null) {
            p.entrySet().each { entry ->
                this.putAt(entry.getKey(), entry.getValue())
            }
        }
        this.pageName = name
    }

    private void copyFromClipboardPageSafe(ClipboardPage src) {
        if (src == null) {
            return
        }

        src.entrySet().each { entry ->
            def key = entry.getKey()
            def val
            if (src instanceof AbstractClipboardPage) {
                val = ((AbstractClipboardPage)src).getPropertyObject(key)
            } else {
                try {
                    def propertyVal = src.getProperty(key)?.getPropertyValue()
                    val = propertyVal ?: entry.getValue()
                } catch(Exception ignored) {
                    val = entry.getValue()
                }
            }
            this.putAt(key, val)
        }
    }
}

package com.pega.pegarules.pub.clipboard

/** Thin wrapper representing a page-valued property. */
class Page extends AbstractClipboardPage {
    Page(Object v = null) {
        super()
        if (v instanceof ClipboardPage) {
            v.entrySet().each { entry ->
                this.putAt(entry.getKey(), entry.getValue())
            }
        } else if (v instanceof Map) {
            this.putAll(v)
        }
        this.type = ClipboardPropertyType.PAGE
    }

    // explicit typed constructors to avoid ambiguous bytecode for super(...) calls
    Page(Map m) {
        super(m)
    }

    Page(List props) {
        super(props)
    }
    Page(String name, Object v, ClipboardPropertyType type = ClipboardPropertyType.PAGE) {
        super()
        try {
            if (v instanceof Map) {
                def tmp = new SimpleClipboardPage((Map)v)
                this.copyFrom(tmp)
            } else if (v instanceof List) {
                def tmp = new SimpleClipboardPage((List)v)
                this.copyFrom(tmp)
            } else if (v instanceof ClipboardPage) {
                this.copyFrom((ClipboardPage)v)
            }
        } catch(Exception ignored) {}
        this.pageName = name
        this.type = type
    }

    Page(ClipboardPage p) {
        super()
        if (p != null) {
            p.entrySet().each { entry ->
                this.putAt(entry.getKey(), entry.getValue())
            }
        }
        this.type = ClipboardPropertyType.PAGE
    }

    Page(String name, ClipboardPage p) {
        super()
        if (p != null) {
            p.entrySet().each { entry ->
                this.putAt(entry.getKey(), entry.getValue())
            }
        }
        this.pageName = name
        this.type = ClipboardPropertyType.PAGE
    }
}

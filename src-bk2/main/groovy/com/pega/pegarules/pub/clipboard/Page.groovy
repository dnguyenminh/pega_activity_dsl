package com.pega.pegarules.pub.clipboard

/** Thin wrapper representing a page-valued property. */
class Page extends AbstractClipboardPage {
    Page(Object v = null) {
        // default construction path: start from empty page then copy from clipboard page if provided
        super()
        if (v instanceof ClipboardPage) {
            try { this.copyFrom((ClipboardPage)v) } catch(Exception ignored) {}
            this.type = ClipboardPropertyType.PAGE
        }
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
        if (p instanceof AbstractClipboardPage) {
            try { this.copyFrom(p) } catch(Exception ignored) {}
        }
        this.type = ClipboardPropertyType.PAGE
    }

    Page(String name, ClipboardPage p) {
        super()
        if (p instanceof AbstractClipboardPage) {
            try { this.copyFrom(p) } catch(Exception ignored) {}
        }
        this.pageName = name
        this.type = ClipboardPropertyType.PAGE
    }
}

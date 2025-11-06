package com.pega.pegarules.pub.clipboard

/** Thin wrapper representing a page-valued property. */
class Page extends SimpleClipboardProperty {
    Page(ClipboardPage p = null) {
        super(p)
        this.type = ClipboardPropertyType.PAGE
    }

    Page(String name, ClipboardPage p) {
        super(name, p, ClipboardPropertyType.PAGE)
    }
}

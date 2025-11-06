package com.pega.pegarules.pub.clipboard

/** Thin wrapper representing a page group (map of pages). */
class PageGroup extends SimpleClipboardProperty {
    PageGroup(Map<String, ClipboardPage> m = null) {
        super(m)
    }
}

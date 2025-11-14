package com.pega.pegarules.pub.clipboard

/** Thin wrapper representing a page group (map of pages). */
class PageGroup extends AbstractClipboardPage {
    PageGroup(Map<String, ClipboardPage> m = null) {
        super(m)
    }
}

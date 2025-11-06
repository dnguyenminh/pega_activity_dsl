package com.pega.pegarules.pub.clipboard

/**
 * Thin helper representing a Pega PageList. It's a ClipboardProperty whose
 * value is a List of SimpleClipboardPage instances. This lets tests and
 * callers write PageList([...]) style constructions.
 */
class PageList extends SimpleClipboardProperty {
    PageList(List pages = null) {
        // construct parent with converted list in one expression to satisfy JVM rules
        super((Object)(pages == null ? null : pages.collect { it instanceof Map ? new SimpleClipboardPage((Map)it) : it }))
    }
}

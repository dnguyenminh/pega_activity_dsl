package com.pega.pegarules.pub.clipboard

import groovy.transform.CompileStatic

/**
 * PageList represents a Pega page-list property. It's implemented as a
 * ClipboardProperty (extends SimpleClipboardProperty) whose property value is
 * a List of ClipboardPage instances. The factory should return this object
 * for pxResults so callers can treat pxResults as a first-class PageList.
 */
@CompileStatic
class PageList extends SimpleClipboardProperty {
    PageList(List pages = null) {
        super()
        // convert entries to ClipboardPage instances where appropriate
        def converted = (pages == null) ? [] : pages.collect { it ->
            if(it instanceof ClipboardPage) return it
            if(it instanceof Map) return new SimpleClipboardPage((Map)it)
            if(it instanceof List) return new SimpleClipboardPage((List)it)
            return it
        }
        // set internal value to the converted list
        this.setValue(converted)
    }

    // Convenience constructor to accept varargs for tests or callers
    PageList(Object... pages) {
        this(pages == null ? null : pages.toList())
    }
}

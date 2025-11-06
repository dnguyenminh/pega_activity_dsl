package com.pega.pegarules.pub.clipboard

/**
 * Small factory helpers to create clipboard pages and properties in a single place.
 * One of the parameters for properties accepts `ClipboardPropertyType` as requested.
 */
class ClipboardFactory {

    /** Create an empty ClipboardPage. pxObjClass is required and will be set on the created page. */
    static ClipboardPage newPage(String pxObjClass) {
        def p = new SimpleClipboardPage()
        def cls = pxObjClass ?: '@baseclass'
        p.put('pxObjClass', cls)
        return p
    }

    /** Create a ClipboardPage from a Map (delegates to SimpleClipboardPage(Map)). pxObjClass will be set on the created page. */
    static ClipboardPage newPage(Map m, String pxObjClass) {
        def p = new SimpleClipboardPage(m)
        def cls = pxObjClass ?: '@baseclass'
        p.put('pxObjClass', cls)
        return p
    }

    /** Create a ClipboardPage from a List of ClipboardProperty or Map-backed entries. pxObjClass will be set on the created page. */
    static ClipboardPage newPage(List props, String pxObjClass) {
        def p = new SimpleClipboardPage(props)
        def cls = pxObjClass ?: '@baseclass'
        p.put('pxObjClass', cls)
        return p
    }

    /** Create a PageList (ClipboardProperty wrapping a list of pages). */
    static PageList newPageList(List pages) {
        // Convert Map/List entries to SimpleClipboardPage but do not modify pxObjClass
        def converted = pages.collect { it ->
            if(it instanceof ClipboardPage) {
                return it
            }
            if(it instanceof Map) {
                return new SimpleClipboardPage((Map)it)
            }
            if(it instanceof List) {
                return new SimpleClipboardPage((List)it)
            }
            return it
        }
        return new PageList(converted)
    }

    /**
     * Create a ClipboardProperty with explicit name, value and type.
     * Example: newProperty('amount', 5, ClipboardPropertyType.INTEGER)
     */
    static ClipboardProperty newProperty(String name, Object value, ClipboardPropertyType type = ClipboardPropertyType.STRING) {
        return new SimpleClipboardProperty(name, value, type)
    }

 
}

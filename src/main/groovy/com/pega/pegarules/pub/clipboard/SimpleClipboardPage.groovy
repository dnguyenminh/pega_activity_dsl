package com.pega.pegarules.pub.clipboard

import java.math.BigDecimal
import java.util.Date

/**
 * Minimal implementation of ClipboardPage backed by a Map delegate.
 */
class SimpleClipboardPage implements ClipboardPage {
    // delegate stores propertyName -> ClipboardProperty
    private Map delegate = [:]
    List<String> messages = []
    String pageName = null

    // standard @baseclass properties (initialize with null/empty defaults)
    private static final Map STANDARD_BASECLASS_PROPS = [
        pxCreateDate: new SimpleClipboardProperty(null),
        pxUpdateDate: new SimpleClipboardProperty(null),
        pxCreateOperator: new SimpleClipboardProperty(null),
        pxUpdateOperator: new SimpleClipboardProperty(null),
    // default class for pages when not explicitly provided
    pxObjClass: new SimpleClipboardProperty('@baseclass'),
        pyLabel: new SimpleClipboardProperty(null),
        pyDescription: new SimpleClipboardProperty(null),
        pyWorkPage: new SimpleClipboardProperty(null)
    ]

    SimpleClipboardPage() {
        // initialize delegate with standard keys as property objects
        STANDARD_BASECLASS_PROPS.each { k, v ->
            // clone property objects so instances are independent
            this.@delegate.put(k, new SimpleClipboardProperty((v instanceof SimpleClipboardProperty) ? v.getPropertyValue() : v))
        }
    }

    // Ensure Groovy property access (page.prop) returns the unwrapped raw value by intercepting metaClass property access.
    static {
        SimpleClipboardPage.metaClass.getProperty = { o, name ->
            try {
                try { println "DEBUG metaClass.getProperty called for name=${name}" } catch(Exception ignored) {}
                return o.getAt(name)
            } catch(Exception e) {
                try { println "DEBUG metaClass.getProperty fallback for name=${name}, err=${e.message}" } catch(Exception ignored) {}
                return o.metaClass.getProperty(o, name)
            }
        }
    }

    SimpleClipboardPage(Map m) {
        // start from base props then overlay provided map values
        STANDARD_BASECLASS_PROPS.each { k, v -> this.@delegate.put(k, new SimpleClipboardProperty((v instanceof SimpleClipboardProperty) ? v.getPropertyValue() : v)) }
        // convert entries in m into properties
        m.each { k, v ->
            try {
                println "DEBUG SimpleClipboardPage(Map): incoming key=${k}, type=${v?.getClass()?.name}, value=${v}"
            } catch(Exception ignore) {}
            if(v instanceof Map) delegate.put(k, new SimpleClipboardProperty(new SimpleClipboardPage((Map)v)))
            else if(v instanceof List) {
                // convert list elements which are maps into pages
                def lst = v.collect { it instanceof Map ? new SimpleClipboardPage((Map)it) : it }
                this.@delegate.put(k, new SimpleClipboardProperty(lst))
            } else {
                this.@delegate.put(k, new SimpleClipboardProperty(v))
            }
        }
    }

    /** Accept a List of ClipboardProperty objects (Page constructed from explicit property descriptors).
     * Example usage: new SimpleClipboardPage([ new SimpleClipboardProperty('amount',5), new SimpleClipboardProperty('id','a') ])
     */
    SimpleClipboardPage(List props) {
        // initialize with standard baseclass properties
        STANDARD_BASECLASS_PROPS.each { k, v -> this.@delegate.put(k, new SimpleClipboardProperty((v instanceof SimpleClipboardProperty) ? v.getPropertyValue() : v)) }
        if(props == null) return
        props.each { entry ->
            try {
                if(entry instanceof ClipboardProperty) {
                    def propName = entry.getName()
                    if(propName) this.@delegate.put(propName, entry)
                    else {
                        // if name missing, attempt to use string key or skip
                        // fall back to index-based key
                        this.@delegate.put(UUID.randomUUID().toString(), entry)
                    }
                } else if(entry instanceof Map) {
                    // map-style element with one entry -> name:value
                    if(((Map)entry).size() == 1) {
                        def k = ((Map)entry).keySet().iterator().next()
                        def v = ((Map)entry).get(k)
                        if(v instanceof Map) this.@delegate.put(k, new SimpleClipboardProperty(new SimpleClipboardPage((Map)v)))
                        else if(v instanceof List) this.@delegate.put(k, new SimpleClipboardProperty(((List)v).collect { it instanceof Map ? new SimpleClipboardPage((Map)it) : it }))
                        else this.@delegate.put(k, new SimpleClipboardProperty(v))
                    }
                }
            } catch(Exception e) {
                // ignore malformed entries
            }
        }
    }

    // internal delegate map used for storage; implement Map methods required by the interface
    int size() { return delegate.size() }
    boolean isEmpty() { return delegate.isEmpty() }
    boolean containsKey(Object k) { return delegate.containsKey(k) }
    boolean containsValue(Object v) {
        return delegate.values().any { p -> (p instanceof ClipboardProperty) ? p.getPropertyValue()==v : p==v }
    }
    // Map-like get: return property value (string) for compatibility with ClipboardPage interface
    String get(Object k) {
        def p = delegate.get(k)
        if(p == null) return null
        if(p instanceof ClipboardProperty) return ((ClipboardProperty)p).getStringValue()
        return p == null ? null : p.toString()
    }
    // support bracket get and set: page['prop'] and page['prop']=val
    Object getAt(Object k) {
        def p = this.@delegate.get(k)
        return _unwrapPropertyValue(p)
    }
    // put: accept raw value and wrap into ClipboardProperty; return previous value as string
    String put(Object k, Object v) {
        def prev = delegate.get(k)
        def tostore
        if(v instanceof Map) tostore = new SimpleClipboardProperty(new SimpleClipboardPage((Map)v))
        else if(v instanceof List) tostore = new SimpleClipboardProperty(v.collect { it instanceof Map ? new SimpleClipboardPage((Map)it) : it })
        else if(v instanceof ClipboardProperty) tostore = v
        else tostore = new SimpleClipboardProperty(v)
        try { println "DEBUG SimpleClipboardPage.put(Object,Object): key=${k}, incomingType=${v?.getClass()?.name}, tostoreInner=${(tostore instanceof ClipboardProperty)?((ClipboardProperty)tostore).getPropertyValue():tostore}" } catch(Exception ignore) {}
        this.@delegate[k] = tostore
        if(prev instanceof ClipboardProperty) return ((ClipboardProperty)prev).getStringValue()
        return prev == null ? null : prev.toString()
    }
    void putAt(Object k, Object v) {
        def tostore
        if(v instanceof Map) tostore = new SimpleClipboardProperty(new SimpleClipboardPage((Map)v))
        else if(v instanceof List) tostore = new SimpleClipboardProperty(v.collect { it instanceof Map ? new SimpleClipboardPage((Map)it) : it })
        else if(v instanceof ClipboardProperty) tostore = v
        else tostore = new SimpleClipboardProperty(v)
        // debug: log when setting base-class defaults
        try {
            println "DEBUG SimpleClipboardPage.putAt: key=${k}, incomingType=${v?.getClass()?.name}, tostoreType=${tostore?.getClass()?.name}, tostoreInner=${(tostore instanceof ClipboardProperty)?((ClipboardProperty)tostore).getPropertyValue():tostore}"
        } catch(Exception ignore) { }
        this.@delegate.put(k, tostore)
    }
    void putAll(Map m) {
        // use this.putAt to avoid dispatching to the String-specific put(String,String) overload
        m.each { k, v -> this.putAt(k, v) }
    }
    void clear() { delegate.clear() }
    Set keySet() { return delegate.keySet() }
    Collection values() { return delegate.values().collect { p -> (p instanceof ClipboardProperty) ? p.getPropertyValue() : p } }
    Set entrySet() { return delegate.entrySet() }

    // --- ClipboardPage methods ---
    void addMessage(String aMessage) { messages << aMessage }
    void addMessage(String aMessage, String aProperty) { messages << aMessage }
    void addMessage(String aMessage, String aProperty, int aSeverity) { messages << aMessage }
    void clearMessages() { messages.clear() }
    Iterator getMessagesAll() { return messages.iterator() }
    boolean hasMessages() { return !messages.isEmpty() }

    // Return a ClipboardProperty object as required by the interface.
    // If the stored value is already a ClipboardProperty, return it.
    // Otherwise, wrap the raw value in a SimpleClipboardProperty.
    ClipboardProperty getProperty(String aReference) {
        def p = delegate.get(aReference)
        if(p instanceof ClipboardProperty) return (ClipboardProperty)p
        if(p == null) return null
        return new SimpleClipboardProperty(_unwrapPropertyValue(p))
    }

    // helper: return the raw unwrapped value when callers need it
    Object getPropertyObject(String aReference) {
        def p = delegate.get(aReference)
        if(p == null) return null
        return _unwrapPropertyValue(p)
    }
    String getString(String aReference) { def p = delegate.get(aReference); return p==null?null:((p instanceof ClipboardProperty)?((ClipboardProperty)p).getStringValue():p.toString()) }
    String put(String aPropertyName, String aValue) {
        // Avoid delegating to the overloaded put(Object,Object) to prevent recursion
        def prev = this.@delegate.get(aPropertyName)
        def tostore = new SimpleClipboardProperty(aValue)
        try { println "DEBUG SimpleClipboardPage.put(String,String): key=${aPropertyName}, value=${aValue}, tostoreInner=${tostore.getPropertyValue()}" } catch(Exception ignore) {}
        this.@delegate[aPropertyName] = tostore
        if(prev instanceof ClipboardProperty) return ((ClipboardProperty)prev).getStringValue()
        return prev == null ? null : prev.toString()
    }
    String putString(String aPropertyName, String aValue) {
        return put(aPropertyName, aValue)
    }
    void clearValue(String aReference) { delegate.remove(aReference) }
    ClipboardProperty removeProperty(String aReference) { def prev = delegate.remove(aReference); return (ClipboardProperty)prev }
    String remove(Object aReference) { def prev = delegate.remove(aReference); if(prev instanceof ClipboardProperty) return ((ClipboardProperty)prev).getStringValue(); return prev==null?null:prev.toString() }

    String getJSON(boolean aEncode) { return delegate.toString() }
    void adoptJSONObject(Object aJO) { /* no-op */ }
    String getXML(boolean aEncode) { return delegate.toString() }
    String getXML(int aOptions) { return delegate.toString() }
    String getXML(String aPageName, int aOptions) { return delegate.toString() }
    void adoptXMLForm(String aXMLForm, int aOptions) { /* no-op */ }

    ClipboardPage copy() {
        def m = [:]
        delegate.each { k, v ->
            if(v instanceof ClipboardProperty) m[k] = ((ClipboardProperty)v).getPropertyValue()
            else m[k] = v
        }
        return new SimpleClipboardPage(m)
    }
    void copyTo(ClipboardPage aDestPage) { if(aDestPage instanceof SimpleClipboardPage) { ((SimpleClipboardPage)aDestPage).delegate.clear(); ((SimpleClipboardPage)aDestPage).delegate.putAll(delegate) } }
    void copyFrom(ClipboardPage aSourcePage) { if(aSourcePage instanceof SimpleClipboardPage) {
            ((SimpleClipboardPage)aSourcePage).delegate.each { k, v -> delegate[k] = v }
        } }
    void rename(String aNewName) { this.pageName = aNewName }
    void replace(ClipboardPage aSourcePage) { delegate.clear(); if(aSourcePage instanceof SimpleClipboardPage) delegate.putAll(((SimpleClipboardPage)aSourcePage).delegate) }

    // allow dot-access like page.prop to return the underlying property value
    def propertyMissing(String name) {
        def p = this.@delegate.get(name)
        return _unwrapPropertyValue(p)
    }

    private Object _unwrapPropertyValue(Object p) {
        if(p == null) return null
        try {
            while(p instanceof ClipboardProperty) {
                p = ((ClipboardProperty)p).getPropertyValue()
            }
        } catch(Exception e) { /* ignore and return raw */ }
        return p
    }

    // intercept generic property assignments (e.g. page.prop = val) to ensure proper wrapping
    void setProperty(String name, Object value) {
        // wrap and store directly into the internal delegate to avoid meta-method dispatch
        def tostore
        if(value instanceof Map) tostore = new SimpleClipboardProperty(new SimpleClipboardPage((Map)value))
        else if(value instanceof List) tostore = new SimpleClipboardProperty(value.collect { it instanceof Map ? new SimpleClipboardPage((Map)it) : it })
        else if(value instanceof ClipboardProperty) tostore = value
        else tostore = new SimpleClipboardProperty(value)
        this.@delegate.put(name, tostore)
    }

    boolean isEmbedded() { return false }
    boolean isValid() { return true }
    void setValue(String aReference, Object aValue) { put(aReference, aValue) }
    BigDecimal getBigDecimal(String aReference) {
        def v = _unwrapPropertyValue(delegate.get(aReference))
        if (v == null) return null
        try {
            return (v instanceof BigDecimal) ? (BigDecimal)v : new BigDecimal(v.toString())
        } catch(Exception e) {
            return null
        }
    }
    boolean getBoolean(String aReference) {
        def v = _unwrapPropertyValue(delegate.get(aReference))
        return v as boolean
    }
    Date getDate(String aReference) { return null }
    void putProperty(ClipboardProperty aProperty) { /* no-op */ }
    Object getEntryHandle(String aPropertyReference) { return null }
    String getClassName() { return 'Data-Generic' }
    String getName() { return pageName }
    boolean isReadOnly() { return false }
    Collection<String> getMessages() { return messages }
    boolean isJavaPage() { return false }
    ClipboardPage copy(ClipboardPage aPage) { copyTo(aPage); return aPage }
    void removeFromClipboard() { delegate.clear() }
}

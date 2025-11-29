package com.pega.pegarules.pub.clipboard

import java.math.BigDecimal
import java.util.Date

/**
 * Minimal implementation of ClipboardPage backed by a Map delegate.
 */
abstract class AbstractClipboardPage implements ClipboardPage {

    protected Map delegate
    List<String> messages = []
    String pageName = null

    // standard @baseclass properties (initialize with null/empty defaults)
    // store raw default values here; we'll wrap into Page objects at instance construction time
    private static final Map STANDARD_BASECLASS_PROPS = [
        pxCreateDate: null,
        pxUpdateDate: null,
        pxCreateOperator: null,
        pxUpdateOperator: null,
        // default class for pages when not explicitly provided
        pxObjClass: '@baseclass',
        pyLabel: null,
        pyDescription: null,
        pyWorkPage: null
    ]

    AbstractClipboardPage() {
        this.@delegate = [:] // Initialize here
        // initialize delegate with standard keys as property objects
        STANDARD_BASECLASS_PROPS.each { k, v ->
            this.putAt(k, v)
        }
    }

    // Ensure Groovy property access (page.prop) returns the unwrapped raw value by intercepting metaClass property access.
    // Ensure Groovy property access (page.prop) returns the unwrapped raw value by intercepting metaClass property access.
    static {
        // AbstractClipboardPage.metaClass.getProperty = { o, name -> ... } removed as it conflicts with ClipboardPage interface
    }

    AbstractClipboardPage(Map m) {
        this.@delegate = [:] // Initialize here
        // start from base props then overlay provided map values
        STANDARD_BASECLASS_PROPS.each { k, v ->
            putAt(k, v)
        }
        // convert entries in m into properties (coerce keys to String to avoid ClassCastException)
        if (m != null) {
            for (Map.Entry entry : m.entrySet()) {
                def key = (entry.getKey() == null) ? null : entry.getKey().toString()
                putAt(key, entry.getValue())
            }
        }
    }

    /**
     * Construct page from a List of descriptors.
     * Common callers use a List of Maps like: [ [c:3], [d:4] ] which should yield properties c and d.
     */
    AbstractClipboardPage(List props) {
        this.@delegate = [:] // Initialize here
        // initialize baseclass defaults
        STANDARD_BASECLASS_PROPS.each { k, v -> this.putAt(k, v) } // Use putAt for consistency
        if (props == null) return

        // Each element may be a Map of entries, a ClipboardPage, or a raw value.
        props.eachWithIndex { e, idx ->
            if (e instanceof Map) {
                // each map may contain one or more property entries; reuse putAll to ensure proper wrapping
                this.putAll((Map)e)
            } else if (e instanceof ClipboardPage) {
                // copy entries from the provided page
                e.entrySet().each { entry ->
                    this.putAt(entry.getKey(), entry.getValue())
                }
            } else if (e instanceof ClipboardProperty) {
                // best-effort: store the property value under a generated key
                def key = "item" + idx
                this.putAt(key, ((ClipboardProperty)e).getPropertyValue())
            } else {
                // raw values: append into an 'items' list property
                def existing = this.getPropertyObject("items")
                if (!(existing instanceof List)) existing = []
                existing << e
                this.putAt("items", existing)
            }
        }
    }

    // Helper: recursively unwrap SimpleClipboardProperty and convert Maps/ClipboardPage to Page, Lists to List<Page>
    private static Object _deepUnwrapAndConvert(Object v) {
        def unwrapped = v
        // Fully unwrap any ClipboardProperty-like object to its raw value (handle classloader mismatches).
        while (_isClipboardProperty(unwrapped)) {
            unwrapped = _getPropertyValueSafe(unwrapped)
        }
    
        // If it's already a ClipboardPage (including Page), convert to Page for propertyObject flows
        if (unwrapped instanceof ClipboardPage) {
            return new Page((ClipboardPage)unwrapped)
        }

        if (unwrapped instanceof Map) {
            return new Page((Map)unwrapped)
        }
        if (unwrapped instanceof List) {
            return ((List)unwrapped).collect { elem ->
                // Recursively convert elements in the list
                _deepUnwrapAndConvert(elem)
            }
        }
        return unwrapped
    }

    /** Accept a List of ClipboardProperty objects (Page constructed from explicit property descriptors).
    * Example usage: new AbstractClipboardPage([ new Page('amount',5), new Page('id','a') ])
     */

    // internal delegate map used for storage; implement Map methods required by the interface
    int size() { return this.@delegate.size() }
    boolean isEmpty() { return this.@delegate.isEmpty() }
    boolean containsKey(Object k) { return this.@delegate.containsKey(k) }
    boolean containsValue(Object v) {
        return this.@delegate.values().any { p -> (p instanceof ClipboardProperty) ? p.getPropertyValue()==v : p==v }
    }
    // Map-like get: return property value (string) for compatibility with ClipboardPage interface
    String get(Object k) {
        def key = (k == null) ? null : k.toString()
        def p = this.@delegate.get(key)
        if(p == null) return null
        if(p instanceof ClipboardProperty) return ((ClipboardProperty)p).getStringValue()
        return p == null ? null : p.toString()
    }
    // support bracket get and set: page['prop'] and page['prop']=val
    Object getAt(Object k) {
        // System.out.println("AGGRESSIVE_DEBUG: AbstractClipboardPage.getAt called with " + k)
        def key = (k == null) ? null : k.toString()
        def obj = getPropertyObject(key)
        if (obj == null) return null
        try {
            if (obj instanceof SimpleClipboardPage) return obj
            if (obj instanceof Page) return new SimpleClipboardPage((ClipboardPage)obj)
            if (obj instanceof ClipboardPage) return new SimpleClipboardPage((ClipboardPage)obj)
            if (obj instanceof Map) return new SimpleClipboardPage((Map)obj)
            if (obj instanceof List) {
                return ((List)obj).collect { e ->
                    if (e instanceof SimpleClipboardPage) return e
                    if (e instanceof Page) return new SimpleClipboardPage((ClipboardPage)e)
                    if (e instanceof ClipboardPage) return new SimpleClipboardPage((ClipboardPage)e)
                    if (e instanceof Map) return new SimpleClipboardPage((Map)e)
                    return e
                }
            }
        } catch(Exception ignored) {}
        return obj
    }
    // Helper to store a value, handling unwrapping and conversion
    private void _storeValue(Object k, Object v) {
        def key = (k == null) ? null : k.toString()
    
        // If caller passed a ClipboardProperty-like object, try to unwrap it first to avoid
        // storing a property wrapper around a Page/value.
        def valueToProcess = v
        if (_isClipboardProperty(valueToProcess)) {
            try { valueToProcess = _getPropertyValueSafe(valueToProcess) } catch(Exception ignored) { /* continue with original */ }
        }
    
        def finalValue = _deepUnwrapAndConvert(valueToProcess)
    
        // Defensive: if finalValue itself is a ClipboardProperty-like wrapper (edge cases),
        // unwrap one more layer so we store the underlying Page/value.
        if (_isClipboardProperty(finalValue)) {
            try { finalValue = _getPropertyValueSafe(finalValue) } catch(Exception ignored) { /* ignore */ }
            finalValue = _deepUnwrapAndConvert(finalValue)
        }

        // If a Map slipped through, convert to Page explicitly.
        if (finalValue instanceof Map) {
            finalValue = new SimpleClipboardPage((Map)finalValue)
        }

        // If the value is a ClipboardPage (Page or similar), store it directly as Page to preserve identity
        if (finalValue instanceof ClipboardPage) {
            try { finalValue = new SimpleClipboardPage((ClipboardPage)finalValue) } catch(Exception ignored) {}
            this.@delegate.put(key, finalValue)
            return
        }
        // If the finalValue is already a Page-like SimpleClipboardPage put it directly
        try {
            if (finalValue instanceof SimpleClipboardPage || finalValue instanceof AbstractClipboardPage) {
                this.@delegate.put(key, finalValue)
                return
            }
        } catch(Exception ignored) {}
        this.@delegate.put(key, new SimpleClipboardProperty(finalValue))
    }

    // put: accept raw value and wrap into ClipboardProperty; normalize Map/List/ClipboardPage into Page/List<Page>
    String put(Object k, Object v) {
        def key = (k == null) ? null : k.toString()
        def prev = this.@delegate.get(key)
        _storeValue(k, v) // Use the new helper (key normalized there)
        if(prev instanceof ClipboardProperty) return ((ClipboardProperty)prev).getStringValue()
        return prev == null ? null : prev.toString()
    }
    void putAt(Object k, Object v) {
        _storeValue(k, v) // Use the new helper (key normalized there)
    }
    void putAll(Map m) {
        // use a simple for-loop and explicit entry handling to avoid closure/property resolution issues
        // Coerce keys to String to avoid runtime property-set casting errors when keys are non-String objects.
        if (m == null) return
        for (Map.Entry entry : m.entrySet()) {
            def key = (entry.getKey() == null) ? null : entry.getKey().toString()
            this.putAt(key, entry.getValue())
        }
    }
    void clear() { this.@delegate.clear() }
    Set keySet() { return this.@delegate.keySet() }
    Collection values() { return this.@delegate.values().collect { p -> (p instanceof ClipboardProperty) ? p.getPropertyValue() : p } }
    Set entrySet() { return this.@delegate.entrySet() }

    // --- ClipboardPage methods ---
    void addMessage(String aMessage) { messages << aMessage }
    void addMessage(String aMessage, String aProperty) { messages << aMessage }
    void addMessage(String aMessage, String aProperty, int aSeverity) { messages << aMessage }
    void clearMessages() { messages.clear() }
    Iterator getMessagesAll() { return messages.iterator() }
    boolean hasMessages() { return !messages.isEmpty() }

    // Return a ClipboardProperty object as required by the interface.
    // If the stored value is already a ClipboardProperty, return it.
    // Otherwise, wrap the raw value in a SimpleClipboardProperty so the
    // returned object implements ClipboardProperty (correct interface).
    ClipboardProperty getProperty(String aReference) {
        def p = this.@delegate.get(aReference)
        if (p instanceof ClipboardProperty) return (ClipboardProperty)p
        if (p == null) return null
     
        // Unwrap any nested ClipboardProperty layers and coerce Map/List -> Page(s)
        def resolved = _unwrapPropertyValue(p)
     
        // If the resolved value is already a ClipboardPage, wrap it in a SimpleClipboardProperty
        // (SimpleClipboardProperty.getPageValue handles Map -> Page conversion internally).
        return new SimpleClipboardProperty(resolved)
    }

Object getPropertyObject(String aReference) {
    def key = (aReference == null) ? null : aReference.toString()
    def p = this.@delegate.get(key)
    if (p == null) return null

    // Unwrap known wrapper types and convert Map/ClipboardPage -> Page
    def resolved = _unwrapPropertyValue(p)

    // Defensive: if resolved is still a ClipboardProperty-like wrapper, unwrap iteratively.
    try {
        while (_isClipboardProperty(resolved)) {
            resolved = _getPropertyValueSafe(resolved)
            resolved = _deepUnwrapAndConvert(resolved)
        }
    } catch(Exception ignored) { /* best-effort: return whatever we have */ }

    // Convert SimpleClipboardPage/AbstractClipboardPage to Page for callers that expect Page instances
    try {
        if (resolved instanceof SimpleClipboardPage || resolved instanceof AbstractClipboardPage) {
            return new SimpleClipboardProperty(new Page((ClipboardPage)resolved))
        }
    } catch(Exception ignored) {}
    return resolved
}
    String getString(String aReference) { def key = (aReference == null) ? null : aReference.toString(); def p = this.@delegate.get(key); return p==null?null:((p instanceof ClipboardProperty)?((ClipboardProperty)p).getStringValue():p.toString()) }
    String put(String aPropertyName, String aValue) {
        // Avoid delegating to the overloaded put(Object,Object) to prevent recursion
        def prev = this.@delegate.get(aPropertyName)
        def tostore = new SimpleClipboardProperty(aValue)
        this.@delegate[aPropertyName] = tostore
        if(prev instanceof ClipboardProperty) return ((ClipboardProperty)prev).getStringValue()
        return prev == null ? null : prev.toString()
    }
    String putString(String aPropertyName, String aValue) {
        return put(aPropertyName, aValue)
    }
    void clearValue(String aReference) { this.@delegate.remove(aReference) }
    ClipboardProperty removeProperty(String aReference) { def prev = this.@delegate.remove(aReference); return (ClipboardProperty)prev }
    String remove(Object aReference) { def prev = this.@delegate.remove(aReference); if(prev instanceof ClipboardProperty) return ((ClipboardProperty)prev).getStringValue(); return prev==null?null:prev.toString() }

    String getJSON(boolean aEncode) { return this.@delegate.toString() }
    void adoptJSONObject(Object aJO) { /* no-op */ }
    String getXML(boolean aEncode) { return this.@delegate.toString() }
    String getXML(int aOptions) { return this.@delegate.toString() }
    String getXML(String aPageName, int aOptions) { return this.@delegate.toString() }
    void adoptXMLForm(String aXMLForm, int aOptions) { /* no-op */ }

    ClipboardPage copy() {
        def newPage = new Page()
        this.@delegate.each { k, v ->
            def unwrappedValue = _unwrapPropertyValue(v)
            newPage.put(k, unwrappedValue)
        }
        return newPage
    }
    void copyTo(ClipboardPage aDestPage) {
        if (aDestPage instanceof AbstractClipboardPage) {
            aDestPage.clear()
            this.@delegate.each { k, v ->
                def unwrappedValue = _unwrapPropertyValue(v)
                aDestPage.put(k, unwrappedValue)
            }
        }
    }
    void copyFrom(ClipboardPage aSourcePage) {
        if (aSourcePage instanceof AbstractClipboardPage) {
            this.@delegate.clear()
            ((AbstractClipboardPage)aSourcePage).@delegate.each { k, v ->
                def unwrappedValue = _unwrapPropertyValue(v)
                this.put(k, unwrappedValue)
            }
        }
    }
    void rename(String aNewName) { this.pageName = aNewName }
    void replace(ClipboardPage aSourcePage) {
        // Clear current content and copy entries from the source page using public API to ensure
        // proper wrapping/normalization of values.
        this.clear()
        if (aSourcePage == null) return
        if (aSourcePage instanceof AbstractClipboardPage) {
            // Source has internal delegate map we can iterate safely and normalize values via put(...)
            ((AbstractClipboardPage)aSourcePage).@delegate.each { k, v ->
                this.put(k, _unwrapPropertyValue(v))
            }
        } else {
            // Generic ClipboardPage: iterate its entrySet and use public put to normalize values
            try {
                aSourcePage.entrySet().each { entry ->
                    this.put(entry.getKey(), entry.getValue())
                }
            } catch(Exception ignored) {
                // best-effort fallback: if entrySet isn't available or fails, do nothing
            }
        }
    }

    // allow dot-access like page.prop to return the underlying property value
    def propertyMissing(String name) {
        // Use getAt to ensure consistent unwrapping semantics with bracket access.
        return getAt(name)
    }

    protected Object _unwrapPropertyValue(Object p) {
        if (p == null) return null
        // If the stored object is already a Page instance, return it directly to preserve identity.
            try {
                if (p instanceof Page) {
                    return p
                }
            } catch(Exception ignored) { /* defensive for classloader edge-cases */ }
        // If it's any ClipboardPage (but not Page), convert to a Page to provide consistent type.
        try {
            if (p instanceof ClipboardPage) {
                return new SimpleClipboardPage((ClipboardPage)p)
            }
        } catch(Exception ignored) { /* continue with fallback */ }
        if (_isClipboardProperty(p)) {
            try {
                return _deepUnwrapAndConvert(_getPropertyValueSafe(p))
            } catch(Exception ignored) {
                return _deepUnwrapAndConvert(p)
            }
        }
        return _deepUnwrapAndConvert(p)
    }

    // Helper: Try to produce a SimpleClipboardPage from a page-like object without
    // casting to ClipboardPage directly (which may fail across classloader boundaries).
    protected SimpleClipboardPage _toSimpleClipboardPageSafe(Object obj) {
        if (obj == null) return null
        // If it's a native ClipboardPage, this is the easiest path (try to cast safely)
        try {
            if (obj instanceof ClipboardPage) {
                return new SimpleClipboardPage((ClipboardPage)obj)
            }
        } catch (Exception ignored) { /* classloader cast fallback below */ }
        // If the object appears to be a Page by name, attempt to read its entries reflectively
        try {
            def m = [:]
            def es = obj.getClass().getMethod('entrySet').invoke(obj)
            if (es != null) {
                es.each { e ->
                    try {
                        // entry may be a Map.Entry or similar; attempt to access key/value
                        def k = e.getKey ? e.getKey() : (e.key ?: null)
                        def v = e.getValue ? e.getValue() : (e.value ?: null)
                        m[k == null ? null : k.toString()] = v
                    } catch (Exception inner) { /* best-effort; skip problematic entries */ }
                }
            /* debug prints removed */
                return new SimpleClipboardPage((Map)m)
            }
        } catch (Exception ignored) { /* ignore and continue */ }
        // Last ditch: if it's a Map, use it directly
        try {
            if (obj instanceof Map) return new SimpleClipboardPage((Map)obj)
        } catch (Exception ignored) { /* ignore */ }
        return null
    }

    private static boolean isPageInstance(Object obj) {
        if (obj == null) return false
        if (obj instanceof ClipboardPage) return true
        // Extreme defensive check for classloader issues
        Closure checkClassName = { it ->
            try {
                return it.class.name == 'com.pega.pegarules.pub.clipboard.Page'
            } catch (Exception e) {
                return false
            }
        }
        return checkClassName(obj)
    }

    private static boolean isListOfPageInstances(List list) {
        for (def elem : list) {
            if (elem != null && !isPageInstance(elem)) {
                return false
            }
        }
        return true
    }

    // Detect clipboard-property-like objects even across classloader boundaries by
    // checking for the interface first then falling back to reflection for the
    // presence of a getPropertyValue method.
    protected static boolean _isClipboardProperty(Object o) {
        if (o == null) return false
        try {
            if (o instanceof ClipboardProperty) return true
        } catch(Exception ignored) { /* classloader mismatch */ }
        try {
            return o.getClass().getMethod('getPropertyValue') != null
        } catch(Exception ignored) {
            return false
        }
    }

    // Safely obtain property value via interface or reflection.
    private static Object _getPropertyValueSafe(Object o) {
        if (o == null) return null
        try {
            if (o instanceof ClipboardProperty) {
                return ((ClipboardProperty)o).getPropertyValue()
            }
        } catch(Exception ignored) { /* continue to reflection fallback */ }
        try {
            def m = o.getClass().getMethod('getPropertyValue')
            return m.invoke(o)
        } catch(Exception ignored) {
            return o
        }
    }



    boolean isEmbedded() { return false }
    boolean isValid() { return true }
    void setValue(String aReference, Object aValue) { put(aReference, aValue) }
    BigDecimal getBigDecimal(String aReference) {
        def v = _unwrapPropertyValue(this.@delegate.get(aReference))
        if (v == null) return null
        try {
            return (v instanceof BigDecimal) ? (BigDecimal)v : new BigDecimal(v.toString())
        } catch(Exception e) {
            return null
        }
    }
    boolean getBoolean(String aReference) {
        def v = _unwrapPropertyValue(this.@delegate.get(aReference))
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
    int getType() { return ClipboardProperty.TYPE_PAGE } // Added missing getType() implementation
    ClipboardPage copy(ClipboardPage aPage) { copyTo(aPage); return aPage }
    void removeFromClipboard() { this.@delegate.clear() }
}

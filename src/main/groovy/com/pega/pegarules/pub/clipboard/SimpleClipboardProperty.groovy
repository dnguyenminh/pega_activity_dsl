package com.pega.pegarules.pub.clipboard

import java.math.BigDecimal
import java.util.Date
import java.util.Objects

/** Minimal ClipboardProperty implementation used for base-class defaults. */
class SimpleClipboardProperty implements ClipboardProperty {
    Object value
    String name

    // Backwards-compatible constructor: allow constructing with just a value
    // Pega stores simple property data as String by default; keep STRING as default type
    SimpleClipboardProperty(Object v = null) {
        this.name = null
        this.value = v
    // debug logging removed - do not print during tests
    }

    // Convenience named constructor: keep STRING as default type unless caller supplies explicit type
    SimpleClipboardProperty(String name, Object v) {
        this.name = name
        this.value = v
    // debug logging removed - do not print during tests
    }

    // Named property constructor: explicit args to avoid ambiguity
    SimpleClipboardProperty(String name, Object v, int type) { // 'type' parameter is now unused.
        this.name = name; this.value = v;
    // debug logging removed - do not print during tests
    }

    // Accept enum typed constructor for convenience so callers using ClipboardPropertyType can pass it directly
    SimpleClipboardProperty(String name, Object v, com.pega.pegarules.pub.clipboard.ClipboardPropertyType type) {
        this(name, v)
        // current implementation ignores explicit type; this keeps behaviour consistent
    // debug logging removed - do not print during tests
    }

    void add(Object aValue) {
        if(this.value == null) this.value = [aValue]
        else if(this.value instanceof List) ((List)this.value) << aValue
        else this.value = [this.value, aValue]
    }

    void add(int aIndex, Object aValue) {
        if(!(this.value instanceof List)) {
            this.value = this.value == null ? [] : [this.value]
        }
        ((List)this.value).add(aIndex, aValue)
    }

    void clearValue() { this.value = null }

    boolean contains(Object aValue) {
        if(this.value instanceof List) return ((List)this.value).contains(aValue)
        if(this.value instanceof Map) return ((Map)this.value).containsValue(aValue) || ((Map)this.value).containsKey(aValue)
        return this.value == aValue
    }

    void doBackwardChain() { }

    ClipboardProperty get(int aIndex) {
        if(this.value instanceof List) {
            def v = ((List)this.value)[aIndex]
            if (v instanceof Map || v instanceof ClipboardPage) {
                // wrap Page into a ClipboardProperty wrapper so callers receive a ClipboardProperty
                return new SimpleClipboardProperty(new Page(v))
            } else {
                return new SimpleClipboardProperty(v)
            }
        }
        return null
    }

    ClipboardProperty get(String aIndex) {
        if(this.value instanceof Map) {
            def v = ((Map)this.value)[aIndex]
            if (v instanceof Map || v instanceof ClipboardPage) {
                // wrap Page into a ClipboardProperty wrapper
                return new SimpleClipboardProperty(new Page(v))
            } else {
                return new SimpleClipboardProperty(v)
            }
        }
        return null
    }

    String getAbsoluteReference() { return null }

    BigDecimal getBigDecimalValue() { try { return value == null ? null : new BigDecimal(value.toString()) } catch(Exception e) { return null } }

    boolean getBooleanValue() { return this.toBoolean() }

    Object getDefinition() { return null }

    double getDoubleValue() { try { return value == null ? 0.0d : (value as double) } catch(Exception e) { return 0.0d } }

    Object getEntryHandle() { return null }

    Iterator getErrors() { return [].iterator() }

    int getIntegerValue() { try { return value == null ? 0 : (value as int) } catch(Exception e) { return 0 } }

    String getJustification() { return null }

    int getLength() { return value == null ? 0 : value.toString().length() }

    Iterator getMessages() { return [].iterator() }

    int getMode() {
        if(this.value instanceof List) return MODE_LIST
        if(this.value instanceof Map) return MODE_GROUP
        return MODE_SINGLE
    }

    String getName() { return name }

    ClipboardPage getParent() { return null }

    ClipboardPage getPageValue() {
        // If the stored value is already a ClipboardPage (or Page), return it.
        if (this.value instanceof ClipboardPage) return (ClipboardPage)this.value
        // Convert AbstractClipboardPage / Map into Page so callers receive Page instances
        if (this.value instanceof AbstractClipboardPage) return new Page((ClipboardPage)this.value)
        if (this.value instanceof Map) return new Page((Map)this.value)
        return null
    }

    Object getPropertyValue() {
        
        // Aggressively normalize property values to Page or List<Page> where appropriate.
        def v = this.value
        // Unwrap nested ClipboardProperty layers first (best-effort).
        try {
            while (v instanceof ClipboardProperty) {
                v = ((ClipboardProperty)v).getPropertyValue()
            }
        } catch(Exception ignored) { /* continue with what we have */ }

        // If it's already a Page, return it (robust check for classloader issues)
        // If it's already a Page, return it (robust check for classloader issues)
        if (v instanceof Page) {
            return v
        }
        if (v != null) {
            def vClass = v.getClass()
            if (vClass != null && vClass.name == 'com.pega.pegarules.pub.clipboard.Page') {
                return v
            }
        }
        // If it's any ClipboardPage, convert to Page (ensure AbstractClipboardPage -> Page)
        if (v instanceof ClipboardPage) {
            return new Page((ClipboardPage)v)
        }
        // If it's an AbstractClipboardPage fallback, convert to Page
        if (v instanceof AbstractClipboardPage) {
            return new Page((ClipboardPage)v)
        }
        // If it's a Map, convert to Page
        if (v instanceof Map) {
            return new Page((Map)v)
        }
        // If it's a List, convert Map/ClipboardPage elements to Page instances
        if (v instanceof List) {
            return ((List)v).collect { elem ->
                def e = elem
                try {
                    while (e instanceof ClipboardProperty) {
                        e = ((ClipboardProperty)e).getPropertyValue()
                    }
                } catch(Exception ignoredInner) {}
                // Robust check for Page instances within the list (classloader issues)
                if (e instanceof Page) return e
                if (e != null) {
                    def eClass = e.getClass()
                    if (eClass != null && eClass.name == 'com.pega.pegarules.pub.clipboard.Page') return e
                }
                if (e instanceof ClipboardPage) return new Page((ClipboardPage)e)
                if (e instanceof Map) return new Page((Map)e)
                return e
            }
        }
        // Otherwise return as-is
        return v
    }

    String getReference() { return null }

    String getStringValue() { return value == null ? null : value.toString() }

    int getType() {
        if(this.value instanceof Map) return TYPE_PAGE
        if(this.value instanceof List) return TYPE_UNKNOWN
        if(this.value instanceof Integer) return TYPE_INTEGER
        if(this.value instanceof BigDecimal) return TYPE_DECIMAL
        if(this.value instanceof Double || this.value instanceof Float) return TYPE_DOUBLE
        if(this.value instanceof Boolean) return TYPE_TRUEFALSE
        if(this.value instanceof Date) return TYPE_DATETIME
        if(this.value instanceof String) return TYPE_TEXT
        return TYPE_UNKNOWN
    }

    boolean hasMessages() { return false }

    boolean isError() { return false }

    boolean isIncompatible() { return false }

    boolean isProtected() { return false }

    boolean isPage() { return this.value instanceof Map }

    boolean isUndefined() { return this.value == null }

    Iterator<ClipboardProperty> iterator() {
        if(this.value instanceof List) {
            return ((List)this.value).collect { new Page(it) }.iterator()
        }
        if(this.value instanceof Map) {
            return ((Map)this.value).collect { k,v -> new Page(v) }.iterator()
        }
        return [].iterator()
    }

    void remove(int aIndex) {
        if(this.value instanceof List) ((List)this.value).remove(aIndex)
    }

    void remove(String aIndex) {
        if(this.value instanceof Map) ((Map)this.value).remove(aIndex)
    }

    void setJustification(String aJustification) { }

    void setValue(Object aValue) { this.value = aValue }

    int size() {
        if(this.value == null) return 0
        if(this.value instanceof List) return ((List)this.value).size()
        if(this.value instanceof Map) return ((Map)this.value).size()
        return 1
    }

    boolean toBoolean() {
        if(this.value == null) return false
        if(this.value instanceof Boolean) return this.value
        try { return this.value as boolean } catch(Exception e) { return false }
    }

    Date toDate() {
        if(this.value instanceof Date) return (Date)this.value
        try { return null } catch(Exception e) { return null }
    }

    double toDouble() { try { return value == null ? 0.0d : (value as double) } catch(Exception e) { return 0.0d } }

    int toInteger() { try { return value == null ? 0 : (value as int) } catch(Exception e) { return 0 } }

    String toString() { return getStringValue() }

    boolean equals(Object o) {
        if (this.is(o)) return true
        def my = this.getPropertyValue()
        if (o instanceof SimpleClipboardProperty) {
            return Objects.equals(my, ((SimpleClipboardProperty)o).getPropertyValue())
        }
        return Objects.equals(my, o)
    }

    int hashCode() {
        def v = this.getPropertyValue()
        return v == null ? 0 : v.hashCode()
    }
}

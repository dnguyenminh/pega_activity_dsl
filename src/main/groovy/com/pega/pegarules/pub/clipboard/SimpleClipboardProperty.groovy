package com.pega.pegarules.pub.clipboard

import java.math.BigDecimal
import java.util.Date

/** Minimal ClipboardProperty implementation used for base-class defaults. */
class SimpleClipboardProperty implements ClipboardProperty {
    Object value
    String name
    ClipboardPropertyType type
    // Backwards-compatible constructor: allow constructing with just a value
    // Pega stores simple property data as String by default; keep STRING as default type
    SimpleClipboardProperty(Object v = null) {
        this.name = null
        this.value = v
        this.type = ClipboardPropertyType.STRING
        try { println "DEBUG SimpleClipboardProperty:<init> value=${v}, type=${this.type}" } catch(Exception ignore) {}
    }

    // Convenience named constructor: keep STRING as default type unless caller supplies explicit type
    SimpleClipboardProperty(String name, Object v) {
        this.name = name
        this.value = v
        this.type = ClipboardPropertyType.STRING
        try { println "DEBUG SimpleClipboardProperty:<init> name=${name}, value=${v}, type=${this.type}" } catch(Exception ignore) {}
    }

    // Named property constructor: explicit args to avoid ambiguity
    SimpleClipboardProperty(String name, Object v, ClipboardPropertyType type) {
        this.name = name; this.value = v; this.type = type
        try { println "DEBUG SimpleClipboardProperty:<init> name=${name}, value=${v}, type=${type}" } catch(Exception ignore) {}
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
            return new Page(v)
        }
        return null
    }

    ClipboardProperty get(String aIndex) {
        if(this.value instanceof Map) {
            def v = ((Map)this.value)[aIndex]
            return new Page(v)
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
        if(this.value instanceof AbstractClipboardPage) return (AbstractClipboardPage)this.value
        if(this.value instanceof Map) return new SimpleClipboardPage((Map)this.value)
        return null
    }

    Object getPropertyValue() { return value }

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

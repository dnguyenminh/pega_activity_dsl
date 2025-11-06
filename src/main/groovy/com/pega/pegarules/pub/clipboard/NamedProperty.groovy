package com.pega.pegarules.pub.clipboard

/** Small named property wrapper to make test code clearer. Delegates to SimpleClipboardProperty. */
class NamedProperty extends SimpleClipboardProperty {
    NamedProperty(String name, Object value, ClipboardPropertyType type = ClipboardPropertyType.STRING) {
        super(name, value, type)
    }

    // Backwards-compatible single-arg constructor
    NamedProperty(Object v) {
        super(v)
    }
}

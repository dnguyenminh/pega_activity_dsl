package com.pega.pegarules.pub.clipboard

/** Thin wrapper representing a single value clipboard property. */
class SingleValue extends SimpleClipboardProperty {
    SingleValue(Object v = null) {
        super(v)
        this.type = ClipboardPropertyType.STRING
    }

    SingleValue(String name, Object v) {
        super(name, v, ClipboardPropertyType.STRING)
    }
}

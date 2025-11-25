package com.pega.pegarules.pub.clipboard

/** Thin wrapper representing a single value clipboard property. */
class SingleValue extends SimpleClipboardProperty {
    // SingleValue(String v = null) {
    //     super(v)
    //     this.type = ClipboardPropertyType.STRING
    // }

    // SingleValue(String name, String v) {
    //     super(name, v, ClipboardPropertyType.STRING)
    // }
    SingleValue(String name, String v, int type) {
        super(name, v, type)
    }
}

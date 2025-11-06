package com.pega.pegarules.pub.clipboard

/** Wrapper for a list of JavaProperty instances. */
class JavaPropertyList extends SimpleClipboardProperty {
    JavaPropertyList(List vals = null) {
        super(vals)
        this.type = ClipboardPropertyType.JAVA_PROPERTY_LIST
    }
}

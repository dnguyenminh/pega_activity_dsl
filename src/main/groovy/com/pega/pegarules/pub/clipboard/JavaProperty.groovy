package com.pega.pegarules.pub.clipboard

/** Wrapper representing a Java-backed property mapping. */
class JavaProperty extends SimpleClipboardProperty {
    JavaProperty(Object v = null) {
        super(v)
        this.type = ClipboardPropertyType.JAVA_PROPERTY
    }

    JavaProperty(String name, Object v) {
        super(name, v, ClipboardPropertyType.JAVA_PROPERTY)
    }
}

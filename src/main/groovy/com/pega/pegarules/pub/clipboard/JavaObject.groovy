package com.pega.pegarules.pub.clipboard

/** Wrapper for a Java object mapping (map of JavaProperty). */
class JavaObject extends SimpleClipboardProperty {
    JavaObject(Map<String, JavaProperty> m = null) {
        super(m)
        this.type = ClipboardPropertyType.JAVA_OBJECT_GROUP
    }
}

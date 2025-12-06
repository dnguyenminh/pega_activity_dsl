package com.pega.pegarules.pub.clipboard

import groovy.transform.CompileStatic

/** Wrapper representing a Java-backed property mapping. */
@CompileStatic
class JavaProperty extends SimpleClipboardProperty {
    JavaProperty(Object v = null) {
        super(v)
        // this.type = ClipboardPropertyType.JAVA_PROPERTY  // Removed: no such field exists
    }

    JavaProperty(String name, Object v) {
        super(name, v, ClipboardPropertyType.JAVA_PROPERTY)
    }
}

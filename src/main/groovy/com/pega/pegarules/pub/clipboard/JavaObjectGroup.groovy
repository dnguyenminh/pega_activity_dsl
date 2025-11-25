package com.pega.pegarules.pub.clipboard

/** Wrapper for a map of JavaObject instances. */
class JavaObjectGroup extends SimpleClipboardProperty {
    JavaObjectGroup(Map<String, JavaObject> m = null) {
        super(m)
    }
}

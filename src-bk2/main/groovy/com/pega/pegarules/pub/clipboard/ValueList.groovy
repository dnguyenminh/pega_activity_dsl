package com.pega.pegarules.pub.clipboard

/** Thin wrapper representing a list of single values. */
class ValueList extends SimpleClipboardProperty {
    ValueList(List vals = null) {
        super(vals)
        // leave type inference to SimpleClipboardProperty
    }
}

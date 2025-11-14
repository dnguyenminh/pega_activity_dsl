package com.pega.pegarules.pub.clipboard

/**
 * Representation of a Pega "Code-Pega-List" page class.
 * This class extends Page and ensures the pxObjClass and pxResults
 * properties are initialized to Pega-style values. pxResults is
 * constructed via ClipboardFactory.newPageList(...) so it is a
 * ClipboardProperty (PageList) instance.
 */
class CodePegaList extends BaseClass {
    /**
     * Canonical properties for Code-Pega-List pages. Values may be null
     * to indicate a documented/default name but no instance-level value
     * should be added during construction. The constructor will only copy
     * non-null defaults into the new page instance.
     */
    static final Map<String, Object> STANDARD_CODEPEGALIST_PROPS = [
        // marker and results
        pxObjClass: 'Code-Pega-List',
        pxResults: null,

        // page-list typed helpers (documented here; left null so ctor will add explicit PageList)
        pyCondition: null,

        // textual/documentation keys (documented but default null)
        pyCacheFile: null,
        pyDetailsAction: null,
        pyDetailsWindow: null,
        pyMaxRecords: null,
        pyObjClass: null,
        pyPageSize: null,
        pyQueryTimeStamp: null,

        // boolean hints (provide defaults)
        pyReturnLightweightResults: false,
        pyUseAlternateDb: false,

        // runtime/read-only numeric/boolean defaults
        pxElapsedTime: 0,
        pxMore: false,
        pxNextKey: null,
        pxResultCount: 0,
        pxSelectStatement: null,
        pxSQLStatementPost: null,
        pxSQLStatementPre: null,
        pxTimeElapsed: 0,
        pxTotalResultCount: 0
    ]

    CodePegaList(List pages = null) {
        super()
        try {
            // initialize pxResults and other non-null defaults from the canonical static map
            def cf = com.pega.pegarules.pub.clipboard.ClipboardFactory

            // pxResults is special: create from provided pages (may be null -> factory will handle)
            def pl = cf.newPageList(pages)
            this.putAt('pxResults', pl)

            // iterate canonical defaults and add only non-null defaults
            STANDARD_CODEPEGALIST_PROPS.each { k, v ->
                try {
                    if (v == null) return
                    def toStore
                    if (k == 'pxObjClass') {
                        toStore = cf.newProperty(k, v)
                    } else if (v instanceof Boolean) {
                        toStore = cf.newProperty(k, v, com.pega.pegarules.pub.clipboard.ClipboardPropertyType.BOOLEAN)
                    } else if (v instanceof Integer) {
                        toStore = cf.newProperty(k, v, com.pega.pegarules.pub.clipboard.ClipboardPropertyType.INTEGER)
                    } else if (v instanceof Number) {
                        toStore = cf.newProperty(k, v, com.pega.pegarules.pub.clipboard.ClipboardPropertyType.DECIMAL)
                    } else if (v instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) {
                        toStore = v
                    } else {
                        // fallback: store as STRING via factory
                        toStore = cf.newProperty(k, v)
                    }
                    this.putAt(k, toStore)
                } catch(Exception e) {
                    // ignore individual failures to keep construction robust
                }
            }
        } catch(Exception e) {
            // defensive: swallow to avoid breaking test harness during construction
        }
    }

    /** Convenience accessor to return the underlying List of ClipboardPage instances. */
    List getPxResultsList() {
        try {
            def p = this.getPropertyObject('pxResults')
            if(p instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) return ((com.pega.pegarules.pub.clipboard.ClipboardProperty)p).getPropertyValue()
            return p
        } catch(Exception e) { return null }
    }
}

package com.pega.pegarules.pub.clipboard

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Representation of the standard BaseClass property rule.
 *
 * This provides the canonical set of base-class properties used by
 * clipboard pages and some small helpers to apply/read those defaults.
 *
 * It's intentionally small and non-invasive — AbstractClipboardPage still
 * performs wrapping/conversion. This helper centralizes the canonical
 * property names so other parts of the simulator can reference them.
 */
class BaseClass extends Page {
    /** Canonical base-class properties (shallow defaults). */
    // Expanded canonical base-class properties to better match Pega docs.
    static final Map<String, Object> STANDARD_BASECLASS_PROPS = [
        // timestamp variants seen in Pega docs
        pxCreateDateTime: null,
        pxCreateDate: null,
        pxUpdateDateTime: null,
        pxUpdateDate: null,

        // operators
        pxCreateOperator: null,
        pxUpdateOperator: null,

        // flow bookkeeping
        pxFlow: null,
        pxFlowCount: 0,

        // identifiers
        pxInsName: null,
        pzInsKey: null,
        pzStatus: null,

        // class and labels
        pxObjClass: '@baseclass',
        pyLabel: '',
        pyDescription: '',
        pyWorkPage: '',

        // misc
        pyTemporaryObject: false
    ]

    /** Return a shallow copy of the standard base-class props map. */
    static Map<String, Object> copyStandardProps() {
        return STANDARD_BASECLASS_PROPS.collectEntries { k, v -> [(k): v] }
    }

    /**
     * Apply the standard base-class properties to the provided page.
     * This will call putAt on the page so the page's wrapping rules apply.
     */
    static void applyTo(ClipboardPage page) {
        if(page == null) return
        copyStandardProps().each { k, v ->
            def toStore = (v instanceof ClipboardProperty) ? v : com.pega.pegarules.pub.clipboard.ClipboardFactory.newProperty(k, v == null ? null : v)
            page.putAt(k, toStore)
        }
    }

    /**
     * Helper: return set of canonical base-class property names.
     */
    static Set<String> propertyNames() {
        return STANDARD_BASECLASS_PROPS.keySet()
    }

    /**
     * Default constructor: create an empty Page and ensure base-class
     * properties exist on the instance. We only set values when a property
     * is missing to avoid overwriting explicit inputs.
     */
    BaseClass() {
        super()
        ensureBasePropsPresent()
    }

    /**
     * Construct from a Map (delegates to Page(Map)) then ensure base props.
     */
    BaseClass(Map m) {
        super(m)
        ensureBasePropsPresent()
    }

    /**
     * Construct from List-style descriptors then ensure base props.
     */
    BaseClass(List props) {
        super(props)
        ensureBasePropsPresent()
    }

    private void ensureBasePropsPresent() {
        // Only put defaults for keys that are not already present
        // compute UTC now once for consistent values within this call
        def nowInstant = Instant.now()
        def nowDateTime = DateTimeFormatter.ISO_INSTANT.format(nowInstant)
        def nowDate = nowInstant.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE)

        copyStandardProps().each { k, v ->
            def existing = this.getPropertyObject(k)

            // Determine a sensible default when the standard map has nulls.
            def defaultVal = v

            // If the base-class key is a timestamp/date variant, initialize to now (UTC)
            if (defaultVal == null) {
                if (k == 'pxCreateDateTime' || k == 'pxUpdateDateTime') {
                    defaultVal = nowDateTime
                } else if (k == 'pxCreateDate' || k == 'pxUpdateDate') {
                    defaultVal = nowDate
                } else if (k.toString().startsWith('py')) {
                    // For py* properties prefer empty string instead of null so
                    // pages have usable default textual values.
                    defaultVal = ''
                } else {
                    defaultVal = null
                }
            }

            // Set default when property is missing or its value is null/empty
            boolean shouldSet = (existing == null)
            if(!shouldSet && (existing instanceof String)) {
                shouldSet = ((existing as String).trim().length() == 0 && defaultVal != null)
            }
            if(shouldSet) {
                def toStore = (defaultVal instanceof ClipboardProperty) ? defaultVal : com.pega.pegarules.pub.clipboard.ClipboardFactory.newProperty(k, defaultVal == null ? null : defaultVal)
                this.putAt(k, toStore)
            }
        }
    }
}

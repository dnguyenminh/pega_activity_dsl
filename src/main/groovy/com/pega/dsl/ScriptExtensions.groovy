package com.pega.dsl

class ScriptExtensions {
    static {
        def scriptMeta = groovy.lang.Script.metaClass
        def objectMeta = Object.metaClass
        def specMeta = null
        try { specMeta = Class.forName('spock.lang.Specification').metaClass } catch (ignored) { }

        // Known DSL entrypoints we will forward explicitly to avoid recursive metaClass dispatch.
        def DSL_METHODS = [
            'application','flow','activity','dataTransform','decisionTable','decisionTree','dataPage',
            'property','section','harness','correspondence','restConnector','soapConnector','restService',
            'testCase','accessGroup','accessRole','database','authenticationProfile','ruleset','when',
            'pega','setting','setVersion'
        ] as Set

        // Top-level script helpers (for Groovy scripts)
        scriptMeta.application = { String name, Closure closure = null ->
            com.pega.dsl.PegaDeveloperUtilitiesDsl.application(name, closure)
        }
        scriptMeta.flow = { String name, Closure closure = null ->
            com.pega.dsl.PegaDeveloperUtilitiesDsl.flow(name, closure)
        }

        // Make script-level invoke fallback to DSL too (use Object[] signature for proper dispatch)
        scriptMeta.invokeMethod = { String name, Object[] args ->
            // Forward only known DSL entrypoints to avoid intercepting normal instance dispatch.
            if (DSL_METHODS.contains(name)) {
                try { return com.pega.dsl.PegaDeveloperUtilitiesDsl."${name}"(*args) }
                catch (MissingMethodException e) { throw new MissingMethodException(name, delegate?.getClass(), args) }
            }

            try {
                def mm = delegate?.metaClass?.getMetaMethod(name, args)
                if (mm != null) {
                    return mm.invoke(delegate, args as Object[])
                }
            } catch (ignored) { /* swallow and fall through to MissingMethod */ }

            throw new MissingMethodException(name, delegate?.getClass(), args)
        }

        // Also install on Object so Spock Specs and other classes can call
        // application(...) and flow(...) unqualified.
        objectMeta.application = { String name, Closure closure = null ->
            com.pega.dsl.PegaDeveloperUtilitiesDsl.application(name, closure)
        }
        objectMeta.flow = { String name, Closure closure = null ->
            com.pega.dsl.PegaDeveloperUtilitiesDsl.flow(name, closure)
        }

        // Forward unknown method resolution on instances to the DSL as a fallback.
        // Use Object[] to match Groovy's invokeMethod signature at runtime.
        objectMeta.invokeMethod = { String name, Object[] args ->
            // Only forward a known DSL entrypoint; otherwise attempt normal meta-method dispatch.
            if (DSL_METHODS.contains(name)) {
                try { return com.pega.dsl.PegaDeveloperUtilitiesDsl."${name}"(*args) }
                catch (MissingMethodException e) { throw new MissingMethodException(name, delegate?.getClass(), args) }
            }

            try {
                def mm = delegate?.metaClass?.getMetaMethod(name, args)
                if (mm != null) {
                    return mm.invoke(delegate, args as Object[])
                }
            } catch (ignored) { /* swallow and fall through */ }

            throw new MissingMethodException(name, delegate?.getClass(), args)
        }

        // Also keep methodMissing to cover Groovy's fallback paths.
        objectMeta.methodMissing = { String name, Object[] args ->
            try {
                return com.pega.dsl.PegaDeveloperUtilitiesDsl."${name}"(*args)
            } catch (MissingMethodException e) {
                throw new MissingMethodException(name, delegate.getClass(), args)
            }
        }

        // Install forwarding/helpers specifically onto Spock Specification metaClass
        // to ensure unqualified calls like application(...) inside specs resolve.
        if (specMeta != null) {
            specMeta.invokeMethod = { String name, Object[] args ->
                // Allow known DSL entrypoints to be called unqualified inside specs.
                if (DSL_METHODS.contains(name)) {
                    try { return com.pega.dsl.PegaDeveloperUtilitiesDsl."${name}"(*args) }
                    catch (MissingMethodException e) { throw new MissingMethodException(name, delegate?.getClass(), args) }
                }

                try {
                    def mm = delegate?.metaClass?.getMetaMethod(name, args)
                    if (mm != null) {
                        return mm.invoke(delegate, args as Object[])
                    }
                } catch (ignored) { /* swallow */ }

                throw new MissingMethodException(name, delegate?.getClass(), args)
            }

            specMeta.methodMissing = { String name, Object[] args ->
                try {
                    return com.pega.dsl.PegaDeveloperUtilitiesDsl."${name}"(*args)
                } catch (MissingMethodException e) {
                    throw new MissingMethodException(name, delegate.getClass(), args)
                }
            }

            specMeta.application = { String name, Closure closure = null ->
                com.pega.dsl.PegaDeveloperUtilitiesDsl.application(name, closure)
            }
            specMeta.flow = { String name, Closure closure = null ->
                com.pega.dsl.PegaDeveloperUtilitiesDsl.flow(name, closure)
            }
        }
    }
}
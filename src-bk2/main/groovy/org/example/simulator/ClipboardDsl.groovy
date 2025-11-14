package org.example.simulator

import com.pega.pegarules.pub.clipboard.ClipboardFactory as CF

/**
 * Project-wide DSL helpers to allow Gradle-style closure syntax for building pages/page-lists.
 * Example:
 *   import static org.example.simulator.ClipboardDsl.pageList
 *   def list = pageList {
 *     page { amount 5; id 'a' }
 *     page { amount 2; id 'b' }
 *   }
 */

class PropertyCollector {
    Map map = [:]

    // capture method-like calls inside the closure: e.g. amount 5
    def invokeMethod(String name, Object args) {
        if (args instanceof Object[]) {
            if (args.length == 1) map[name] = args[0]
            else map[name] = args as List
        } else {
            map[name] = args
        }
        return null
    }

    // handle missing-method dispatch as well
    def methodMissing(String name, Object args) {
        if (args instanceof Object[]) {
            if (args.length == 1) map[name] = args[0]
            else map[name] = args as List
        } else {
            map[name] = args
        }
        return null
    }
}

class ClipboardDsl {
    static def pageList(Closure c) {
        def pages = []
        def ctx = new Expando()
        ctx.page = { arg ->
            if (arg instanceof Map) pages << CF.newPage(arg, null)
            else if (arg instanceof Closure) {
                def collector = new PropertyCollector()
                arg.delegate = collector
                arg.resolveStrategy = Closure.DELEGATE_FIRST
                arg()
                pages << CF.newPage(collector.map, null)
            } else {
                throw new IllegalArgumentException("Unsupported page arg: $arg")
            }
        }

        c.delegate = ctx
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c()
        return CF.newPageList(pages)
    }

    static def page(Map m) { CF.newPage(m, null) }

    static def page(Closure c) {
        def collector = new PropertyCollector()
        c.delegate = collector
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c()
        return CF.newPage(collector.map, null)
    }

    /**
     * Build a Clipboard using a Gradle-like closure DSL.
     * Example:
     *   def cb = ClipboardDsl.clipboard {
     *     pageList('Orders') {
     *       page { amount 5; id 'a' }
     *     }
     *   }
     */
    static def clipboard(Closure c) {
        def cb = new Clipboard()
        def ctx = new Expando()

        // page(name, Closure) -> create a single page and set it on clipboard
        ctx.page = { String name, Object arg ->
            if(arg instanceof Closure) cb.set(name, page((Closure)arg))
            else if(arg instanceof Map) cb.set(name, arg)
            else throw new IllegalArgumentException("Unsupported page arg for $name: ${arg?.getClass()?.name}")
        }

        // pageList(name, Closure) -> create a PageList and set it on clipboard
        ctx.pageList = { String name, Closure arg ->
            cb.set(name, pageList((Closure)arg))
        }

        // support shorthand: name { page { ... } }
        ctx.methodMissing = { String name, Object args ->
            if(args && args.length == 1 && args[0] instanceof Closure) {
                // assume page-list for method calls
                cb.set(name, pageList((Closure)args[0]))
                return null
            }
            throw new MissingMethodException(name, ClipboardDsl, args)
        }

        c.delegate = ctx
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c()
        return cb
    }

    // Helper to produce a Pega-style function-expression string for use in step params
    // Example: ClipboardDsl.lengthOfPageListExpr('Orders') -> "@LengthOfPageList(Orders)"
    static def lengthOfPageListExpr(String pagePath) {
        if(pagePath == null) return null
        def p = pagePath.trim()
        if((p.startsWith("'") && p.endsWith("'")) || (p.startsWith('"') && p.endsWith('"'))) {
            p = p.substring(1, p.length()-1)
        }
        return "@LengthOfPageList(${p})"
    }
}

package org.example

class PropertyUtils {
    /**
     * Get a value by path (dot-separated). If the full path is a top-level key, return it directly.
     */
    static Object get(Map ctx, String path) {
        if (path == null) return null
        if (ctx.containsKey(path)) return ctx[path]
        def parts = path.split(/\./)
        def cur = ctx
        for (int i = 0; i < parts.length; i++) {
            def p = parts[i]
            if (cur instanceof Map && cur.containsKey(p)) {
                def val = cur[p]
                if (i == parts.length - 1) return val
                cur = val
            } else {
                return null
            }
        }
        return null
    }

    /**
     * Set a value by path (dot-separated). Creates intermediate maps as needed.
     */
    static void set(Map ctx, String path, Object value) {
        if (path == null) return
        if (!path.contains('.')) {
            ctx[path] = value
            return
        }
        def parts = path.split(/\./)
        def cur = ctx
        for (int i = 0; i < parts.length - 1; i++) {
            def p = parts[i]
            if (!(cur[p] instanceof Map)) {
                cur[p] = [:]
            }
            cur = cur[p]
        }
        cur[parts[-1]] = value
    }

    /**
     * Remove a property by path. If removal leaves empty parent maps, they are left as-is.
     */
    static void remove(Map ctx, String path) {
        if (path == null) return
        if (!path.contains('.')) {
            ctx.remove(path)
            return
        }
        def parts = path.split(/\./)
        def cur = ctx
        for (int i = 0; i < parts.length - 1; i++) {
            def p = parts[i]
            if (!(cur[p] instanceof Map)) return
            cur = cur[p]
        }
        cur.remove(parts[-1])
    }
}


package vn.com.fecredit.pega.activity

class WhenRegistry {
    private static final Map<String, Closure> registry = [:]

    static void register(String name, Closure c) {
        if (name == null || c == null) return
        registry[name.toString()] = c
    }

    static Closure get(String name) {
        if (!name) return null
        return registry[name.toString()]
    }

    static boolean contains(String name) {
        return registry.containsKey(name.toString())
    }

    static void clear() { registry.clear() }

    static Set<String> keys() { registry.keySet() as Set }
}


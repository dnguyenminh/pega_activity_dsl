package com.pega.dsl

class SectionBuilder {
    private final Section section

    SectionBuilder(Section section) {
        this.section = section
    }
 
    def doCall(Object... args) { this }
 
    def dynamic() {
        section.layoutType = 'Dynamic'
        return this
    }

    def freeform() {
        section.layoutType = 'Freeform'
        return this
    }

    def smartLayout() {
        section.layoutType = 'Smart Layout'
        return this
    }

    def description(String txt) {
        section.description = txt
        section.descriptionText = txt
        return this
    }

    def className(String className) {
        section.className = className
        this
    }

    def setStatus(String status) {
        section.status = status
        this
    }

    def repeatingGrid(String pageList, Closure closure) {
        def grid = new RepeatingGridElement(pageList: pageList)
        if (closure) {
            closure.delegate = grid
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        section.elements.add(grid)
        return grid
    }

    def table(String pageList, Closure closure = null) {
        return repeatingGrid(pageList, closure)
    }

    def input(String property, String label = '', Closure closure = null) {
        def inp = new InputElement(property: property, label: label)
        if (closure) {
            closure.delegate = inp
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        section.elements.add(inp)
        return inp
    }

    def input(String property, Closure closure) {
        return input(property, '', closure)
    }

    def includeSection(String name, Closure closure = null) {
        def inc = new IncludeSectionElement(sectionName: name)
        if (closure) {
            closure.delegate = inc
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        section.elements.add(inc)
        return inc
    }

    def button(String label, String action = '', Closure closure = null) {
        def b = new ButtonElement(label: label, action: action)
        if (closure) {
            closure.delegate = b
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        section.elements.add(b)
        return b
    }

    def button(String label, Closure closure) {
        return button(label, '', closure)
    }

    def label(String text, String property = '') {
        def l = new LabelElement(text: text, property: property)
        section.elements.add(l)
        return l
    }

    def methodMissing(String name, Object[] args) {
        // Conservative fallback: if a closure.call/doCall accidentally resolves to
        // this builder (e.g. "call 'X'"), accept it and no-op.
        try {
            if (name == 'call' && args != null && args.length >= 1 && args[0] instanceof String) {
                return this
            }
        } catch (ignored) { }

        try {
            System.err.println("DEBUG: SectionBuilder.methodMissing called raw='${name}' argsCount=${args?.length}")
            Closure c = null
            if (args instanceof Closure) {
                c = (Closure) args
            } else if (args instanceof Object[] && args.length > 0 && args[args.length - 1] instanceof Closure) {
                c = (Closure) args[args.length - 1]
            }

            if (c != null && name) {
                def candidate = name?.toString()
                candidate = PegaDslCore.normalizeCandidate(candidate)
                if (!candidate) return null

                if (candidate.toLowerCase().contains('detailslist')) {
                    return this.repeatingGrid('.DetailsList', c)
                }

                def pageList = candidate.startsWith('.') ? candidate : ('.' + candidate)
                return this.repeatingGrid(pageList, c)
            }
        } catch (ignored) { }
        return metaClass.invokeMethod(this, name, args)
    }
}

package com.pega.dsl

class Harness extends Rule {
    List<HarnessElement> elements = []
    String template

    Harness() {
        this.type = 'Harness'
    }

    def template(String templateName) {
        this.template = templateName
    }

    def header(String sectionName, Closure closure = null) {
        def elem = new HarnessElement(type: 'Header', content: sectionName)
        if (closure) {
            closure.delegate = elem
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        elements.add(elem)
    }

    def workArea(String sectionName, Closure closure = null) {
        def elem = new HarnessElement(type: 'Work Area', content: sectionName)
        if (closure) {
            closure.delegate = elem
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        elements.add(elem)
    }

    def footer(String sectionName, Closure closure = null) {
        def elem = new HarnessElement(type: 'Footer', content: sectionName)
        if (closure) {
            closure.delegate = elem
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        elements.add(elem)
    }

    def navigation(String sectionName, Closure closure = null) {
        def elem = new HarnessElement(type: 'Navigation', content: sectionName)
        if (closure) {
            closure.delegate = elem
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        elements.add(elem)
    }

    def includeSection(String sectionName, Closure closure = null) {
        def elem = new HarnessElement(type: 'Section', content: sectionName)
        if (closure) {
            closure.delegate = elem
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        elements.add(elem)
    }
}


package com.pega.dsl

class Correspondence extends Rule {
    String subject
    String body
    String format = 'HTML'
    List<CorrespondenceParameter> correspondenceParameters = []

    Correspondence() {
        this.type = 'Correspondence'
    }

    def subject(String subject) {
        this.subject = subject
    }

    def body(String body) {
        this.body = body
    }

    def html() {
        this.format = 'HTML'
    }

    def text() {
        this.format = 'Text'
    }

    def rtf() {
        this.format = 'RTF'
    }

    def parameter(String name, String prompt = '', String defaultValue = '', Closure closure = null) {
        def param = new CorrespondenceParameter(
            name: name,
            prompt: prompt,
            defaultValue: defaultValue
        )
        if (closure) {
            closure.delegate = param
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        correspondenceParameters.add(param)
        return param
    }

    // Handle single parameter name with closure (for typed parameters)
    def parameter(String name, Closure closure) {
        def param = new CorrespondenceParameter(name: name, prompt: '', defaultValue: '')
        closure.delegate = param
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        correspondenceParameters.add(param)
        return param
    }
}


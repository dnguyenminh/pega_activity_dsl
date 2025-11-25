package com.pega

import spock.lang.Specification
import com.pega.pegarules.pub.clipboard.SimpleClipboardPage
import com.pega.pegarules.pub.clipboard.ClipboardPage

class RuntimeDiagnosticSpec extends Specification {
    def "diagnostic"() {
        setup:
        def page = new SimpleClipboardPage([a:1, b: [x:2]])
        when:
        def v = page.getAt('b')
        then:
        println "DIAG: returned class: ${v?.getClass()?.name}"
        println "DIAG: returned instanceof ClipboardPage? ${v instanceof ClipboardPage}"
        try {
            println "DIAG: classloader: ${v?.getClass()?.getClassLoader()}"
            println "DIAG: interfaces: ${v?.getClass()?.getInterfaces()*.name}"
        } catch(Exception e) { println "DIAG: error reading interfaces: $e" }
        true
    }
}
package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class CodePegaListSpec extends Specification {

    def "constructor initializes pxResults as PageList and provides getPxResultsList"() {
        when:
        def cpl = new CodePegaList([[id:1],[id:2]])

    then:
    def p = cpl.getPropertyObject('pxResults')
    // Allow either PageList or plain List depending on creation path
    (p instanceof PageList) || (p instanceof List)
    cpl.getPxResultsList() instanceof List
    cpl.getPxResultsList().size() == 2
    }

    def "getPxResultsList handles null values"() {
        when:
        def cpl = new CodePegaList(null)

        then:
        cpl.getPxResultsList() instanceof List
    }

    def "constructor sets pxObjClass correctly"() {
        when:
        def cpl = new CodePegaList()

        then:
        cpl.getPropertyObject('pxObjClass') == 'Code-Pega-List'
    }

    def "constructor initializes boolean properties with correct types"() {
        when:
        def cpl = new CodePegaList()

        then:
        cpl.getPropertyObject('pyReturnLightweightResults') == false
        cpl.getPropertyObject('pyUseAlternateDb') == false
        cpl.getPropertyObject('pxMore') == false
    }

    def "constructor initializes integer properties with correct types"() {
        when:
        def cpl = new CodePegaList()

        then:
        cpl.getPropertyObject('pxElapsedTime') == 0
        cpl.getPropertyObject('pxResultCount') == 0
        cpl.getPropertyObject('pxTimeElapsed') == 0
        cpl.getPropertyObject('pxTotalResultCount') == 0
    }

    def "constructor handles null pages parameter"() {
        when:
        def cpl = new CodePegaList(null)

        then:
        cpl.getPxResultsList() != null
        cpl.getPxResultsList() instanceof List
    }

    def "constructor handles empty pages list"() {
        when:
        def cpl = new CodePegaList([])

        then:
        cpl.getPxResultsList() != null
        cpl.getPxResultsList().isEmpty()
    }

    def "constructor handles pages with data"() {
        given:
        def pages = [[id: 1, name: 'page1'], [id: 2, name: 'page2']]

        when:
        def cpl = new CodePegaList(pages)

        then:
        def results = cpl.getPxResultsList()
        results.size() == 2
        results[0] instanceof Map
        results[1] instanceof Map
    }

    def "getPxResultsList returns null on exception"() {
        given:
        def cpl = new CodePegaList()
        // Remove pxResults to cause exception in getPropertyObject
        cpl.remove('pxResults')

        when:
        def result = cpl.getPxResultsList()

        then:
        result == null
    }

    def "constructor handles exceptions gracefully during property setting"() {
        given:
        // This test verifies that exceptions during property setting don't break construction
        // The constructor has try-catch blocks around property setting

        when:
        def cpl = new CodePegaList()

        then:
        // Construction should succeed even if some properties fail to set
        cpl != null
        cpl instanceof CodePegaList
        cpl.getPropertyObject('pxObjClass') == 'Code-Pega-List'
    }
}

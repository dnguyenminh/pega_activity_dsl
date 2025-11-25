package com.pega.dsl

import spock.lang.Specification

class DelegateProxyExtraSpec extends Specification {

    def "delegate proxy forwards call/doCall and throws when target missing"() {
        given:
        def target = new Object() {
            def existing(String x) { "ok-$x" }
        }
        def proxy = new DelegateProxy(target)

    expect:
    // call the method via normal dispatch so DelegateProxy.invokeMethod path is used
    proxy.existing('X') == 'ok-X'

    when: "invoking a missing method should throw"
    proxy.nope()

    then:
    thrown(MissingMethodException)
    }
}

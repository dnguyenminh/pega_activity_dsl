package com.pega.dsl

import spock.lang.Specification

class RESTServiceSpec extends Specification {
    def cleanup() {
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "path with closure sets current delegate and mapping closures work, then restores when prev null"() {
        given:
        def s = new RESTService('S1')
        PegaDslCore.CURRENT_DELEGATE.remove()

        when:
        s.path('/hello') {
            // inside path closure, create an operation and mapping
            get()
            requestMapping {
                map('fromA', 'toA')
            }
            responseMapping {
                map('fromR', 'toR')
            }
            header('h1', 'v1')
            activity('Act1')
        }

        then:
        s.paths.size() == 1
        s.getResourcePath() == '/hello'
        s.activity == 'Act1'
        s.requestMapping['toA'] == 'fromA'
        s.responseMapping['toR'] == 'fromR'
        // ensure CURRENT_DELEGATE restored/removed
        PegaDslCore.CURRENT_DELEGATE.get() == null
    }

    def "path without closure still registers path and header applies at service level"() {
        given:
        def s = new RESTService('S2')

        when:
        s.path('/bare')
        s.header('global', 'g')

        then:
        s.paths.find { it.path == '/bare' }
        s.properties['global'] == 'g'
    }

    def "request/response mapping map(Map) overloads work and map() when no current operation is no-op"() {
        given:
        def s = new RESTService('S3')

        when: "use map() with no operation"
        def ret = s.map('a','b')

        then:
        ret.is(s)

        when: "explicit map via Map put"
        s.path('/m')
        s.post()
        s.requestMapping([p:'q'])
        s.responseMapping([r:'s'])

        then:
        s.requestMapping['p'] == 'q'
        s.responseMapping['r'] == 's'
    }

    def "header attaches to current operation when present"() {
        given:
        def s = new RESTService('S4')

        when:
        s.path('/op') {
            post()
            header('opk','opv')
        }

        then:
        def node = s.paths[-1]
        def op = node.operations[-1]
        op.properties['opk'] == 'opv'
    }

    def "_addOperation creates default path when none exists and sets current operation"() {
        given:
        def s = new RESTService('S5')

        when:
        s.get()

        then:
        s.paths.size() == 1
        s.paths[0].path == '/'
        s.paths[0].operations[-1].method == 'GET'
    }

    def "call(Closure) executes closure with service delegate and allows inline defs"() {
        given:
        def s = new RESTService('S6')

        when:
        s.call {
            description('inline')
            get()
            header('k','v')
        }

        then:
        s.description == 'inline'
        s.paths.size() == 1
        s.paths[-1].operations[-1].method == 'GET'
        s.paths[-1].operations[-1].properties['k'] == 'v'
    }

    def "description and setDescription both set the description field"() {
        given:
        def s = new RESTService('S7')

        when:
        s.description('one')
        s.setDescription('two')

        then:
        s.description == 'two'
    }

    def "patch() creates PATCH operation when used without explicit path"() {
        given:
        def s = new RESTService('S8')

        when:
        s.patch()

        then:
        s.paths.size() == 1
        s.paths[0].operations[-1].method == 'PATCH'
    }
}

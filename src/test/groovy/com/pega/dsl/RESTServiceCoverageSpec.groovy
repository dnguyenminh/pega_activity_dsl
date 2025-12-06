package com.pega.dsl

import spock.lang.Specification

class RESTServiceCoverageSpec extends Specification {

    def cleanup() {
        PegaDslCore.CURRENT_DELEGATE.remove()
    }

    def "header applies to both global and operation scopes"() {
        given:
        def service = new RESTService('Svc')

        when: 'add headers before any operation and inside an operation'
        service.header('X-Global', '1')
        service.path('/items') {
            get()
            header('X-Op', '2')
        }

        then: 'top-level header stored on properties while operation header stays local'
        service.properties['X-Global'] == '1'
        service.paths[0].operations[0].properties['X-Op'] == '2'
    }

    def "activity writes to both the service and the current operation"() {
        given:
        def service = new RESTService('Svc')

        when: 'set activity before an operation and then inside an operation'
        service.activity('TopActivity')
        service.path('/orders') {
            post()
            activity('OpActivity')
        }

        then:
        service.properties['activity'] == 'TopActivity'
        service.paths[0].operations[0].activity == 'OpActivity'
        service.activity == 'OpActivity'
    }

    def "request/response mapping helpers honor the current mode"() {
        given:
        def service = new RESTService('Svc')

        when:
        service.path('/mappings') {
            get()
            requestMapping([static: 'value'])
            requestMapping {
                map('payload.id', 'ID')
            }
            responseMapping([fixed: 'ok'])
            responseMapping {
                map('resp.total', 'TOTAL')
            }
        }

        then:
        def op = service.paths[0].operations[0]
        op.requestMapping == [static: 'value', ID: 'payload.id']
        op.responseMapping == [fixed: 'ok', TOTAL: 'resp.total']
        service._currentMappingMode == null

        and: 'calling mapping APIs without an active operation is a no-op'
        def idle = new RESTService('Idle')
        idle.requestMapping([noop: true])
        idle.responseMapping([noop: true])
        idle.requestMapping { map('a', 'b') }
        idle.responseMapping { map('c', 'd') }
        idle.paths.isEmpty()
    }

    def "map is safe when no operation is active"() {
        given:
        def service = new RESTService('Svc')

        when:
        def result = service.map('from', 'to')

        then:
        result.is(service)
        service.paths.isEmpty()
    }

    def "operations attach to default path and reuse the last declared path"() {
        given:
        def service = new RESTService('Svc')

        when: 'call HTTP verb before defining any explicit path'
        service.get()

        then: 'a default path is created'
        service.paths[0].path == '/'
        service.paths[0].operations[0].method == 'GET'

        when: 'define a named path without immediately adding an operation'
        service.path('/beta') { }
        service.post()

        then: 'the new operation attaches to the most recent path'
        service.paths[-1].path == '/beta'
        service.paths[-1].operations[0].method == 'POST'

        when: 'manually augment the most recent operation mappings'
        service.paths[-1].operations[0].requestMapping['foo'] = 'bar'
        service.paths[-1].operations[0].responseMapping['baz'] = 'qux'

        then: 'helper accessors surface the latest resource path and mappings'
        service.resourcePath == '/beta'
        service.requestMapping == [foo: 'bar']
        service.responseMapping == [baz: 'qux']
    }

    def "path restores previous delegate context"() {
        given:
        def service = new RESTService('Svc')
        PegaDslCore.CURRENT_DELEGATE.set('sentinel')

        when:
        service.path('/withPrev') { }

        then:
        PegaDslCore.CURRENT_DELEGATE.get() == 'sentinel'

        when:
        PegaDslCore.CURRENT_DELEGATE.remove()
        service.path('/withoutPrev') { }

        then:
        PegaDslCore.CURRENT_DELEGATE.get() == null
    }

    def "call executes provided closure and ignores null"() {
        given:
        def service = new RESTService('Svc')

        when:
        service {
            servicePackage('pkg')
            path('/callable') {
                get()
                header('Scoped', 'y')
            }
        }

        then:
        service.servicePackage == 'pkg'
        service.paths*.path == ['/callable']
        service.paths[0].operations[0].properties['Scoped'] == 'y'

        when: 'calling with null closure hits the defensive branch'
        service.call(null)

        then:
        service.paths.size() == 1
    }

    def "resourcePath handles unset state and active path context"() {
        given:
        def service = new RESTService('Svc')
        String insidePath

        when:
        def initialPath = service.resourcePath
        service.path('/transient') {
            insidePath = resourcePath
        }

        then:
        initialPath == null
        insidePath == '/transient'
        service.resourcePath == '/transient'
    }

    def "mapping accessors fall back to empty maps"() {
        given:
        def service = new RESTService('Svc')

        expect:
        service.requestMapping == [:]
        service.responseMapping == [:]

        when: 'a path is defined without operations'
        service.path('/lonely') { }

        then:
        service.paths[-1].operations.isEmpty()
        service.requestMapping == [:]
        service.responseMapping == [:]
    }

    def "request/response mapping closures tolerate null inputs"() {
        given:
        def service = new RESTService('Svc')
        service.path('/nullables') {
            get()
        }

        when:
        service.requestMapping((Closure) null)
        service.responseMapping((Closure) null)

        then:
        service._currentMappingMode == null
        service.requestMapping.isEmpty()
        service.responseMapping.isEmpty()
    }
}

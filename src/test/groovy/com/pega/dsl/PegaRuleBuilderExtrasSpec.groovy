package com.pega.dsl

import spock.lang.Specification

class PegaRuleBuilderExtrasSpec extends Specification {

    def "other builders return expected rule objects"() {
        given:
        def b = new PegaRuleBuilder()

        when:
        def tree = b.decisionTree('DT1') { description 't' }
        def dp = b.dataPage('DP1') { description 'p' }
        def whenC = b.when('W1') { description 'w' }
        def dt = b.dataTransform('DX1') { description 'dx' }
        def prop = b.property('Prop1') { description 'prop' }
        def sec = b.section('S1') { }
        def flow = b.flow('F1') { }
        def corr = b.correspondence('C1') { description 'c' }
        // RESTService doesn't expose a 'url' method; use description to exercise the closure
        def rs = b.restService('RS1') { description 'u' }
        def tc = b.testCase('T1') { description 'tcase' }
        def ag = b.accessGroup('AG1') { role 'r1' }
        def ar = b.accessRole('AR1') { }
        def db = b.database('DB1') { description 'db' }
        def ap = b.authenticationProfile('AP1') { description 'ap' }
        def sc = b.soapConnector('SC1') { wsdl 'http://w' }

        then:
        tree instanceof DecisionTree
        tree.name == 'DT1'

        dp instanceof DataPage
        dp.name == 'DP1'

        whenC instanceof WhenCondition
        whenC.name == 'W1'

        dt instanceof DataTransform
        dt.name == 'DX1'

        prop instanceof Property
        prop.name == 'Prop1'

        sec instanceof Section
        sec.name == 'S1'

        flow instanceof Flow
        flow.name == 'F1'

        corr instanceof Correspondence
        corr.name == 'C1'

        rs instanceof RESTService
        rs.name == 'RS1'

        tc instanceof TestCase
        tc.name == 'T1'

        ag instanceof AccessGroup
        ag.name == 'AG1'
        ag.roles.contains('r1')

        ar instanceof AccessRole
        ar.name == 'AR1'

        db instanceof Database
        db.name == 'DB1'

        ap instanceof AuthenticationProfile
        ap.name == 'AP1'

        sc instanceof SOAPConnector
        sc.name == 'SC1'
        sc.wsdlUrl == 'http://w'
    }
}

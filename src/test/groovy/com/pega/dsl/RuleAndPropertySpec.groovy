package com.pega.dsl

import groovy.lang.MissingMethodException
import spock.lang.Specification

class RuleAndPropertySpec extends Specification {

    def "rule mutators update state and support chaining"() {
        when:
        def rule = new Rule()
        rule.description('Base description')
            .setVersion('1.2.3')
            .setDescription('override description')
        rule.property('pxObjClass', 'SampleClass')
        rule.parameter('limit', 5)
        rule.setStatus('Available')
        rule.setAvailable(false)
        rule.className('com.pega.sample.RuleClass')

        then:
        rule.description == 'override description'
        rule.version == '1.2.3'
        rule.properties['pxObjClass'] == 'SampleClass'
        rule.parameters['limit'] == 5
        rule.status == 'Available'
        !rule.isAvailable
        rule.className == 'com.pega.sample.RuleClass'
    }

    def "property constructors set defaults"() {
        when:
        def unnamed = new Property()
        def named = new Property('CustomerID')

        then:
        unnamed.type == 'Property'
        unnamed.name == null
        !unnamed.isRequired
        unnamed.validValues.isEmpty()
        named.type == 'Property'
        named.name == 'CustomerID'
    }

    def "authentication profile oauth2 configuration populates fields"() {
        when:
        def profile = new AuthenticationProfile('CRM_OAuth')
        profile.oauth2('client-123', 'secret-xyz', 'https://auth/token')

        then:
        profile.type == 'OAuth 2.0'
        profile.properties['type'] == 'OAuth2'
        profile.properties['clientId'] == 'client-123'
        profile.properties['clientSecret'] == 'secret-xyz'
        profile.properties['tokenUrl'] == 'https://auth/token'
    }

    def "harness element dynamic accessors map to parameters"() {
        given:
        def element = new HarnessElement(type: 'Text', content: 'Hello')

        when:
        element.parameter('layout', '2-col')
        element.dynamicFlag = 42
        def retrieved = element.dynamicFlag
        element.buttonText('Continue')

        then:
        element.parameters['layout'] == '2-col'
        element.parameters['dynamicFlag'] == '42'
        retrieved == '42'
        element.parameters['buttonText'] == 'Continue'

        when:
        element.invalidCall('a', 'b')

        then:
        thrown(MissingMethodException)
    }
}

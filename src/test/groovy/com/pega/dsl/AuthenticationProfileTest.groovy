package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class AuthenticationProfileTest extends Specification {

    def "should create OAuth 2.0 authentication profile"() {
        when:
        def profile = authenticationProfile('MyOAuth2Profile') {
            description 'OAuth 2.0 profile for external service'
            oauth2('test-client-id', 'test-client-secret', 'https://auth.example.com/token')
        }

        then:
        profile.name == 'MyOAuth2Profile'
        profile.description == 'OAuth 2.0 profile for external service'
        profile.type == 'OAuth 2.0'
        profile.properties['clientId'] == 'test-client-id'
        profile.properties['clientSecret'] == 'test-client-secret'
        profile.properties['tokenUrl'] == 'https://auth.example.com/token'
    }

    def "should handle basic properties"() {
        when:
        def profile = authenticationProfile('BasicAuth') {
            description 'A simple profile'
            // Assuming other types might be added later
        }

        then:
        profile.name == 'BasicAuth'
        profile.description == 'A simple profile'
        // Default type before a specific auth method is called
        profile.type == 'AuthenticationProfile'
    }
}

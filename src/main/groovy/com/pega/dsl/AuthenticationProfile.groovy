package com.pega.dsl

class AuthenticationProfile extends Rule {
    AuthenticationProfile() { this.type = 'AuthenticationProfile' }
    AuthenticationProfile(String name) { this.name = name; this.type = 'AuthenticationProfile' }

    def oauth2(String clientId, String clientSecret, String tokenUrl) {
        // Provide the canonical type string expected by tests
        this.type = 'OAuth 2.0'
        properties['type'] = 'OAuth2'
        properties['clientId'] = clientId
        properties['clientSecret'] = clientSecret
        properties['tokenUrl'] = tokenUrl
        return this
    }
}


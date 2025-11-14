package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class SOAPConnectorTest extends Specification {

    def "should create SOAP connector with basic properties"() {
        when:
        def connector = soapConnector('GetWeather') {
            description 'Get weather data from a SOAP service'
            wsdl 'http://api.weather.com/service?wsdl'
            namespace 'http://weather.com/ws'
            operation 'GetForecast'
        }

        then:
        connector.name == 'GetWeather'
        connector.description == 'Get weather data from a SOAP service'
        connector.wsdlUrl == 'http://api.weather.com/service?wsdl'
        connector.namespace == 'http://weather.com/ws'
        connector.operation == 'GetForecast'
    }

    def "should configure request mapping for SOAP connector"() {
        when:
        def connector = soapConnector('UpdateLead') {
            operation 'updateLead'
            requestMapping {
                map '.Lead.ID', 'leadID'
                map '.Lead.Status', 'leadStatus'
                set 'updateSource', '"PegaCRM"'
            }
        }

        then:
        connector.requestMapping.size() == 3
        connector.requestMapping['leadID'] == '.Lead.ID'
        connector.requestMapping['leadStatus'] == '.Lead.Status'
        connector.requestMapping['updateSource'] == '"PegaCRM"'
    }

    def "should configure response mapping for SOAP connector"() {
        when:
        def connector = soapConnector('GetAccountDetails') {
            operation 'getAccount'
            responseMapping {
                map 'account.number', '.Account.Number'
                map 'account.balance.amount', '.Account.Balance'
                map 'account.balance.currency', '.Account.Currency'
                set '.LastUpdated', '@now()'
            }
        }

        then:
        connector.responseMapping.size() == 4
        connector.responseMapping['.Account.Number'] == 'account.number'
        connector.responseMapping['.Account.Balance'] == 'account.balance.amount'
        connector.responseMapping['.LastUpdated'] == '@now()'
    }

    def "should create a comprehensive SOAP connector"() {
        when:
        def connector = soapConnector('SubmitClaim') {
            description 'Submit insurance claim to the main system'
            wsdl 'https://claims.internal/service?wsdl'
            namespace 'http://claims.internal/submit'
            operation 'SubmitClaim'
            authentication 'ClaimsSystemAuth'
            
            header 'X-Request-ID', 'UUID()'
            
            requestMapping {
                map '.Claim.ID', 'claimId'
                map '.Claim.PolicyNumber', 'policyNo'
                map '.Claim.Amount', 'claimAmount'
                map '.Claimant.FirstName', 'claimant.fName'
                map '.Claimant.LastName', 'claimant.lName'
            }
            
            responseMapping {
                map 'submissionStatus.id', '.ConfirmationID'
                map 'submissionStatus.status', '.SubmissionStatus'
            }
        }

        then:
        connector.name == 'SubmitClaim'
        connector.authProfile == 'ClaimsSystemAuth'
        connector.headers.containsKey('X-Request-ID')
        connector.requestMapping.size() == 5
        connector.responseMapping.size() == 2
        connector.requestMapping['claimId'] == '.Claim.ID'
        connector.responseMapping['.ConfirmationID'] == 'submissionStatus.id'
    }
}

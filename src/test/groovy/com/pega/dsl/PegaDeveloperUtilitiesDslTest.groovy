package com.pega.dsl

import spock.lang.Specification
import com.pega.dsl.PegaDeveloperUtilitiesDsl

/**
 * Comprehensive test suite for Pega Developer Utilities DSL
 */
class PegaDeveloperUtilitiesDslTest extends Specification {

    PegaRuleBuilder pega = PegaDeveloperUtilitiesDsl.pega()

    def "should create application with rulesets and rules"() {
        when:
        def app = pega.application('TestApp') {
            description 'Test Application'
            
            ruleset('TestRuleset') {
                description 'Test Ruleset'
                
                rule('activity', 'TestActivity') {
                    description 'Test activity'
                    propertySet '.TestProp', 'TestValue'
                }
            }
        }
        
        then:
        app.name == 'TestApp'
        app.description == 'Test Application'
        app.rulesets.size() == 1
        app.rulesets[0] == 'TestRuleset'
    }

    def "should create activity with steps"() {
        when:
        def activity = pega.activity('ProcessData') {
            description 'Process customer data'
            propertySet '.CustomerID', 'param.ID'
            loadDataPage 'D_CustomerData', [CustomerID: '.CustomerID']
            commit()
        }
        
        then:
        activity.name == 'ProcessData'
        activity.description == 'Process customer data'
        activity.steps.size() == 3
        activity.steps[0].method == 'Property-Set'
        activity.steps[1].method == 'Load-DataPage'
        activity.steps[2].method == 'Commit'
    }

    def "should create when condition with multiple clauses"() {
        when:
        def whenCond = pega.when('IsVIPCustomer') {
            description 'Check if customer is VIP'
            and '.Customer.Tier == Gold'
            or '.Customer.Tier == Platinum'
            and '.Customer.Status == Active'
        }
        
        then:
        whenCond.name == 'IsVIPCustomer'
        whenCond.conditions.size() == 3
        whenCond.conditions[1].connector == 'OR'
    }

    def "should create decision table with conditions and results"() {
        when:
        def table = pega.decisionTable('CustomerRouting') {
            description 'Route customers based on tier and priority'
            
            conditions(['.Customer.Tier', '.Priority'])
            results(['.RouteTo', '.SLA'])
            
            row([
                '.Customer.Tier': 'Gold',
                '.Priority': 'High',
                '.RouteTo': 'SeniorTeam',
                '.SLA': '4'
            ])
        }
        
        then:
        table.name == 'CustomerRouting'
        table.conditions.size() == 2
        table.results.size() == 2
        table.rows.size() == 1
    }

    def "should create decision tree with branches"() {
        when:
        def tree = pega.decisionTree('CustomerEscalation') {
            description 'Escalation decision tree'
            
            root {
                condition '.RequestType', '=='
                branch('Technical') {
                    condition '.Priority', '=='
                    branch('High', 'EscalateToSeniorTech')
                }
                branch('Billing', 'RouteToBilling')
            }
        }
        
        then:
        tree.name == 'CustomerEscalation'
        tree.rootNode.property == '.RequestType'
        tree.rootNode.children.size() == 2
    }

    def "should create data page with activity source"() {
        when:
        def dataPage = pega.dataPage('D_CustomerList') {
            description 'Customer master data'
            sourceActivity 'LoadCustomers', [Status: 'Active']
            scope 'Node'
        }
        
        then:
        dataPage.name == 'D_CustomerList'
        dataPage.sourceType == 'Activity'
        dataPage.dataSource == 'LoadCustomers'
        dataPage.sourceParameters['Status'] == 'Active'
    }

    def "should create data transform with actions"() {
        when:
        def transform = pega.dataTransform('DT_CustomerData') {
            description 'Transform customer data'
            
            set '.ProcessedDate', '@basedate'
            
            when(if: '.Customer.Tier == null', then: {
                set '.Customer.Tier', '"Bronze"'
            })
            
            forEach(in: '.Orders', do: {
                set '.ProcessedFlag', 'true'
            })
        }
        
        then:
        transform.name == 'DT_CustomerData'
        transform.actions.size() == 3
        transform.actions[1].type == 'When'
        transform.actions[2].type == 'For Each Page In'
    }

    def "should create property with validation"() {
        when:
        def property = pega.property('CustomerID') {
            description 'Customer identifier'
            text(32)
            required()
        }
        
        then:
        property.name == 'CustomerID'
        property.propertyType == 'Text (32)'
        property.isRequired == true
    }

    def "should create section with UI elements"() {
        when:
        def section = pega.section('CustomerForm') {
            description 'Customer data entry form'
        }
        
        then:
        section.name == 'CustomerForm'
        section.description == 'Customer data entry form'
    }

    def "should create flow with shapes and connectors"() {
        when:
        def flow = pega.flow('CustomerProcess') {
            description 'Customer processing flow'
            start 'Begin'
            assignment 'Data Entry'
            end 'Complete'
            connect 'Begin', 'Data Entry'
        }
        
        then:
        flow.name == 'CustomerProcess'
        flow.shapes.size() == 3
        flow.connectors.size() == 1
    }

    def "should create REST connector with mappings"() {
        when:
        def connector = pega.restConnector('CustomerAPI') {
            description 'External customer API'
            url 'https://api.example.com/customers/{id}'
            get()
        }
        
        then:
        connector.name == 'CustomerAPI'
        connector.url == 'https://api.example.com/customers/{id}'
        connector.method == 'GET'
    }
}

package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

/**
 * Comprehensive test suite for When Condition rules
 * Tests all aspects of When condition DSL functionality
 */
class WhenConditionTest extends Specification {

    def "should create basic when condition with single clause"() {
        when:
        def whenCondition = when('BasicCondition') {
            description 'Simple customer status check'
            and '.Customer.Status == Active'
        }
        
        then:
        whenCondition.name == 'BasicCondition'
        whenCondition.type == 'When'
        whenCondition.description == 'Simple customer status check'
        whenCondition.conditions.size() == 1
        
        def clause = whenCondition.conditions[0]
        clause.property == '.Customer.Status'
        clause.operator == '=='
        clause.value == 'Active'
        clause.connector == 'AND'
    }

    def "should create when condition with multiple AND clauses"() {
        when:
        def whenCondition = when('CustomerValidation') {
            description 'Validate customer eligibility'
            and '.Customer.Status == Active'
            and '.Customer.CreditScore > 700'
            and '.Customer.Income >= 50000'
        }
        
        then:
        whenCondition.name == 'CustomerValidation'
        whenCondition.conditions.size() == 3
        
        whenCondition.conditions[0].property == '.Customer.Status'
        whenCondition.conditions[1].property == '.Customer.CreditScore'
        whenCondition.conditions[2].property == '.Customer.Income'
    }

    def "should create when condition with mixed AND/OR clauses"() {
        when:
        def whenCondition = when('VIPCustomerCheck') {
            description 'Check if customer qualifies for VIP status'
            and '.Customer.Tier == Gold'
            or '.Customer.Tier == Platinum'
            and '.Customer.Status == Active'
        }
        
        then:
        whenCondition.name == 'VIPCustomerCheck'
        whenCondition.conditions.size() == 3
        
        whenCondition.conditions[0].connector == 'AND'
        whenCondition.conditions[1].connector == 'OR'
        whenCondition.conditions[2].connector == 'AND'
    }

    def "should handle various comparison operators"() {
        when:
        def whenCondition = when('OperatorTest') {
            description 'Test all comparison operators'
            and '.Amount == 1000'
            and '.Quantity != 0'
            and '.Price > 100'
            and '.Discount < 0.5'
            and '.MinAmount >= 50'
            and '.MaxAmount <= 5000'
        }
        
        then:
        whenCondition.conditions.size() == 6
        whenCondition.conditions[0].operator == '=='
        whenCondition.conditions[1].operator == '!='
        whenCondition.conditions[2].operator == '>'
        whenCondition.conditions[3].operator == '<'
        whenCondition.conditions[4].operator == '>='
        whenCondition.conditions[5].operator == '<='
    }

    def "should handle property expressions and functions"() {
        when:
        def whenCondition = when('ExpressionTest') {
            description 'Test property expressions and functions'
            and '@today() >= .StartDate'
            and 'LENGTH(.Description) > 10'
        }
        
        then:
        whenCondition.conditions.size() == 2
        whenCondition.conditions[0].property == '@today()'
        whenCondition.conditions[1].property == 'LENGTH(.Description)'
    }

    def "should create complex business logic when condition"() {
        when:
        def whenCondition = when('LoanEligibility') {
            description 'Determine loan eligibility based on multiple criteria'
            and '.Employment.Status == Employed'
            and '.Employment.AnnualIncome >= 40000'
            or '.Credit.Score >= 720'
            and '.Financial.DebtToIncomeRatio <= 0.4'
        }
        
        then:
        whenCondition.name == 'LoanEligibility'
        whenCondition.conditions.size() == 4
        whenCondition.conditions[2].connector == 'OR'
    }

    def "should handle null and empty value checks"() {
        when:
        def whenCondition = when('ValidationChecks') {
            description 'Validate required fields and empty values'
            and '.CustomerID != null'
            and '.CustomerName != ""'
        }
        
        then:
        whenCondition.conditions.size() == 2
        whenCondition.conditions[0].value == 'null'
        whenCondition.conditions[1].value == '""'
    }

    def "should create when condition with rule inheritance properties"() {
        when:
        def whenCondition = when('InheritedCondition') {
            description 'Test rule inheritance properties'
            className 'MyApp-Customer-Work'
            setStatus 'Final'
            setAvailable true
            
            and '.Status == Active'
            
            setWhenProperty 'RulesetName', 'CustomerRules'
        }
        
        then:
        whenCondition.name == 'InheritedCondition'
        whenCondition.className == 'MyApp-Customer-Work'
        whenCondition.status == 'Final'
        whenCondition.isAvailable == true
        whenCondition.properties['RulesetName'] == 'CustomerRules'
    }
}

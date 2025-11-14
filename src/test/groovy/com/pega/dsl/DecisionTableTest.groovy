package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

/**
 * Comprehensive test suite for Decision Table rules
 * Tests all aspects of Decision Table DSL functionality
 */
class DecisionTableTest extends Specification {

    def "should create basic decision table with conditions and results"() {
        when:
        def table = decisionTable('BasicTable') {
            description 'Simple decision table'
            
            conditions(['.CustomerTier', '.OrderAmount'])
            results(['.DiscountRate', '.ProcessingPriority'])
            
            row([
                '.CustomerTier': 'Gold',
                '.OrderAmount': '>= 1000',
                '.DiscountRate': '0.15',
                '.ProcessingPriority': 'High'
            ])
            
            row([
                '.CustomerTier': 'Silver',
                '.OrderAmount': '>= 500',
                '.DiscountRate': '0.10',
                '.ProcessingPriority': 'Medium'
            ])
        }
        
        then:
        table.name == 'BasicTable'
        table.type == 'DecisionTable'
        table.description == 'Simple decision table'
        table.conditions.size() == 2
        table.results.size() == 2
        table.rows.size() == 2
        
        table.conditions[0] == '.CustomerTier'
        table.conditions[1] == '.OrderAmount'
        table.results[0] == '.DiscountRate'
        table.results[1] == '.ProcessingPriority'
        
        def row1 = table.rows[0]
        row1['.CustomerTier'] == 'Gold'
        row1['.OrderAmount'] == '>= 1000'
        row1['.DiscountRate'] == '0.15'
        row1['.ProcessingPriority'] == 'High'
        
        def row2 = table.rows[1]
        row2['.CustomerTier'] == 'Silver'
        row2['.OrderAmount'] == '>= 500'
        row2['.DiscountRate'] == '0.10'
        row2['.ProcessingPriority'] == 'Medium'
    }

    def "should create comprehensive customer routing decision table"() {
        when:
        def table = decisionTable('CustomerRouting') {
            description 'Route customers to appropriate teams based on multiple criteria'
            
            conditions(['.Customer.Tier', '.Request.Type', '.Request.Priority', '.Customer.LifetimeValue'])
            results(['.RouteTo', '.SLA', '.EscalationLevel', '.SpecialHandling'])
            
            row([
                '.Customer.Tier': 'Platinum',
                '.Request.Type': 'Technical',
                '.Request.Priority': 'High',
                '.Customer.LifetimeValue': '>= 50000',
                '.RouteTo': 'VIPTechnicalTeam',
                '.SLA': '2',
                '.EscalationLevel': '3',
                '.SpecialHandling': 'true'
            ])
            
            row([
                '.Customer.Tier': 'Platinum',
                '.Request.Type': 'Billing',
                '.Request.Priority': 'Any',
                '.Customer.LifetimeValue': '>= 50000',
                '.RouteTo': 'VIPBillingTeam',
                '.SLA': '4',
                '.EscalationLevel': '2',
                '.SpecialHandling': 'true'
            ])
            
            row([
                '.Customer.Tier': 'Gold',
                '.Request.Type': 'Technical',
                '.Request.Priority': 'High',
                '.Customer.LifetimeValue': '>= 25000',
                '.RouteTo': 'SeniorTechnicalTeam',
                '.SLA': '4',
                '.EscalationLevel': '2',
                '.SpecialHandling': 'false'
            ])
            
            row([
                '.Customer.Tier': 'Gold',
                '.Request.Type': 'Technical',
                '.Request.Priority': 'Medium',
                '.Customer.LifetimeValue': '>= 25000',
                '.RouteTo': 'TechnicalTeam',
                '.SLA': '8',
                '.EscalationLevel': '1',
                '.SpecialHandling': 'false'
            ])
            
            row([
                '.Customer.Tier': 'Gold',
                '.Request.Type': 'Billing',
                '.Request.Priority': 'Any',
                '.Customer.LifetimeValue': '>= 25000',
                '.RouteTo': 'BillingTeam',
                '.SLA': '12',
                '.EscalationLevel': '1',
                '.SpecialHandling': 'false'
            ])
            
            row([
                '.Customer.Tier': 'Silver',
                '.Request.Type': 'Technical',
                '.Request.Priority': 'High',
                '.Customer.LifetimeValue': '>= 10000',
                '.RouteTo': 'TechnicalTeam',
                '.SLA': '8',
                '.EscalationLevel': '1',
                '.SpecialHandling': 'false'
            ])
            
            row([
                '.Customer.Tier': 'Silver',
                '.Request.Type': 'Any',
                '.Request.Priority': 'Low',
                '.Customer.LifetimeValue': '< 10000',
                '.RouteTo': 'StandardTeam',
                '.SLA': '24',
                '.EscalationLevel': '0',
                '.SpecialHandling': 'false'
            ])
            
            otherwise([
                '.RouteTo': 'StandardTeam',
                '.SLA': '48',
                '.EscalationLevel': '0',
                '.SpecialHandling': 'false'
            ])
        }
        
        then:
        table.name == 'CustomerRouting'
        table.conditions.size() == 4
        table.results.size() == 4
        table.rows.size() == 8
        
        def defaultRow = table.rows[7]
        defaultRow['.RouteTo'] == 'StandardTeam'
        defaultRow['.SLA'] == '48'
        defaultRow['__otherwise__'] == 'true'
    }

    def "should create financial risk assessment decision table"() {
        when:
        def table = decisionTable('RiskAssessment') {
            description 'Assess financial risk based on multiple factors'
            
            conditions(['.CreditScore', '.Income', '.DebtToIncomeRatio', '.EmploymentYears', '.LoanAmount'])
            results(['.RiskLevel', '.InterestRate', '.RequiresApproval', '.MaxLoanAmount', '.AdditionalReview'])
            
            row([
                '.CreditScore': '>= 750',
                '.Income': '>= 80000',
                '.DebtToIncomeRatio': '<= 0.3',
                '.EmploymentYears': '>= 3',
                '.LoanAmount': '<= 400000',
                '.RiskLevel': 'Low',
                '.InterestRate': '3.25',
                '.RequiresApproval': 'false',
                '.MaxLoanAmount': '500000',
                '.AdditionalReview': 'false'
            ])
            
            row([
                '.CreditScore': '< 600',
                '.Income': 'Any',
                '.DebtToIncomeRatio': 'Any',
                '.EmploymentYears': 'Any',
                '.LoanAmount': 'Any',
                '.RiskLevel': 'Very High',
                '.InterestRate': '0',
                '.RequiresApproval': 'true',
                '.MaxLoanAmount': '0',
                '.AdditionalReview': 'true'
            ])
        }
        
        then:
        table.name == 'RiskAssessment'
        table.rows.size() == 2
    }

    def "should create pricing strategy decision table"() {
        when:
        def table = decisionTable('PricingStrategy') {
            description 'Determine pricing strategy based on customer and product factors'
            
            conditions(['.Product.Category', '.Customer.Segment', '.Order.Quantity', '.Season', '.Inventory.Level'])
            results(['.BaseDiscount', '.VolumeDiscount', '.SeasonalAdjustment', '.UrgencyFee', '.FinalPricingTier'])
            
            row([
                '.Product.Category': 'Premium',
                '.Customer.Segment': 'Enterprise',
                '.Order.Quantity': '>= 100',
                '.Season': 'Any',
                '.Inventory.Level': 'High',
                '.BaseDiscount': '0.15',
                '.VolumeDiscount': '0.05',
                '.SeasonalAdjustment': '0',
                '.UrgencyFee': '0',
                '.FinalPricingTier': 'Tier1'
            ])
        }
        
        then:
        table.rows.size() == 1
    }

    def "should handle complex condition expressions"() {
        when:
        def table = decisionTable('ComplexConditions') {
            description 'Decision table with complex condition expressions'
            
            conditions(['.Customer.Age', '.Account.Balance', 'LENGTH(.Customer.Name)', '@today() - .Account.OpenDate'])
            results(['.ApprovalRequired'])
            
            row([
                '.Customer.Age': '>= 65',
                '.Account.Balance': '>= 10000',
                'LENGTH(.Customer.Name)': '> 5',
                '@today() - .Account.OpenDate': '>= 365',
                '.ApprovalRequired': 'false'
            ])
        }
        
        then:
        table.conditions.size() == 4
        table.conditions[2] == 'LENGTH(.Customer.Name)'
        table.conditions[3] == '@today() - .Account.OpenDate'
    }

    def "should create decision table with rule inheritance properties"() {
        when:
        def table = decisionTable('InheritedTable') {
            description 'Table with rule inheritance properties'
            className 'MyApp-Customer-Work'
            setStatus 'Final'
            setAvailable true
            
            setTableProperty 'RulesetName', 'DecisionRules'
            setTableProperty 'BusinessPurpose', 'Customer classification'
            
            conditions(['.Revenue', '.Years'])
            results(['.Category'])
            
            row([
                '.Revenue': '>= 1000000',
                '.Years': '>= 5',
                '.Category': 'Enterprise'
            ])
        }
        
        then:
        table.name == 'InheritedTable'
        table.className == 'MyApp-Customer-Work'
        table.status == 'Final'
        table.isAvailable == true
        table.properties['RulesetName'] == 'DecisionRules'
    }

    def "should handle otherwise and default scenarios"() {
        when:
        def table = decisionTable('DefaultScenarios') {
            description 'Table with default and otherwise scenarios'
            
            conditions(['.Priority', '.Type'])
            results(['.Handler', '.Timeout'])
            
            row([
                '.Priority': 'Critical',
                '.Type': 'Security',
                '.Handler': 'SecurityTeam',
                '.Timeout': '15'
            ])
            
            otherwise([
                '.Handler': 'GeneralSupport',
                '.Timeout': '240'
            ])
        }
        
        then:
        table.rows.size() == 2
        def defaultRow = table.rows[1]
        defaultRow['.Handler'] == 'GeneralSupport'
        defaultRow['__otherwise__'] == 'true'
    }

    def "should create multi-tier approval decision table"() {
        when:
        def table = decisionTable('ApprovalMatrix') {
            description 'Multi-tier approval decision matrix'
            
            conditions(['.RequestType', '.Amount'])
            results(['.Level1Approver', '.AutoApprove'])
            
            row([
                '.RequestType': 'Capital',
                '.Amount': '>= 100000',
                '.Level1Approver': 'CFO',
                '.AutoApprove': 'false'
            ])
            
            row([
                '.RequestType': 'Operational',
                '.Amount': '< 1000',
                '.Level1Approver': 'System',
                '.AutoApprove': 'true'
            ])
        }
        
        then:
        table.rows.size() == 2
    }
}

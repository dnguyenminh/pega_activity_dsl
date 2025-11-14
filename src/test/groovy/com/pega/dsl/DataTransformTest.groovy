package com.pega.dsl

import spock.lang.Specification
import com.pega.dsl.PegaDeveloperUtilitiesDsl

/**
 * Comprehensive test suite for Data Transform rules
 * Tests all aspects of Data Transform DSL functionality
 */
class DataTransformTest extends Specification {

    PegaRuleBuilder pega = PegaDeveloperUtilitiesDsl.pega()

    def "should create basic data transform with simple set actions"() {
        when:
        def transform = pega.dataTransform('BasicTransform') {
            description 'Simple data transformation'
            set '.CustomerName', '.SourceName'
            set '.ProcessedDate', '@basedate'
            set '.Status', '"Active"'
        }
        
        then:
        transform.name == 'BasicTransform'
        transform.type == 'DataTransform'
        transform.description == 'Simple data transformation'
        transform.actions.size() == 3
        
        transform.actions[0].type == 'Set'
        transform.actions[0].target == '.CustomerName'
        transform.actions[0].source == '.SourceName'
        
        transform.actions[1].type == 'Set'
        transform.actions[1].target == '.ProcessedDate'
        transform.actions[1].source == '@basedate'
        
        transform.actions[2].type == 'Set'
        transform.actions[2].target == '.Status'
        transform.actions[2].source == '"Active"'
    }

    def "should create data transform with various action types"() {
        when:
        def transform = pega.dataTransform('ComprehensiveTransform') {
            description 'Test all data transform action types'
            
            // Set actions
            set '.CustomerID', '.SourceID'
            set '.CreatedDate', '@now()'
            
            // Apply data transform
            applyDataTransform 'DT_CommonFields'
            
            // Append action
            appendTo '.Notes', '.AdditionalComments'
            
            // Remove action
            remove '.TemporaryField'
        }
        
        then:
        transform.actions.size() == 5
        
        transform.actions[0].type == 'Set'
        transform.actions[1].type == 'Set'
        transform.actions[2].type == 'Apply-DataTransform'
        transform.actions[3].type == 'Append to'
        transform.actions[4].type == 'Remove'
    }

    def "should create data transform with conditional when blocks"() {
        when:
        def transform = pega.dataTransform('ConditionalTransform') {
            description 'Data transform with conditional logic'
            
            set '.BaseStatus', '"Pending"'
            
            when(if: '.Customer.Type == "Premium"', then: {
                set '.Priority', '"High"'
                set '.SLA', '4'
                set '.AssignedTeam', '"PremiumSupport"'
            })
            
            when(if: '.Customer.Tier == null', then: {
                set '.Customer.Tier', '"Bronze"'
                set '.DefaultDiscount', '0.05'
            })
            
            set '.ProcessedFlag', 'true'
        }
        
        then:
        transform.actions.size() == 4
        transform.actions[1].type == 'When'
        transform.actions[1].condition == '.Customer.Type == "Premium"'
        transform.actions[1].children.size() == 3
        
        transform.actions[2].type == 'When'
        transform.actions[2].condition == '.Customer.Tier == null'
        transform.actions[2].children.size() == 2
    }

    def "should create data transform with for-each loops"() {
        when:
        def transform = pega.dataTransform('LoopTransform') {
            description 'Data transform with for-each loops'
            
            set '.OrderTotal', '0'
            
            forEach(in: '.OrderItems', do: {
                set '.ExtendedPrice', '.Quantity * .UnitPrice'
                set '.TaxAmount', '.ExtendedPrice * .TaxRate'
                set '.ProcessedFlag', 'true'
            })
            
            forEach(in: '.Payments', do: {
                set '.ProcessedDate', '@now()'
                set '.Status', '"Processed"'
            })
            
            set '.FinalizedDate', '@basedate'
        }
        
        then:
        transform.actions.size() == 4
        transform.actions[1].type == 'For Each Page In'
        transform.actions[1].target == '.OrderItems'
        transform.actions[1].children.size() == 3

        transform.actions[2].type == 'For Each Page In'
        transform.actions[2].target == '.Payments'
        transform.actions[2].children.size() == 2
    }

    def "should create complex business data transform"() {
        when:
        def transform = pega.dataTransform('CustomerProcessing') {
            description 'Complex customer data processing transform'
            
            set '.ProcessedDateTime', '@now()'
            set '.ProcessedBy', '@operator.pyUserName'
            
            when(if: '.Customer.FirstName != null && .Customer.LastName != null', then: {
                set '.Customer.FullName', '.Customer.FirstName + " " + .Customer.LastName'
                set '.Customer.DisplayName', 'UPPER(.Customer.LastName) + ", " + .Customer.FirstName'
            })
            
            when(if: '.Customer.Tier == null || .Customer.Tier == ""', then: {
                set '.Customer.Tier', '"Bronze"'
                set '.Customer.TierAssignedDate', '@today()'
                set '.Customer.DefaultDiscountRate', '0.02'
            })
            
            forEach(in: '.Customer.Addresses', do: {
                set '.FormattedAddress', '.Street + ", " + .City + ", " + .State + " " + .ZipCode'
                set '.Validated', 'false'
                
                when(if: '.Type == "Primary"', then: {
                    set '.Customer.PrimaryAddress', '.FormattedAddress'
                })
            })
            
            set '.Customer.TotalOrderValue', '0'
            set '.Customer.OrderCount', '0'
            
            forEach(in: '.Customer.Orders', do: {
                set '.Customer.TotalOrderValue', '.Customer.TotalOrderValue + .TotalAmount'
                set '.Customer.OrderCount', '.Customer.OrderCount + 1'
                
                when(if: '.Status == "Completed"', then: {
                    set '.CompletedDate', '@today()'
                })
            })
            
            when(if: '.Customer.TotalOrderValue >= 10000', then: {
                set '.Customer.Category', '"VIP"'
                set '.Customer.SpecialHandling', 'true'
            })
            
            when(if: '.Customer.TotalOrderValue >= 5000 && .Customer.TotalOrderValue < 10000', then: {
                set '.Customer.Category', '"Preferred"'
                set '.Customer.PreferredBenefits', 'true'
            })
            
            when(if: '.Customer.TotalOrderValue < 5000', then: {
                set '.Customer.Category', '"Standard"'
            })
            
            applyDataTransform 'DT_AuditFields'
            applyDataTransform 'DT_ValidationRules', '.Customer', '.Customer'
            
            set '.ProcessingComplete', 'true'
            set '.ReadyForReview', '.Customer.Category == "VIP"'
        }
        
        then:
        transform.actions.size() == 15
    }

    def "should handle complex expressions and calculations"() {
        when:
        def transform = pega.dataTransform('CalculationTransform') {
            description 'Transform with complex calculations'
            
            set '.SubTotal', '.Quantity * .UnitPrice'
            set '.TaxAmount', 'ROUND(.SubTotal * .TaxRate, 2)'
            set '.DiscountAmount', '.SubTotal * .DiscountPercent'
            set '.Total', '.SubTotal + .TaxAmount - .DiscountAmount'
            
            set '.FormattedPhone', 'REGEX(.PhoneNumber, "[^0-9]", "", "g")'
            set '.UppercaseName', 'UPPER(.CustomerName)'
            set '.InitialsCaps', 'PROPER(.Description)'
            
            set '.ExpiryDate', '@today() + .ValidityPeriod'
            set '.AgeInYears', 'FLOOR((@today() - .BirthDate) / 365.25)'
            set '.IsExpired', '.ExpiryDate < @today()'
            
            set '.Status', '.IsActive ? "Active" : "Inactive"'
            set '.Category', '.Score >= 90 ? "Excellent" : (.Score >= 70 ? "Good" : "Needs Improvement")'
        }
        
        then:
        transform.actions.size() == 12
    }

    def "should handle nested page structures and references"() {
        when:
        def transform = pega.dataTransform('NestedPageTransform') {
            description 'Transform with nested page references'
            
            set '.Customer.Address.Country', '"US"'
            set '.Customer.Contact.Email', 'LOWER(.Customer.Contact.Email)'
            set '.Customer.Preferences.Currency', '"USD"'
            
            set '.Order.LineItems(1).Total', '.Order.LineItems(1).Quantity * .Order.LineItems(1).Price'
            
            forEach(in: '.Customer.Accounts', do: {
                set '.Balance', 'ROUND(.Balance, 2)'
                set '.LastUpdated', '@now()'
                
                when(if: '.Type == "Savings"', then: {
                    set '.InterestRate', '0.025'
                })
                
                when(if: '.Type == "Checking"', then: {
                    set '.InterestRate', '0.001'
                })
            })
            
            forEach(in: '.Tags', do: {
                set '.Normalized', 'LOWER(TRIM(.Value))'
            })
            
            set '.Summary.CustomerInfo', '.Customer.FullName + " (" + .Customer.ID + ")"'
            set '.Summary.TotalAccounts', '.Customer.Accounts.Count'
            set '.Summary.PrimaryAccountBalance', '.Customer.Accounts(1).Balance'
        }
        
        then:
        transform.actions.size() == 9
    }

    def "should create data transform with rule inheritance and metadata"() {
        when:
        def transform = pega.dataTransform('InheritedTransform') {
            description 'Transform with rule inheritance properties'
            className 'MyApp-Customer-Data'
            setStatus 'Final'
            setAvailable true
            
            property 'RulesetName', 'CustomerData'
            property 'RulesetVersion', '01.02.01'
            property 'Purpose', 'Customer data standardization'
            
            set '.StandardizedData', 'true'
            set '.TransformVersion', '"1.2"'
            
            applyDataTransform 'DT_BaseTransform'
            
            when(if: '.RequiresValidation == true', then: {
                set '.ValidationStatus', '"Pending"'
                applyDataTransform 'DT_ValidationRules'
            })
        }
        
        then:
        transform.name == 'InheritedTransform'
        transform.className == 'MyApp-Customer-Data'
        transform.status == 'Final'
        transform.isAvailable == true
        transform.properties['RulesetName'] == 'CustomerData'
        transform.actions.size() == 4
    }

    def "should handle data transform with parameterized apply calls"() {
        when:
        def transform = pega.dataTransform('ParameterizedTransform') {
            description 'Transform with parameterized apply data transform calls'
            
            set '.InitialSetup', 'true'
            
            applyDataTransform 'DT_AddressStandardization', '.Customer.BillingAddress', '.Customer.StandardizedBilling'
            applyDataTransform 'DT_AddressStandardization', '.Customer.ShippingAddress', '.Customer.StandardizedShipping'
            
            applyDataTransform 'DT_CommonValidation'
            
            applyDataTransform 'DT_CustomerEnrichment', '.Customer'
            
            set '.TransformComplete', 'true'
        }
        
        then:
        transform.actions.size() == 6
    }

    def "should handle empty data transform"() {
        when:
        def transform = pega.dataTransform('EmptyTransform') {
            description 'This is an empty transform'
        }

        then:
        transform.name == 'EmptyTransform'
        transform.actions.isEmpty()
    }

    def "should throw error for invalid action"() {
        when:
        pega.dataTransform('InvalidAction') {
            update '.SomeField', 'SomeValue'
        }

        then:
        thrown(MissingMethodException)
    }
}

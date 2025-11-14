package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class TestCaseTest extends Specification {

    def "should create test case with basic properties"() {
        when:
        def test = testCase('TestCustomerValidation') {
            description 'Test the main customer validation rule'
            ruleToTest 'ValidateCustomer'
        }

        then:
        test.name == 'TestCustomerValidation'
        test.description == 'Test the main customer validation rule'
        test.ruleToTest == 'ValidateCustomer'
    }

    def "should configure input data"() {
        when:
        def test = testCase('InputTest') {
            input 'CustomerID', 'C-123'
            input 'CustomerTier', 'Gold'
            input 'OrderAmount', 5000
        }

        then:
        test.inputData.size() == 3
        test.inputData['CustomerID'] == 'C-123'
        test.inputData['OrderAmount'] == 5000
    }

    def "should configure expected results"() {
        when:
        def test = testCase('ExpectTest') {
            expect '.ValidationResult', true
            expect '.ValidationMessage', ''
            expect '.Customer.IsVIP', true
        }

        then:
        test.expectedResults.size() == 3
        test.expectedResults['.ValidationResult'] == true
        test.expectedResults['.ValidationMessage'] == ''
        test.expectedResults['.Customer.IsVIP'] == true
    }

    def "should configure different types of assertions"() {
        when:
        def test = testCase('AssertionTest') {
            assertTrue '.IsValid'
            assertEquals '.Customer.Status', '"Active"'
            assertNotNull '.ProcessedDate'
        }

        then:
        test.assertions.size() == 3
        
        def assert1 = test.assertions[0]
        assert1.type == 'assertTrue'
        assert1.expr == '.IsValid'

        def assert2 = test.assertions[1]
        assert2.type == 'assertEquals'
        assert2.expected == '.Customer.Status'
        assert2.actual == '"Active"'

        def assert3 = test.assertions[2]
        assert3.type == 'assertNotNull'
        assert3.expr == '.ProcessedDate'
    }

    def "should create a comprehensive test case"() {
        when:
        def test = testCase('FullLoanApprovalTest') {
            description 'Test the full loan approval process for a high-value customer'
            ruleToTest 'LoanApprovalFlow'
            
            input 'Applicant.CreditScore', 800
            input 'Applicant.Income', 150000
            input 'Loan.Amount', 750000
            
            expect '.ApprovalStatus', '"Approved"'
            expect '.InterestRate', 3.5
            
            assertTrue '.ApplicationProcessed'
            assertEquals '.AssignedUnderwriter', '"UW_Senior"'
            assertNotNull '.ApprovalDate'
        }

        then:
        test.name == 'FullLoanApprovalTest'
        test.ruleToTest == 'LoanApprovalFlow'
        test.inputData.size() == 3
        test.expectedResults.size() == 2
        test.assertions.size() == 3
        test.inputData['Applicant.CreditScore'] == 800
        test.expectedResults['.InterestRate'] == 3.5
        test.assertions.find { it.type == 'assertEquals' }.expected == '.AssignedUnderwriter'
    }
}

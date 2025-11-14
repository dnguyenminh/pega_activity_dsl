package examples

import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

/**
 * Comprehensive examples of using the Pega Developer Utilities DSL
 */
class PegaDSLExamples {

    /**
     * Example 1: Complete Application Definition
     */
    def static exampleApplication() {
        return application('CustomerServiceApp') {
            version '1.0'
            description 'Customer Service Management Application'
            
            setting 'debug.mode', true
            setting 'timeout.session', 3600
            
            ruleset('CustomerService') {
                version '01.01.01'
                description 'Customer Service Core Rules'
                
                // Activity Rule Example
                rule('activity', 'ProcessCustomerRequest') {
                    description 'Process incoming customer service requests'
                    
                    localVariable 'CustomerID', 'Text'
                    localVariable 'RequestType', 'Text'
                    localVariable 'Priority', 'Text'
                    
                    // Validate input
                    step('Property-Set') {
                        parameter 'PropertyName', '.CustomerID'
                        parameter 'PropertyValue', 'param.CustomerID'
                    }
                    
                    // Load customer data
                    loadDataPage 'D_CustomerData', [CustomerID: '.CustomerID']
                    
                    // Apply business logic
                    applyDataTransform 'DT_ProcessRequest'
                    
                    // Determine priority
                    branch 'DetermineRequestPriority'
                    
                    // Create work item
                    pageNew 'NewRequest', 'Data-CustomerRequest'
                    
                    // Save the request
                    objSave()
                    
                    // Send confirmation email
                    callActivity 'SendConfirmationEmail', [
                        CustomerEmail: '.Customer.Email',
                        RequestID: '.RequestID'
                    ]
                    
                    commit()
                }
                
                // Property Rules
                rule('property', 'CustomerID') {
                    single()
                    text(32)
                    required()
                    validation 'ValidateCustomerID'
                }
                
                rule('property', 'RequestType') {
                    single()
                    text(64)
                    required()
                    validValues(['Technical Support', 'Billing Inquiry', 'Product Question', 'Complaint'])
                }
                
                // Section Rule
                rule('section', 'CustomerRequestForm') {
                    dynamic()
                    
                    input('CustomerID', 'Customer ID') {
                        textInput()
                        required()
                    }
                    
                    input('RequestType', 'Request Type') {
                        dropdown()
                        required()
                    }
                    
                    input('Description', 'Description') {
                        textArea()
                        required()
                    }
                    
                    input('Priority', 'Priority') {
                        radioButtons()
                        visible('.RequestType == "Technical Support"')
                    }
                    
                    button('Submit', 'SubmitRequest') {
                        primary()
                    }
                    
                    button('Cancel', 'CancelRequest') {
                        secondary()
                    }
                }
                
                // Flow Rule
                rule('flow', 'CustomerRequestFlow') {
                    work()
                    
                    start('Begin')
                    
                    assignment('Collect Request Details') {
                        section 'CustomerRequestForm'
                        flowAction 'Submit'
                        flowAction 'Cancel'
                        worklist()
                    }
                    
                    decision('Route Request') {
                        decisionTable 'DT_RequestRouting'
                    }
                    
                    assignment('Technical Review') {
                        section 'TechnicalReviewForm'
                        workbasket 'TechnicalSupport'
                        flowAction 'Approve'
                        flowAction 'Reject'
                        flowAction 'RequestMoreInfo'
                    }
                    
                    utility('Send Notification', 'SendCustomerNotification')
                    
                    end('Complete') {
                        resolved()
                    }
                    
                    connect 'Begin', 'Collect Request Details'
                    connect 'Collect Request Details', 'Route Request', '.Submit'
                    connect 'Route Request', 'Technical Review', '.RequestType == "Technical Support"'
                    connect 'Technical Review', 'Send Notification', '.Approve'
                    connect 'Send Notification', 'Complete'
                }
            }
        }
    }

    /**
     * Example 2: Data Page Configuration
     */
    def static exampleDataPage() {
        return dataPage('D_CustomerData') {
            description 'Customer master data'
            scope 'Node'
            maxAge 1800  // 30 minutes
            refresh 'Reload if older than specified age'
            
            sourceActivity 'LoadCustomerData', [
                CustomerID: 'param.CustomerID'
            ]
        }
    }

    /**
     * Example 3: Data Transform
     */
    def static exampleDataTransform() {
        return dataTransform('DT_ProcessCustomerRequest') {
            description 'Transform customer request data'
            
            set '.ProcessedDate', '@basedate'
            set '.Status', '"Open"'
            set '.AssignedTo', '@requestor.pyUserIdentifier'
            
            when(if: '.Priority == null', then: {
                set '.Priority', '"Medium"'
            })
            
            when(if: '.RequestType == "Technical Support"', then: {
                set '.Category', '"Technical"'
                set '.SLA', '24'
            })
            
            when(if: '.RequestType == "Billing Inquiry"', then: {
                set '.Category', '"Financial"'
                set '.SLA', '48'
            })
            
            forEach(in: '.Attachments', do: {
                set '.ProcessedFlag', 'true'
                set '.ProcessedBy', '@requestor.pyUserIdentifier'
            })
            
            applyDataTransform 'DT_AuditFields'
        }
    }

    /**
     * Example 4: Decision Table
     */
    def static exampleDecisionTable() {
        return decisionTable('DT_RequestRouting') {
            description 'Route customer requests based on type and priority'
            
            condition '.RequestType'
            condition '.Priority'
            condition '.Customer.Tier'
            
            result '.RouteTo'
            result '.SLA'
            result '.AutoEscalate'
            
            row([
                '.RequestType': 'Technical Support',
                '.Priority': 'High',
                '.Customer.Tier': 'Gold',
                '.RouteTo': 'SeniorTechSupport',
                '.SLA': '4',
                '.AutoEscalate': 'true'
            ])
            
            row([
                '.RequestType': 'Technical Support',
                '.Priority': 'Medium',
                '.Customer.Tier': '*',
                '.RouteTo': 'TechSupport',
                '.SLA': '24',
                '.AutoEscalate': 'false'
            ])
            
            row([
                '.RequestType': 'Billing Inquiry',
                '.Priority': '*',
                '.Customer.Tier': '*',
                '.RouteTo': 'BillingTeam',
                '.SLA': '48',
                '.AutoEscalate': 'false'
            ])
        }
    }

    /**
     * Example 5: When Condition
     */
    def static exampleWhenCondition() {
        return when('IsHighPriorityCustomer') {
            description 'Check if customer is high priority'
            
            condition '.Customer.Tier', '==', 'Gold'
            or '.Customer.Tier', '==', 'Platinum'
            or '.Customer.AccountValue', '>', '100000'
            and '.Customer.Status', '==', 'Active'
        }
    }

    /**
     * Example 6: REST Connector
     */
    def static exampleRESTConnector() {
        return restConnector('GetCustomerProfile') {
            description 'Retrieve customer profile from external CRM'
            
            url 'https://api.customercrm.com/v1/customers/{CustomerID}'
            get()
            
            authentication 'CRM_OAuth2'
            
            header 'Accept', 'application/json'
            header 'Content-Type', 'application/json'
            
            requestMapping {
                map '.CustomerID', 'CustomerID'
            }
            
            responseMapping {
                map 'customer.firstName', '.Customer.FirstName'
                map 'customer.lastName', '.Customer.LastName'
                map 'customer.email', '.Customer.Email'
                map 'customer.tier', '.Customer.Tier'
                map 'customer.accountValue', '.Customer.AccountValue'
            }
        }
    }

    /**
     * Example 7: Complex Section with Grid
     */
    def static exampleComplexSection() {
        return section('CustomerRequestHistory') {
            description 'Display customer request history'
            
            smartLayout()
            
            label 'Customer Request History', 'Customer.Name'
            
            repeatingGrid('.RequestHistory') {
                column('RequestID', 'Request ID') {
                    link()
                    sortable()
                    width(120)
                }
                
                column('RequestType', 'Type') {
                    textInput()
                    filterable()
                    width(150)
                }
                
                column('Status', 'Status') {
                    dropdown()
                    filterable()
                    width(100)
                }
                
                column('Priority', 'Priority') {
                    textInput()
                    sortable()
                    width(80)
                }
                
                column('CreatedDate', 'Created') {
                    textInput()
                    sortable()
                    width(120)
                }
                
                column('Actions', 'Actions') {
                    button()
                    width(100)
                }
            }
            
            button('Export', 'ExportHistory') {
                secondary()
            }
            
            button('Refresh', 'RefreshHistory') {
                simple()
            }
        }
    }

    /**
     * Example 8: Correspondence Rule
     */
    def static exampleCorrespondence() {
        return correspondence('CustomerRequestConfirmation') {
            description 'Email confirmation for customer requests'
            
            html()
            
            subject 'Your Request #{RequestID} has been received'
            
            body '''
                <html>
                <body>
                    <h2>Request Confirmation</h2>
                    <p>Dear #{CustomerName},</p>
                    
                    <p>Thank you for contacting our customer service team. We have received your #{RequestType} request.</p>
                    
                    <table border="1" style="border-collapse: collapse;">
                        <tr><td><strong>Request ID:</strong></td><td>#{RequestID}</td></tr>
                        <tr><td><strong>Request Type:</strong></td><td>#{RequestType}</td></tr>
                        <tr><td><strong>Priority:</strong></td><td>#{Priority}</td></tr>
                        <tr><td><strong>Expected Resolution:</strong></td><td>#{ExpectedResolution}</td></tr>
                    </table>
                    
                    <p>We will contact you within #{SLA} hours with an update.</p>
                    
                    <p>Best regards,<br>Customer Service Team</p>
                </body>
                </html>
            '''
            
            parameter 'CustomerName', 'Customer Name'
            parameter 'RequestID', 'Request ID'
            parameter 'RequestType', 'Request Type'
            parameter 'Priority', 'Priority Level'
            parameter 'SLA', 'Service Level Agreement'
            parameter 'ExpectedResolution', 'Expected Resolution Date'
        }
    }

    /**
     * Example 9: SOAP Connector
     */
    def static exampleSOAPConnector() {
        return soapConnector('ValidateCustomerCredit') {
            description 'Validate customer credit status via SOAP service'
            
            wsdl 'https://creditcheck.financialservices.com/CreditService?wsdl'
            operation 'CheckCreditStatus'
            namespace 'http://creditcheck.financialservices.com/'
            
            authentication 'CreditService_Basic'
            
            requestMapping {
                map '.Customer.SSN', 'SSN'
                map '.Customer.FirstName', 'FirstName'
                map '.Customer.LastName', 'LastName'
                map '.Customer.DateOfBirth', 'DOB'
            }
            
            responseMapping {
                map 'CreditScore', '.Customer.CreditScore'
                map 'CreditRating', '.Customer.CreditRating'
                map 'LastUpdated', '.Customer.CreditLastChecked'
                map 'IsApproved', '.Customer.CreditApproved'
            }
        }
    }

    /**
     * Example 10: Test Case
     */
    def static exampleTestCase() {
        return testCase('TestCustomerRequestProcessing') {
            description 'Test customer request processing flow'
            ruleToTest 'ProcessCustomerRequest'
            
            // Input data
            input 'CustomerID', 'CUST001'
            input 'RequestType', 'Technical Support'
            input 'Priority', 'High'
            input 'Description', 'System login issues'
            
            // Expected results
            expect '.Status', 'Open'
            expect '.Category', 'Technical'
            expect '.SLA', 24
            expect '.ProcessedDate', '@today'
            
            // Assertions
            assertTrue '.RequestID'
            assertNotNull '.ProcessedDate'
            assertEquals '.AssignedTo', '@requestor.pyUserIdentifier'
            assertEquals '.Priority', 'High'
        }
    }

    /**
     * Example 11: Authentication Profile
     */
    def static exampleAuthProfile() {
        return authenticationProfile('CRM_OAuth2') {
            oauth2(
                'crm_client_id',
                'crm_client_secret',
                'https://auth.customercrm.com/oauth/token'
            )
        }
    }

    /**
     * Example 12: Database Configuration
     */
    def static exampleDatabase() {
        return database('CustomerDB') {
            url 'jdbc:postgresql://localhost:5432/customerdb'
            driver 'org.postgresql.Driver'
            credentials 'pegauser', 'pegapass'
            
            setDatabaseProperty 'maxConnections', '20'
            setDatabaseProperty 'connectionTimeout', '30000'
            setDatabaseProperty 'idleTimeout', '600000'
        }
    }

    /**
     * Example 13: Access Control
     */
    def static exampleAccessGroup() {
        return accessGroup('CustomerServiceAgent') {
            description 'Access group for customer service representatives'
            
            role 'CustomerServiceRole'
            role 'BasicUserRole'
            
            portal 'CustomerServicePortal'
            
            workPool 'CustomerService'
            workPool 'GeneralInquiries'
        }
    }

    /**
     * Example 14: Complex Flow with Multiple Paths
     */
    def static exampleComplexFlow() {
        return flow('ComplexCustomerFlow') {
            work()
            
            start 'InitiateRequest'
            
            assignment('Data Collection') {
                section 'CustomerDataForm'
                harness 'StandardHarness'
                flowAction 'Submit'
                flowAction 'Save'
                worklist()
            }
            
            decision('Validate Customer') {
                when 'IsValidCustomer'
            }
            
            connector('Credit Check', 'ValidateCustomerCredit') {
                setShapeProperty('timeout', '30')
            }
            
            decision('Route by Priority') {
                decisionTable 'DT_PriorityRouting'
            }
            
            assignment('High Priority Processing') {
                section 'HighPrioritySection'
                workbasket 'HighPriorityTeam'
                flowAction 'Approve'
                flowAction 'Escalate'
            }
            
            assignment('Standard Processing') {
                section 'StandardSection'
                workbasket 'StandardTeam'
                flowAction 'Process'
                flowAction 'Hold'
            }
            
            utility('Generate Report', 'GenerateProcessingReport')
            
            subProcess('Quality Review', 'QualityReviewFlow')
            
            utility('Send Notifications', 'SendAllNotifications')
            
            end('Complete') {
                resolved()
            }
            
            end('Rejected') {
                cancelled()
            }
            
            // Connections
            connect 'InitiateRequest', 'Data Collection'
            connect 'Data Collection', 'Validate Customer'
            connect 'Validate Customer', 'Credit Check', '.IsValid'
            connect 'Validate Customer', 'Rejected', '.IsValid == false'
            connect 'Credit Check', 'Route by Priority'
            connect 'Route by Priority', 'High Priority Processing', '.Priority == "High"'
            connect 'Route by Priority', 'Standard Processing', '.Priority != "High"'
            connect 'High Priority Processing', 'Generate Report', '.Approve'
            connect 'Standard Processing', 'Generate Report', '.Process'
            connect 'Generate Report', 'Quality Review'
            connect 'Quality Review', 'Send Notifications'
            connect 'Send Notifications', 'Complete'
        }
    }

    /**
     * Example 15: REST Service Definition
     */
    def static exampleRESTService() {
        return restService('CustomerRequestService') {
            setDescription 'REST service for customer request operations'
            servicePackage 'CustomerService'
            
            path '/api/v1/customers/{customerID}/requests'
            post()
            
            activity 'ProcessCustomerRequestAPI'
            
            requestMapping {
                map 'customerID', '.CustomerID'
                map 'requestType', '.RequestType'
                map 'description', '.Description'
                map 'priority', '.Priority'
            }
            
            responseMapping {
                map '.RequestID', 'requestId'
                map '.Status', 'status'
                map '.EstimatedResolution', 'estimatedResolution'
                map '.ConfirmationNumber', 'confirmationNumber'
            }
        }
    }

    /**
     * Example 16: Advanced Property with Validation
     */
    def static exampleAdvancedProperty() {
        return property('CustomerTier') {
            description 'Customer tier classification'
            
            single()
            text(20)
            required()
            
            validValues(['Bronze', 'Silver', 'Gold', 'Platinum'])
            defaultValue 'Bronze'
            validation 'ValidateCustomerTier'
        }
    }

    /**
     * Example 17: Decision Tree
     */
    def static exampleDecisionTree() {
        return decisionTree('CustomerEscalationTree') {
            description 'Determine escalation path for customer issues'
            
                        root {
            
                            condition '.RequestType', '=='
            
                            
            
                            branch('Technical Support') {
            
                                condition '.Priority', '=='
            
                                
            
                                branch('High', 'EscalateToSeniorTech')
            
                                branch('Medium', 'AssignToTechTeam')
            
                                branch('Low', 'AssignToJuniorTech')
            
                            }
            
                            
            
                            branch('Billing Inquiry') {
            
                                condition '.Customer.Tier', '=='
            
                                
            
                                branch('Gold', 'EscalateToBillingManager')
            
                                branch('Silver', 'AssignToBillingTeam')
            
                                branch('Bronze', 'AssignToJuniorBilling')
            
                            }
            
                            
            
                            branch('Complaint') {
            
                                setResult('EscalateToManager')
            
                            }
            
                        }
        }
    }

    /**
     * Utility method to run all examples
     */
    def static runAllExamples() {
        println "=== Pega DSL Examples ==="
        
        def examples = [
            'Application': exampleApplication(),
            'Data Page': exampleDataPage(),
            'Data Transform': exampleDataTransform(),
            'Decision Table': exampleDecisionTable(),
            'When Condition': exampleWhenCondition(),
            'REST Connector': exampleRESTConnector(),
            'Complex Section': exampleComplexSection(),
            'Correspondence': exampleCorrespondence(),
            'SOAP Connector': exampleSOAPConnector(),
            'Test Case': exampleTestCase(),
            'Auth Profile': exampleAuthProfile(),
            'Database': exampleDatabase(),
            'Access Group': exampleAccessGroup(),
            'Complex Flow': exampleComplexFlow(),
            'REST Service': exampleRESTService(),
            'Advanced Property': exampleAdvancedProperty(),
            'Decision Tree': exampleDecisionTree()
        ]
        
        examples.each { name, example ->
            println "\n--- $name Example ---"
            println "Name: ${example.name}"
            println "Type: ${example.type ?: example.class.simpleName}"
            if (example.description) {
                println "Description: ${example.description}"
            }
            
            // Print specific details based on type
            switch(example.class.simpleName) {
                case 'Application':
                    println "Version: ${example.version}"
                    println "Rulesets: ${example.rulesets.size()}"
                    break
                case 'Activity':
                    println "Steps: ${example.steps.size()}"
                    println "Local Variables: ${example.localVariables.size()}"
                    break
                case 'Section':
                    println "Elements: ${example.elements.size()}"
                    println "Layout: ${example.layoutType}"
                    break
                case 'Flow':
                    println "Flow Type: ${example.flowType}"
                    println "Shapes: ${example.shapes.size()}"
                    println "Connectors: ${example.connectors.size()}"
                    break
                case 'DataPage':
                    println "Source: ${example.sourceType} - ${example.dataSource}"
                    println "Scope: ${example.scope}"
                    break
                case 'DataTransform':
                    println "Actions: ${example.actions.size()}"
                    break
                case 'DecisionTable':
                    println "Conditions: ${example.conditions.size()}"
                    println "Results: ${example.results.size()}"
                    println "Rows: ${example.rows.size()}"
                    break
            }
        }
        
        println "\n=== Examples completed successfully ==="
        return examples
    }
}

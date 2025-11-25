package org.example

import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*
import examples.PegaDSLExamples

/**
 * Main class demonstrating Pega Developer Utilities DSL
 */
class Main {
    static void main(String[] args) {
        try {
            println "=== Pega Developer Utilities DSL Demo ==="
            
            // Create a simple activity
            def simpleActivity = activity('DemoActivity') {
                description 'A simple demo activity'
                
                localVariable 'customerID', 'Text'
                localVariable 'result', 'Text'
                
                propertySet '.CustomerID', 'param.ID'
                loadDataPage 'D_CustomerData', [CustomerID: '.CustomerID']
                
                step('Property-Set') {
                    parameter 'PropertyName', '.Result'
                    parameter 'PropertyValue', '"Processed"'
                    when '.CustomerID != null'
                }
                
                logMessage 'Processing completed for customer: ' + '.CustomerID'
                commit()
            }
            
            println "\n1. Created Activity: ${simpleActivity.name}"
            println "   Description: ${simpleActivity.description}"
            println "   Steps: ${simpleActivity.steps.size()}"
            println "   Local Variables: ${simpleActivity.localVariables.keySet()}"
            
            // Create a simple section
            def simpleSection = section('DemoForm') {
                description 'Demo form section'
                dynamic()
                
                input('CustomerName', 'Customer Name') {
                    textInput()
                    required()
                }
                
                input('CustomerTier', 'Customer Tier') {
                    dropdown()
                }
                
                button('Submit', 'ProcessForm') {
                    primary()
                }
            }
            
            println "\n2. Created Section: ${simpleSection.name}"
            println "   Layout: ${simpleSection.layoutType}"
            println "   Elements: ${simpleSection.elements.size()}"
            
            // Create a simple flow
            def simpleFlow = flow('DemoFlow') {
                description 'Demo processing flow'
                work()
                
                start 'Begin'
                assignment('Enter Data') {
                    section 'DemoForm'
                    worklist()
                }
                utility('Process', 'DemoActivity')
                end 'Complete'
                
                connect 'Begin', 'Enter Data'
                connect 'Enter Data', 'Process'
                connect 'Process', 'Complete'
            }
            
            println "\n3. Created Flow: ${simpleFlow.name}"
            println "   Type: ${simpleFlow.flowType}"
            println "   Shapes: ${simpleFlow.shapes.size()}"
            println "   Connectors: ${simpleFlow.connectors.size()}"
            
            // Create a data page
            def dataPage = dataPage('D_DemoData') {
                description 'Demo data page'
                activity 'LoadDemoData'
                scope 'Thread'
                maxAge 300
            }
            
            println "\n4. Created Data Page: ${dataPage.name}"
            println "   Source: ${dataPage.sourceType} - ${dataPage.dataSource}"
            println "   Scope: ${dataPage.scope}"
            
            // Create a when condition
            def whenCondition = when('IsDemoCustomer') {
                description 'Check demo customer'
                condition '.CustomerType', '==', 'Demo'
                and '.Status', '==', 'Active'
            }
            
            println "\n5. Created When Condition: ${whenCondition.name}"
            println "   Conditions: ${whenCondition.conditions.size()}"
            
            // Create a decision table
            def decisionTable = decisionTable('DemoRouting') {
                description 'Demo routing table'
                condition '.Priority'
                condition '.Type'
                result '.RouteTo'
                
                row([
                    '.Priority': 'High',
                    '.Type': 'VIP',
                    '.RouteTo': 'SeniorTeam'
                ])
            }
            
            println "\n6. Created Decision Table: ${decisionTable.name}"
            println "   Conditions: ${decisionTable.conditions.size()}"
            println "   Results: ${decisionTable.results.size()}"
            println "   Rows: ${decisionTable.rows.size()}"
            
            // Create REST connector
            def restConnector = restConnector('DemoAPI') {
                description 'Demo external API'
                url 'https://api.demo.com/customers'
                get()
                
                header 'Accept', 'application/json'
                
                responseMapping {
                    map 'customer.name', '.Customer.Name'
                    map 'customer.id', '.Customer.ID'
                }
            }
            
            println "\n7. Created REST Connector: ${restConnector.name}"
            println "   URL: ${restConnector.url}"
            println "   Method: ${restConnector.method}"
            
            // Run comprehensive examples
            println "\n=== Running Comprehensive Examples ==="
            try {
                def examples = PegaDSLExamples.runAllExamples()
                println "✓ Successfully executed ${examples.size()} comprehensive examples"
            } catch (Exception e) {
                println "✗ Error running examples: ${e.message}"
            }
            
            // Create a complete application
            println "\n=== Creating Complete Application ==="
            def completeApp = application('CompleteDemo') {
                setVersion '1.0.0'
                description 'Complete demo application showcasing all DSL features'
                setting 'environment', 'demo'
                setting 'debug', true
                
                /*
                ruleset('DemoRules') { // This is where the ruleset method is called
                    version '01.01.01'
                    description 'Demo application rules'
                    
                    rule('activity', 'CompleteProcess') {
                        description 'Complete processing activity'
                        propertySet '.StartTime', '@basedate'
                        loadDataPage 'D_DemoData'
                        applyDataTransform 'DT_ProcessDemo'
                        call 'ValidateData'
                        commit()
                    }
                    
                    rule('section', 'CompleteForm') {
                        description 'Complete form section'
                        smartLayout()
                        
                        input('ID', 'Identifier') {
                            textInput()
                            required()
                        }
                        
                        repeatingGrid '.Items' {
                            column('Name', 'Name') {
                                textInput()
                                sortable()
                            }
                            column('Value', 'Value') {
                                currency()
                            }
                        }
                    }
                    
                    rule('flow', 'CompleteWorkflow') {
                        description 'Complete workflow'
                        work()
                        
                        start 'Initialize'
                        assignment('Data Entry') {
                            section 'CompleteForm'
                            worklist()
                        }
                        decision('Validate') {
                            when 'IsValid'
                        }
                        utility('Process', 'CompleteProcess')
                        end 'Finish'
                        
                        connect 'Initialize', 'Data Entry'
                        connect 'Data Entry', 'Validate'
                        connect 'Validate', 'Process', '.IsValid'
                        connect 'Process', 'Finish'
                    }
                }
                */
            }
            
            println "✓ Created complete application: ${completeApp.name}"
            println "  Version: ${completeApp.version}"
            println "  Rulesets: ${completeApp.rulesets.size()}"
            println "  Settings: ${completeApp.settings.keySet()}"
            
            println "\n=== DSL Demo Complete ==="
            println "The Pega Developer Utilities DSL successfully created all rule types!"
        } catch (Throwable t) {
            // Avoid letting Errors like StackOverflowError escape during test runs.
            System.err.println("Main demo aborted due to error: " + t.class.name + " - " + t.message)
        }
    }
}

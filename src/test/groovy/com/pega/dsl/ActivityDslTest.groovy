package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

/**
 * Dedicated comprehensive test suite for Activity DSL component
 * Tests all Activity class methods, steps, and functionality
 */
class ActivityDslTest extends Specification {
    def should_handle_invalid_step_parameters() {
        when:
        def activity = activity('InvalidStepTest') {
            step('Property-Set') {
                // No parameters
            }
        }
        then:
        activity.steps.size() == 1
        activity.steps[0].parameters.isEmpty()
    }

    def should_handle_duplicate_local_variable_names() {
        when:
        def activity = activity('DuplicateVarTest') {
            localVariable 'customerID', 'Text'
            localVariable 'customerID', 'Integer'
        }
        then:
        activity.localVariables.size() == 1
        activity.localVariables['customerID'] == 'Integer' // Last one wins
    }

    def should_create_step_with_only_condition() {
        when:
        def activity = activity('StepWithConditionOnly') {
            step('Custom-Method') {
                when '.SomeCondition == true'
            }
        }
        then:
        activity.steps.size() == 1
        activity.steps[0].method == 'Custom-Method'
        activity.steps[0].condition == '.SomeCondition == true'
        activity.steps[0].parameters.isEmpty()
    }

    def should_create_activity_with_only_description() {
        when:
        def activity = activity('DescOnlyActivity') {
            description 'Only description, no steps or variables'
        }
        then:
        activity.name == 'DescOnlyActivity'
        activity.description == 'Only description, no steps or variables'
        activity.steps.isEmpty()
        activity.localVariables.isEmpty()
    }

    def should_create_basic_activity_with_name_and_type() {
        when:
        def activity = activity('TestActivity') {
            description 'Basic test activity'
        }
        
        then:
        activity.name == 'TestActivity'
        activity.type == 'Activity'
        activity.description == 'Basic test activity'
        activity.steps.size() == 0
        activity.localVariables.size() == 0
    }

    def should_add_local_variables_with_different_types() {
        when:
        def activity = activity('VariableTest') {
            localVariable 'customerID', 'Text'
            localVariable 'orderCount', 'Integer'
            localVariable 'totalAmount', 'Decimal'
            localVariable 'isVIP', 'TrueFalse'
            localVariable 'processDate', 'DateTime'
        }
        
        then:
        activity.localVariables.size() == 5
        activity.localVariables['customerID'] == 'Text'
        activity.localVariables['orderCount'] == 'Integer'
        activity.localVariables['totalAmount'] == 'Decimal'
        activity.localVariables['isVIP'] == 'TrueFalse'
        activity.localVariables['processDate'] == 'DateTime'
    }

    def should_create_property_set_step_with_parameters() {
        when:
        def activity = activity('PropertySetTest') {
            propertySet '.CustomerName', 'John Doe'
            propertySet '.Status', 'Active'
            propertySet '.ProcessedDate', '@basedate'
        }
        
        then:
        activity.steps.size() == 3
        activity.steps[0].method == 'Property-Set'
        activity.steps[0].parameters['PropertyName'] == '.CustomerName'
        activity.steps[0].parameters['PropertyValue'] == 'John Doe'
        activity.steps[1].parameters['PropertyName'] == '.Status'
        activity.steps[1].parameters['PropertyValue'] == 'Active'
        activity.steps[2].parameters['PropertyValue'] == '@basedate'
    }

    def should_create_page_new_step_with_class_name() {
        when:
        def activity = activity('PageNewTest') {
            pageNew 'CustomerPage', 'Data-Customer'
            pageNew 'OrderPage', 'Data-Order'
        }
        
        then:
        activity.steps.size() == 2
        activity.steps[0].method == 'Page-New'
        activity.steps[0].parameters['PageName'] == 'CustomerPage'
        activity.steps[0].parameters['ClassName'] == 'Data-Customer'
        activity.steps[1].parameters['PageName'] == 'OrderPage'
        activity.steps[1].parameters['ClassName'] == 'Data-Order'
    }

    def should_create_obj_open_step_with_different_modes() {
        when:
        def activity = activity('ObjOpenTest') {
            objOpen 'CUSTOMER-123'
            objOpen 'ORDER-456', 'READ-ONLY'
            objOpen 'PRODUCT-789', 'UPDATE'
        }
        
        then:
        activity.steps.size() == 3
        activity.steps[0].method == 'Obj-Open'
        activity.steps[0].parameters['ObjectKey'] == 'CUSTOMER-123'
        activity.steps[0].parameters['Mode'] == 'UPDATE' // default mode
        activity.steps[1].parameters['ObjectKey'] == 'ORDER-456'
        activity.steps[1].parameters['Mode'] == 'READ-ONLY'
        activity.steps[2].parameters['Mode'] == 'UPDATE'
    }

    def should_create_obj_save_step() {
        when:
        def activity = activity('ObjSaveTest') {
            objSave()
        }
        
        then:
        activity.steps.size() == 1
        activity.steps[0].method == 'Obj-Save'
    }

    def should_create_call_step_with_activity_and_parameters() {
        when:
        def activity = activity('CallTest') {
            callActivity 'ProcessCustomer'
            callActivity 'ValidateOrder', [OrderID: '.OrderID', CustomerID: '.CustomerID']
            callActivity 'SendNotification', [Message: 'Order processed', Email: '.Customer.Email']
        }
        
        then:
        activity.steps.size() == 3
        activity.steps[0].method == 'Call'
        activity.steps[0].parameters['activity'] == 'ProcessCustomer'
        activity.steps[1].parameters['activity'] == 'ValidateOrder'
        activity.steps[1].parameters['OrderID'] == '.OrderID'
        activity.steps[1].parameters['CustomerID'] == '.CustomerID'
        activity.steps[2].parameters['Message'] == 'Order processed'
        activity.steps[2].parameters['Email'] == '.Customer.Email'
    }

    def should_create_connect_rest_step_with_parameters() {
        when:
        def activity = activity('ConnectRESTTest') {
            connectREST 'CustomerAPI'
            connectREST 'OrderAPI', [CustomerID: '.CustomerID', Format: 'JSON']
        }
        
        then:
        activity.steps.size() == 2
        activity.steps[0].method == 'Connect-REST'
        activity.steps[0].parameters['connector'] == 'CustomerAPI'
        activity.steps[1].parameters['connector'] == 'OrderAPI'
        activity.steps[1].parameters['CustomerID'] == '.CustomerID'
        activity.steps[1].parameters['Format'] == 'JSON'
    }

    def should_create_connect_soap_step_with_parameters() {
        when:
        def activity = activity('ConnectSOAPTest') {
            connectSOAP 'LegacySystem'
            connectSOAP 'ExternalAPI', [Operation: 'GetCustomer', Timeout: '30']
        }
        
        then:
        activity.steps.size() == 2
        activity.steps[0].method == 'Connect-SOAP'
        activity.steps[0].parameters['connector'] == 'LegacySystem'
        activity.steps[1].parameters['connector'] == 'ExternalAPI'
        activity.steps[1].parameters['Operation'] == 'GetCustomer'
        activity.steps[1].parameters['Timeout'] == '30'
    }

    def should_create_apply_datatransform_step_with_source_and_target() {
        when:
        def activity = activity('DataTransformTest') {
            applyDataTransform 'DT_CustomerData'
            applyDataTransform 'DT_OrderProcessing', '.SourcePage', '.TargetPage'
            applyDataTransform 'DT_Validation', '.InputData'
        }
        
        then:
        activity.steps.size() == 3
        activity.steps[0].method == 'Apply-DataTransform'
        activity.steps[0].parameters['DataTransform'] == 'DT_CustomerData'
        activity.steps[1].parameters['DataTransform'] == 'DT_OrderProcessing'
        activity.steps[1].parameters['Source'] == '.SourcePage'
        activity.steps[1].parameters['Target'] == '.TargetPage'
        activity.steps[2].parameters['Source'] == '.InputData'
    }

    def should_create_load_datapage_step_with_parameters() {
        when:
        def activity = activity('LoadDataPageTest') {
            loadDataPage 'D_CustomerList'
            loadDataPage 'D_ProductCatalog', [Category: 'Electronics', Status: 'Active']
            loadDataPage 'D_OrderHistory', [CustomerID: '.CustomerID', Days: '30']
        }
        
        then:
        activity.steps.size() == 3
        activity.steps[0].method == 'Load-DataPage'
        activity.steps[0].parameters['DataPageName'] == 'D_CustomerList'
        activity.steps[1].parameters['DataPageName'] == 'D_ProductCatalog'
        activity.steps[1].parameters['Category'] == 'Electronics'
        activity.steps[1].parameters['Status'] == 'Active'
        activity.steps[2].parameters['CustomerID'] == '.CustomerID'
        activity.steps[2].parameters['Days'] == '30'
    }

    def should_create_show_page_step_with_format() {
        when:
        def activity = activity('ShowPageTest') {
            showPage 'CustomerDetails'
            showPage 'OrderSummary', 'PDF'
            showPage 'ReportData', 'Excel'
        }
        
        then:
        activity.steps.size() == 3
        activity.steps[0].method == 'Show-Page'
        activity.steps[0].parameters['PageName'] == 'CustomerDetails'
        activity.steps[0].parameters['Format'] == 'HTML' // default format
        activity.steps[1].parameters['Format'] == 'PDF'
        activity.steps[2].parameters['Format'] == 'Excel'
    }

    def should_create_branch_step_with_condition() {
        when:
        def activity = activity('BranchTest') {
            branch 'ProcessVIPCustomer'
            branch 'HandleError', '.HasErrors == true'
            branch 'ContinueProcessing', '.Status == "Ready"'
        }
        
        then:
        activity.steps.size() == 3
        activity.steps[0].method == 'Branch'
        activity.steps[0].parameters['Activity'] == 'ProcessVIPCustomer'
        activity.steps[1].parameters['Activity'] == 'HandleError'
        activity.steps[1].parameters['Condition'] == '.HasErrors == true'
        activity.steps[2].parameters['Condition'] == '.Status == "Ready"'
    }

    def should_create_log_message_step_with_different_levels() {
        when:
        def activity = activity('LogMessageTest') {
            logMessage 'Processing started'
            logMessage 'Debug information', 'DEBUG'
            logMessage 'Warning occurred', 'WARN'
            logMessage 'Error in processing', 'ERROR'
        }
        
        then:
        activity.steps.size() == 4
        activity.steps[0].method == 'Log-Message'
        activity.steps[0].parameters['Message'] == 'Processing started'
        activity.steps[0].parameters['Level'] == 'INFO' // default level
        activity.steps[1].parameters['Level'] == 'DEBUG'
        activity.steps[2].parameters['Level'] == 'WARN'
        activity.steps[3].parameters['Level'] == 'ERROR'
    }

    def should_create_queue_step_with_parameters() {
        when:
        def activity = activity('QueueTest') {
            queue 'BackgroundProcess'
            queue 'DataSync', [Table: 'Customer', BatchSize: '100']
        }
        
        then:
        activity.steps.size() == 2
        activity.steps[0].method == 'Queue'
        activity.steps[0].parameters['Activity'] == 'BackgroundProcess'
        activity.steps[1].parameters['Activity'] == 'DataSync'
        activity.steps[1].parameters['Table'] == 'Customer'
        activity.steps[1].parameters['BatchSize'] == '100'
    }

    def should_create_commit_and_rollback_steps() {
        when:
        def activity = activity('TransactionTest') {
            propertySet '.Status', 'Processing'
            commit()
            rollback()
        }
        
        then:
        activity.steps.size() == 3
        activity.steps[1].method == 'Commit'
        activity.steps[2].method == 'Rollback'
    }

    def should_create_custom_step_with_method_and_closure() {
        when:
        def activity = activity('CustomStepTest') {
            step('Custom-Method') {
                parameter 'CustomParam1', 'Value1'
                parameter 'CustomParam2', 'Value2'
                when '.SomeCondition == true'
                transitionTo '5'
                iterate()
            }
        }
        
        then:
        activity.steps.size() == 1
        activity.steps[0].method == 'Custom-Method'
        activity.steps[0].parameters['CustomParam1'] == 'Value1'
        activity.steps[0].parameters['CustomParam2'] == 'Value2'
        activity.steps[0].condition == '.SomeCondition == true'
        activity.steps[0].transition == '5'
        activity.steps[0].isIterate == true
    }

    def should_handle_activity_step_conditions_and_transitions() {
        when:
        def activity = activity('StepConditionTest') {
            step('Property-Set') {
                parameter 'PropertyName', '.TestProp'
                parameter 'PropertyValue', 'TestValue'
                when '.AllowUpdate == true'
                transitionTo 'END'
            }
            
            step('Log-Message') {
                parameter 'Message', 'Step executed'
                iterate()
            }
        }
        
        then:
        activity.steps.size() == 2
        activity.steps[0].condition == '.AllowUpdate == true'
        activity.steps[0].transition == 'END'
        activity.steps[1].isIterate == true
    }

    def should_create_complex_activity_with_multiple_operation_types() {
        when:
        def activity = activity('ComplexActivity') {
            description 'Complex business process activity'
            
            localVariable 'customerID', 'Text'
            localVariable 'orderTotal', 'Decimal'
            localVariable 'isProcessed', 'TrueFalse'
            
            // Initialize data
            propertySet '.StartTime', '@basedate'
            propertySet '.ProcessingStatus', 'STARTED'
            
            // Load customer data
            loadDataPage 'D_CustomerData', [CustomerID: '.CustomerID']
            
            // Create new order page
            pageNew 'OrderPage', 'Data-Order'
            
            // Open existing object
            objOpen '.OrderKey', 'UPDATE'
            
            // Apply data transformation
            applyDataTransform 'DT_OrderProcessing', '.OrderPage', '.ProcessedOrder'
            
            // Call external service
            connectREST 'PaymentGateway', [Amount: '.OrderTotal', CustomerID: '.CustomerID']
            
            // Conditional branching
            branch 'HandlePaymentFailure', '.PaymentStatus != "SUCCESS"'
            
            // Log progress
            logMessage 'Order processing completed', 'INFO'
            
            // Save changes
            objSave()
            commit()
        }
        
        then:
        activity.name == 'ComplexActivity'
        activity.description == 'Complex business process activity'
        activity.localVariables.size() == 3
        activity.steps.size() == 11
        
        // Verify specific steps
        activity.steps[0].method == 'Property-Set'
        activity.steps[2].method == 'Load-DataPage'
        activity.steps[3].method == 'Page-New'
        activity.steps[4].method == 'Obj-Open'
        activity.steps[5].method == 'Apply-DataTransform'
        activity.steps[6].method == 'Connect-REST'
        activity.steps[7].method == 'Branch'
        activity.steps[8].method == 'Log-Message'
        activity.steps[9].method == 'Obj-Save'
        activity.steps[10].method == 'Commit'
    }

    def should_handle_activity_inheritance_properties() {
        when:
        def activity = activity('InheritanceTest') {
            description 'Test rule inheritance'
            setStatus 'Under-Review'
            setAvailable false
        }
        
        then:
        activity.type == 'Activity'
        activity.description == 'Test rule inheritance'
        activity.status == 'Under-Review'
        activity.isAvailable == false
    }

    def should_create_activity_with_mixed_step_types_and_complex_parameters() {
        when:
        def activity = activity('MixedStepsActivity') {
            // Standard activity methods
            propertySet '.InitFlag', 'true'
            callActivity 'ValidationActivity', [Mode: 'Strict', Data: '.InputData']
            
            // Custom steps with complex configurations
            step('Email-Send') {
                parameter 'To', '.Customer.Email'
                parameter 'Subject', 'Order Confirmation'
                parameter 'Template', 'OrderConfirmation'
                when '.Customer.EmailOptIn == true'
            }
            
            step('Queue-Process') {
                parameter 'QueueName', 'OrderProcessing'
                parameter 'Priority', 'High'
                parameter 'Data', '.OrderData'
                transitionTo 'Complete'
                iterate()
            }
            
            // More standard methods
            loadDataPage 'D_ShippingRates', [Zone: '.ShippingZone', Weight: '.OrderWeight']
            applyDataTransform 'DT_FinalProcessing'
            logMessage 'Activity completed successfully'
        }
        
        then:
        activity.steps.size() == 7
        
        // Verify custom step configurations
        def emailStep = activity.steps[2]
        emailStep.method == 'Email-Send'
        emailStep.parameters['To'] == '.Customer.Email'
        emailStep.condition == '.Customer.EmailOptIn == true'
        
        def queueStep = activity.steps[3]
        queueStep.method == 'Queue-Process'
        queueStep.transition == 'Complete'
        queueStep.isIterate == true
    }

    def should_handle_empty_activity_creation() {
        when:
        def activity = activity('EmptyActivity') {
            // Empty activity with just closure
        }
        
        then:
        activity.name == 'EmptyActivity'
        activity.type == 'Activity'
        activity.steps.isEmpty()
        activity.localVariables.isEmpty()
        activity.description == null
    }

    def should_support_fluent_method_chaining_patterns() {
        when:
        def activity = activity('FluentActivity') {
            description 'Demonstrates fluent patterns'
            
            // Chain multiple property sets
            propertySet '.Step1', 'Value1'
            propertySet '.Step2', 'Value2'
            propertySet '.Step3', 'Value3'
            
            // Chain multiple calls
            callActivity 'Activity1'
            callActivity 'Activity2'
            callActivity 'Activity3'
            
            // Mix different method types
            loadDataPage 'D_Data1'
            applyDataTransform 'DT_Transform1'
            connectREST 'API1'
            objSave()
            commit()
        }
        
        then:
        activity.steps.size() == 11
        activity.steps.collect { it.method } == [
            'Property-Set', 'Property-Set', 'Property-Set',
            'Call', 'Call', 'Call',
            'Load-DataPage', 'Apply-DataTransform', 'Connect-REST',
            'Obj-Save', 'Commit'
        ]
    }

    def should_validate_activity_step_parameter_handling() {
        when:
        def activity = activity('ParameterTest') {
            step('Test-Method') {
                parameter 'StringParam', 'StringValue'
                parameter 'IntParam', 42
                parameter 'BoolParam', true
                parameter 'ListParam', ['item1', 'item2', 'item3']
                parameter 'MapParam', [key1: 'value1', key2: 'value2']
            }
        }
        
        then:
        def step = activity.steps[0]
        step.parameters['StringParam'] == 'StringValue'
        step.parameters['IntParam'] == 42
        step.parameters['BoolParam'] == true
        step.parameters['ListParam'] == ['item1', 'item2', 'item3']
        step.parameters['MapParam'] == [key1: 'value1', key2: 'value2']
    }
}

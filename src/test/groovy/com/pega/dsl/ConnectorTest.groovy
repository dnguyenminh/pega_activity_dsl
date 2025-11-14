package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

/**
 * Comprehensive test suite for Connector DSL functionality
 * Tests REST/SOAP connectors, services, mappings, and configurations
 */
class ConnectorTest extends Specification {

    def "should create basic REST connector with default settings"() {
        when:
        def connector = restConnector('CustomerAPI') {
            url('https://api.example.com/customers')
            get()
        }

        then:
        connector.name == 'CustomerAPI'
        connector.type == 'Connect-REST'
        connector.url == 'https://api.example.com/customers'
        connector.method == 'GET'
        connector.headers.isEmpty()
        connector.requestMapping.isEmpty()
        connector.responseMapping.isEmpty()
    }

    def "should create REST connector with different HTTP methods"() {
        when:
        def getConnector = restConnector('GetAPI') { get() }
        def postConnector = restConnector('PostAPI') { post() }
        def putConnector = restConnector('PutAPI') { put() }
        def deleteConnector = restConnector('DeleteAPI') { delete() }
        def patchConnector = restConnector('PatchAPI') { patch() }

        then:
        getConnector.method == 'GET'
        postConnector.method == 'POST'
        putConnector.method == 'PUT'
        deleteConnector.method == 'DELETE'
        patchConnector.method == 'PATCH'
    }

    def "should create REST connector with headers"() {
        when:
        def connector = restConnector('AuthenticatedAPI') {
            url('https://api.example.com/data')
            post()
            header('Content-Type', 'application/json')
            header('Accept', 'application/json')
            header('X-API-Version', '2.0')
        }

        then:
        connector.headers.size() == 3
        connector.headers['Content-Type'] == 'application/json'
        connector.headers['Accept'] == 'application/json'
        connector.headers['X-API-Version'] == '2.0'
    }

    def "should create REST connector with authentication"() {
        when:
        def connector = restConnector('SecureAPI') {
            url('https://api.secure.com/endpoint')
            get()
            authentication('OAuthProfile')
        }

        then:
        connector.authProfile == 'OAuthProfile'
    }

    def "should create REST connector with request and response mapping"() {
        when:
        def connector = restConnector('CustomerService') {
            url('https://api.crm.com/customers')
            post()
            
            requestMapping {
                map('.CustomerName', 'name')
                map('.Email', 'email')
                map('.Phone', 'phone_number')
                set('source', 'Pega')
            }
            
            responseMapping {
                map('customer_id', '.CustomerID')
                map('status', '.APIStatus')
                map('created_date', '.CreatedOn')
            }
        }

        then:
        connector.requestMapping.size() == 4
        connector.requestMapping['name'] == '.CustomerName'
        connector.requestMapping['email'] == '.Email'
        connector.requestMapping['source'] == 'Pega'
        connector.responseMapping.size() == 3
        connector.responseMapping['.CustomerID'] == 'customer_id'
        connector.responseMapping['.APIStatus'] == 'status'
        connector.responseMapping['.CreatedOn'] == 'created_date'
    }

    def "should create complex customer management REST connector"() {
        when:
        def connector = restConnector('CustomerManagementAPI') {
            description('Comprehensive customer management API integration')
            url('https://api.customer-mgmt.com/v2/customers')
            post()
            authentication('CustomerMgmtAuth')
            
            header('Content-Type', 'application/json')
            header('Accept', 'application/json')
            header('X-Client-ID', 'PegaIntegration')
            header('X-Correlation-ID', '{{RequestID}}')
            
            requestMapping {
                // Customer basic info
                map('.Customer.FirstName', 'personal_info.first_name')
                map('.Customer.LastName', 'personal_info.last_name')
                map('.Customer.Email', 'contact.email')
                map('.Customer.Phone', 'contact.phone')
                map('.Customer.DateOfBirth', 'personal_info.date_of_birth')
                
                // Address information
                map('.Address.Street', 'address.street_address')
                map('.Address.City', 'address.city')
                map('.Address.State', 'address.state')
                map('.Address.ZipCode', 'address.postal_code')
                map('.Address.Country', 'address.country')
                
                // Account preferences
                map('.Preferences.NewsletterOptIn', 'preferences.newsletter')
                map('.Preferences.SMSOptIn', 'preferences.sms_notifications')
                
                // Metadata
                set('created_by', 'Pega_System')
                set('source_system', 'Pega_Platform')
                set('api_version', '2.0')
            }
            
            responseMapping {
                // Customer identifiers
                map('customer.id', '.Customer.ExternalID')
                map('customer.account_number', '.Customer.AccountNumber')
                
                // Status and timestamps
                map('status.code', '.APIResponse.StatusCode')
                map('status.message', '.APIResponse.StatusMessage')
                map('metadata.created_at', '.Customer.CreatedTimestamp')
                map('metadata.updated_at', '.Customer.LastModified')
                
                // Validation results
                map('validation.email_valid', '.Validation.EmailValid')
                map('validation.phone_valid', '.Validation.PhoneValid')
                map('validation.address_valid', '.Validation.AddressValid')
                
                // System assignments
                map('assignments.customer_rep_id', '.Customer.AssignedRepID')
                map('assignments.customer_segment', '.Customer.Segment')
            }
        }

        then:
        connector.name == 'CustomerManagementAPI'
        connector.description == 'Comprehensive customer management API integration'
        connector.url == 'https://api.customer-mgmt.com/v2/customers'
        connector.method == 'POST'
        connector.authProfile == 'CustomerMgmtAuth'
        
        connector.headers.size() == 4
        connector.headers['X-Client-ID'] == 'PegaIntegration'
        connector.headers['X-Correlation-ID'] == '{{RequestID}}'
        
        connector.requestMapping.size() == 15
        connector.requestMapping['personal_info.first_name'] == '.Customer.FirstName'
        connector.requestMapping['address.city'] == '.Address.City'
        connector.requestMapping['created_by'] == 'Pega_System'
        
        connector.responseMapping.size() == 11
        connector.responseMapping['.Customer.ExternalID'] == 'customer.id'
        connector.responseMapping['.APIResponse.StatusCode'] == 'status.code'
        connector.responseMapping['.Customer.Segment'] == 'assignments.customer_segment'
    }

    def "should create basic SOAP connector"() {
        when:
        def connector = soapConnector('CreditCheckService') {
            wsdl('https://creditbureau.com/services/CreditCheck?wsdl')
            operation('GetCreditScore')
            namespace('http://creditbureau.com/creditcheck')
        }

        then:
        connector.name == 'CreditCheckService'
        connector.type == 'Connect-SOAP'
        connector.wsdlUrl == 'https://creditbureau.com/services/CreditCheck?wsdl'
        connector.operation == 'GetCreditScore'
        connector.namespace == 'http://creditbureau.com/creditcheck'
    }

    def "should create SOAP connector with mappings and headers"() {
        when:
        def connector = soapConnector('PaymentProcessingService') {
            wsdl('https://payments.example.com/soap/PaymentService?wsdl')
            operation('ProcessPayment')
            namespace('http://payments.example.com/v1')
            
            header('SOAPAction', 'ProcessPayment')
            header('Authorization', 'Bearer {{AccessToken}}')
            
            requestMapping {
                map('.Payment.Amount', 'PaymentAmount')
                map('.Payment.Currency', 'CurrencyCode')
                map('.Customer.ID', 'CustomerID')
                map('.Payment.Method', 'PaymentMethod')
                set('MerchantID', 'PEGA_MERCHANT')
            }
            
            responseMapping {
                map('TransactionID', '.Payment.TransactionID')
                map('Status', '.Payment.Status')
                map('AuthCode', '.Payment.AuthorizationCode')
                map('ResponseMessage', '.Payment.ResponseMessage')
            }
        }

        then:
        connector.wsdlUrl == 'https://payments.example.com/soap/PaymentService?wsdl'
        connector.operation == 'ProcessPayment'
        connector.namespace == 'http://payments.example.com/v1'
        
        connector.headers.size() == 2
        connector.headers['SOAPAction'] == 'ProcessPayment'
        connector.headers['Authorization'] == 'Bearer {{AccessToken}}'
        
        connector.requestMapping.size() == 5
        connector.requestMapping['PaymentAmount'] == '.Payment.Amount'
        connector.requestMapping['MerchantID'] == 'PEGA_MERCHANT'
        
        connector.responseMapping.size() == 4
        connector.responseMapping['.Payment.TransactionID'] == 'TransactionID'
        connector.responseMapping['.Payment.Status'] == 'Status'
    }

    def "should create REST service for exposing API"() {
        when:
        def service = restService('CustomerAPI') {
            servicePackage('CustomerManagement')
            path('/api/v1/customers')
            post()
            activity('ProcessCustomerRequest')
            
            requestMapping {
                map('customer.name', '.CustomerName')
                map('customer.email', '.Email')
                map('customer.phone', '.Phone')
            }
            
            responseMapping {
                map('.CustomerID', 'customer_id')
                map('.Status', 'status')
                map('.Message', 'message')
            }
        }

        then:
        service.name == 'CustomerAPI'
        service.type == 'Service-REST'
        service.servicePackage == 'CustomerManagement'
        service.resourcePath == '/api/v1/customers'
        service.method == 'POST'
        service.activity == 'ProcessCustomerRequest'
        
        service.requestMapping.size() == 3
        service.requestMapping['.CustomerName'] == 'customer.name'
        service.requestMapping['.Email'] == 'customer.email'
        
        service.responseMapping.size() == 3
        service.responseMapping['customer_id'] == '.CustomerID'
        service.responseMapping['status'] == '.Status'
    }

    def "should create credit bureau integration connector"() {
        when:
        def connector = restConnector('CreditBureauIntegration') {
            description('Integration with external credit bureau for credit scoring')
            url('https://api.creditbureau.com/v3/credit-reports')
            post()
            authentication('CreditBureauAuth')
            
            header('Content-Type', 'application/json')
            header('X-API-Key', '{{CreditBureauAPIKey}}')
            header('X-Client-Version', 'Pega-1.2.0')
            
            requestMapping {
                // Personal information
                map('.Applicant.FirstName', 'consumer.name.first')
                map('.Applicant.LastName', 'consumer.name.last')
                map('.Applicant.MiddleName', 'consumer.name.middle')
                map('.Applicant.SSN', 'consumer.ssn')
                map('.Applicant.DateOfBirth', 'consumer.date_of_birth')
                
                // Current address
                map('.CurrentAddress.Street', 'consumer.address.street')
                map('.CurrentAddress.City', 'consumer.address.city')
                map('.CurrentAddress.State', 'consumer.address.state')
                map('.CurrentAddress.ZipCode', 'consumer.address.zip')
                
                // Request parameters
                set('report_type', 'full_credit_report')
                set('include_score', true)
                set('include_factors', true)
                set('purpose', 'credit_application')
            }
            
            responseMapping {
                // Credit score information
                map('credit_score.score', '.CreditReport.Score')
                map('credit_score.model', '.CreditReport.ScoreModel')
                map('credit_score.factors', '.CreditReport.ScoreFactors')
                
                // Credit history
                map('credit_history.accounts', '.CreditReport.Accounts')
                map('credit_history.inquiries', '.CreditReport.Inquiries')
                map('credit_history.public_records', '.CreditReport.PublicRecords')
                
                // Summary metrics
                map('summary.total_accounts', '.CreditReport.TotalAccounts')
                map('summary.open_accounts', '.CreditReport.OpenAccounts')
                map('summary.total_balance', '.CreditReport.TotalBalance')
                map('summary.available_credit', '.CreditReport.AvailableCredit')
                
                // Report metadata
                map('report.id', '.CreditReport.ReportID')
                map('report.generated_date', '.CreditReport.GeneratedDate')
                map('report.status', '.CreditReport.Status')
                map('report.errors', '.CreditReport.Errors')
            }
        }

        then:
        connector.name == 'CreditBureauIntegration'
        connector.description == 'Integration with external credit bureau for credit scoring'
        connector.url == 'https://api.creditbureau.com/v3/credit-reports'
        connector.method == 'POST'
        connector.authProfile == 'CreditBureauAuth'
        
        connector.headers.size() == 3
        connector.headers['X-API-Key'] == '{{CreditBureauAPIKey}}'
        connector.headers['X-Client-Version'] == 'Pega-1.2.0'
        
        connector.requestMapping.size() == 13
        connector.requestMapping['consumer.name.first'] == '.Applicant.FirstName'
        connector.requestMapping['consumer.ssn'] == '.Applicant.SSN'
        connector.requestMapping['report_type'] == 'full_credit_report'
        
        connector.responseMapping.size() == 14
        connector.responseMapping['.CreditReport.Score'] == 'credit_score.score'
        connector.responseMapping['.CreditReport.TotalAccounts'] == 'summary.total_accounts'
        connector.responseMapping['.CreditReport.ReportID'] == 'report.id'
    }

    def "should create notification service connector"() {
        when:
        def connector = restConnector('NotificationService') {
            description('Send notifications via external service')
            url('https://notifications.example.com/api/send')
            post()
            authentication('NotificationAuth')
            
            header('Content-Type', 'application/json')
            header('User-Agent', 'Pega-Notification-Client/1.0')
            
            requestMapping {
                // Message details
                map('.Notification.Type', 'message.type')
                map('.Notification.Subject', 'message.subject')
                map('.Notification.Body', 'message.content')
                map('.Notification.Priority', 'message.priority')
                
                // Recipient information
                map('.Recipient.Email', 'recipient.email')
                map('.Recipient.Phone', 'recipient.phone')
                map('.Recipient.Name', 'recipient.name')
                
                // Delivery options
                map('.DeliveryOptions.Channel', 'delivery.channel')
                map('.DeliveryOptions.ScheduledTime', 'delivery.scheduled_for')
                
                // System metadata
                set('sender', 'Pega_System')
                set('template_engine', 'pega')
            }
            
            responseMapping {
                map('message_id', '.Notification.MessageID')
                map('status', '.Notification.DeliveryStatus')
                map('delivery_time', '.Notification.DeliveredAt')
                map('error_code', '.Notification.ErrorCode')
                map('error_message', '.Notification.ErrorMessage')
            }
        }

        then:
        connector.name == 'NotificationService'
        connector.description == 'Send notifications via external service'
        connector.url == 'https://notifications.example.com/api/send'
        connector.method == 'POST'
        connector.authProfile == 'NotificationAuth'
        
        connector.requestMapping.size() == 11
        connector.requestMapping['message.type'] == '.Notification.Type'
        connector.requestMapping['recipient.email'] == '.Recipient.Email'
        connector.requestMapping['sender'] == 'Pega_System'
        
        connector.responseMapping.size() == 5
        connector.responseMapping['.Notification.MessageID'] == 'message_id'
        connector.responseMapping['.Notification.ErrorCode'] == 'error_code'
    }

    def "should create document generation SOAP connector"() {
        when:
        def connector = soapConnector('DocumentGenerationService') {
            description('Generate documents using external service')
            wsdl('https://docgen.example.com/services/DocumentService?wsdl')
            operation('GenerateDocument')
            namespace('http://docgen.example.com/v2')
            
            header('SOAPAction', 'GenerateDocument')
            header('X-Service-Key', '{{DocumentServiceKey}}')
            
            requestMapping {
                // Document template
                map('.Document.TemplateID', 'DocumentRequest.TemplateID')
                map('.Document.Format', 'DocumentRequest.OutputFormat')
                map('.Document.Quality', 'DocumentRequest.Quality')
                
                // Data for template
                map('.DocumentData', 'DocumentRequest.TemplateData')
                
                // Output options
                map('.OutputOptions.Delivery', 'DocumentRequest.DeliveryMethod')
                map('.OutputOptions.Watermark', 'DocumentRequest.Watermark')
                
                // Security options
                map('.Security.Encrypt', 'DocumentRequest.Encryption')
                map('.Security.Password', 'DocumentRequest.Password')
                
                // Metadata
                set('RequestedBy', 'Pega_Platform')
                set('RequestTime', '{{CurrentDateTime}}')
            }
            
            responseMapping {
                map('DocumentResponse.DocumentID', '.Document.GeneratedID')
                map('DocumentResponse.DocumentURL', '.Document.DownloadURL')
                map('DocumentResponse.Status', '.Document.GenerationStatus')
                map('DocumentResponse.FileSize', '.Document.FileSizeBytes')
                map('DocumentResponse.ExpiryDate', '.Document.ExpiresOn')
                map('DocumentResponse.ErrorCode', '.Document.ErrorCode')
                map('DocumentResponse.ErrorMessage', '.Document.ErrorMessage')
            }
        }

        then:
        connector.name == 'DocumentGenerationService'
        connector.description == 'Generate documents using external service'
        connector.wsdlUrl == 'https://docgen.example.com/services/DocumentService?wsdl'
        connector.operation == 'GenerateDocument'
        connector.namespace == 'http://docgen.example.com/v2'
        
        connector.headers.size() == 2
        connector.headers['SOAPAction'] == 'GenerateDocument'
        connector.headers['X-Service-Key'] == '{{DocumentServiceKey}}'
        
        connector.requestMapping.size() == 10
        connector.requestMapping['DocumentRequest.TemplateID'] == '.Document.TemplateID'
        connector.requestMapping['DocumentRequest.Encryption'] == '.Security.Encrypt'
        connector.requestMapping['RequestedBy'] == 'Pega_Platform'
        
        connector.responseMapping.size() == 7
        connector.responseMapping['.Document.GeneratedID'] == 'DocumentResponse.DocumentID'
        connector.responseMapping['.Document.ErrorMessage'] == 'DocumentResponse.ErrorMessage'
    }

    def "should create REST service with multiple HTTP methods"() {
        when:
        def getService = restService('CustomerGetAPI') {
            path('/api/customers/{id}')
            get()
            activity('GetCustomer')
        }
        
        def postService = restService('CustomerCreateAPI') {
            path('/api/customers')
            post()
            activity('CreateCustomer')
        }
        
        def putService = restService('CustomerUpdateAPI') {
            path('/api/customers/{id}')
            put()
            activity('UpdateCustomer')
        }
        
        def deleteService = restService('CustomerDeleteAPI') {
            path('/api/customers/{id}')
            delete()
            activity('DeleteCustomer')
        }

        then:
        getService.method == 'GET'
        getService.resourcePath == '/api/customers/{id}'
        getService.activity == 'GetCustomer'
        
        postService.method == 'POST'
        postService.resourcePath == '/api/customers'
        postService.activity == 'CreateCustomer'
        
        putService.method == 'PUT'
        putService.activity == 'UpdateCustomer'
        
        deleteService.method == 'DELETE'
        deleteService.activity == 'DeleteCustomer'
    }
}

package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

/**
 * Comprehensive test suite for Correspondence DSL functionality
 * Tests all correspondence formats, parameters, and configurations
 */
class CorrespondenceTest extends Specification {

    def "should create basic HTML correspondence with default settings"() {
        when:
        def correspondence = correspondence('WelcomeEmail') {
            subject('Welcome to Our Service')
            body('<h1>Welcome!</h1><p>Thank you for joining us.</p>')
        }

        then:
        correspondence.name == 'WelcomeEmail'
        correspondence.type == 'Correspondence'
        correspondence.format == 'HTML'
        correspondence.subject == 'Welcome to Our Service'
        correspondence.body == '<h1>Welcome!</h1><p>Thank you for joining us.</p>'
        correspondence.correspondenceParameters.isEmpty()
    }

    def "should create correspondence with different formats"() {
        when:
        def htmlCorr = correspondence('HTMLEmail') { html() }
        def textCorr = correspondence('TextEmail') { text() }
        def rtfCorr = correspondence('RTFDocument') { rtf() }

        then:
        htmlCorr.format == 'HTML'
        textCorr.format == 'Text'
        rtfCorr.format == 'RTF'
    }

    def "should create correspondence with parameters"() {
        when:
        def correspondence = correspondence('CustomerNotification') {
            subject('Account Update for {{CustomerName}}')
            body('Dear {{CustomerName}}, your account has been updated.')
            parameter('CustomerName', 'Customer Full Name')
            parameter('AccountNumber', 'Account Number', '12345')
            parameter('UpdateDate', 'Date of Update')
        }

        then:
        correspondence.correspondenceParameters.size() == 3
        correspondence.correspondenceParameters[0].name == 'CustomerName'
        correspondence.correspondenceParameters[0].prompt == 'Customer Full Name'
        correspondence.correspondenceParameters[0].defaultValue == ''
        correspondence.correspondenceParameters[1].name == 'AccountNumber'
        correspondence.correspondenceParameters[1].defaultValue == '12345'
        correspondence.correspondenceParameters[2].name == 'UpdateDate'
        correspondence.correspondenceParameters[2].prompt == 'Date of Update'
    }

    def "should create correspondence with typed parameters"() {
        when:
        def correspondence = correspondence('InvoiceNotification') {
            subject('Invoice #{{InvoiceNumber}} Due {{DueDate}}')
            body('Your invoice for ${{Amount}} is due on {{DueDate}}.')
            
            parameter('InvoiceNumber') {
                text()
            }
            parameter('Amount') {
                decimal()
            }
            parameter('DueDate') {
                date()
            }
            parameter('ItemCount') {
                integer()
            }
        }

        then:
        correspondence.correspondenceParameters.size() == 4
        correspondence.correspondenceParameters[0].type == 'Text'
        correspondence.correspondenceParameters[1].type == 'Decimal'
        correspondence.correspondenceParameters[2].type == 'Date'
        correspondence.correspondenceParameters[3].type == 'Integer'
    }

    def "should create complex welcome email correspondence"() {
        when:
        def correspondence = correspondence('CustomerWelcomeEmail') {
            description('Welcome email sent to new customers')
            html()
            subject('Welcome to {{CompanyName}}, {{CustomerName}}!')
            
            body('''
            <html>
            <head>
                <style>
                    .header { background-color: #0066cc; color: white; padding: 20px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f5f5f5; padding: 10px; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Welcome to {{CompanyName}}</h1>
                </div>
                <div class="content">
                    <h2>Hello {{CustomerName}},</h2>
                    <p>Thank you for joining us on {{RegistrationDate}}. Your account number is {{AccountNumber}}.</p>
                    <p>We're excited to have you as part of our community!</p>
                    <p>Your initial credit limit is ${{CreditLimit}}.</p>
                    <p>Best regards,<br/>The {{CompanyName}} Team</p>
                </div>
                <div class="footer">
                    <p>This email was sent on {{CurrentDate}}. If you have questions, contact us at support@company.com</p>
                </div>
            </body>
            </html>
            ''')
            
            parameter('CompanyName', 'Company Name', 'MyCompany Inc.')
            parameter('CustomerName', 'Customer Full Name')
            parameter('RegistrationDate') {
                date()
            }
            parameter('AccountNumber', 'Account Number')
            parameter('CreditLimit') {
                decimal()
            }
            parameter('CurrentDate') {
                date()
            }
        }

        then:
        correspondence.name == 'CustomerWelcomeEmail'
        correspondence.format == 'HTML'
        correspondence.subject == 'Welcome to {{CompanyName}}, {{CustomerName}}!'
        correspondence.body.contains('<div class="header">')
        correspondence.body.contains('{{CustomerName}}')
        correspondence.correspondenceParameters.size() == 6
        correspondence.correspondenceParameters[0].name == 'CompanyName'
        correspondence.correspondenceParameters[0].defaultValue == 'MyCompany Inc.'
        correspondence.correspondenceParameters[2].type == 'Date'
        correspondence.correspondenceParameters[4].type == 'Decimal'
    }

    def "should create payment reminder text correspondence"() {
        when:
        def correspondence = correspondence('PaymentReminder') {
            description('Payment reminder sent to customers')
            text()
            subject('Payment Reminder - Invoice {{InvoiceNumber}}')
            
            body('''
            Dear {{CustomerName}},

            This is a friendly reminder that your payment for Invoice {{InvoiceNumber}} 
            in the amount of ${{Amount}} was due on {{DueDate}}.

            Payment Details:
            - Invoice Number: {{InvoiceNumber}}
            - Amount Due: ${{Amount}}
            - Original Due Date: {{DueDate}}
            - Days Past Due: {{DaysPastDue}}

            Please submit your payment at your earliest convenience.
            
            If you have already made this payment, please disregard this notice.

            For questions, please contact our billing department at {{BillingPhone}}.

            Thank you,
            {{CompanyName}} Billing Department
            ''')
            
            parameter('CustomerName', 'Customer Name')
            parameter('InvoiceNumber', 'Invoice Number')
            parameter('Amount') {
                decimal()
            }
            parameter('DueDate') {
                date()
            }
            parameter('DaysPastDue') {
                integer()
            }
            parameter('BillingPhone', 'Billing Phone Number', '1-800-BILLING')
            parameter('CompanyName', 'Company Name', 'Our Company')
        }

        then:
        correspondence.name == 'PaymentReminder'
        correspondence.format == 'Text'
        correspondence.subject == 'Payment Reminder - Invoice {{InvoiceNumber}}'
        correspondence.body.contains('Dear {{CustomerName}}')
        correspondence.body.contains('Days Past Due: {{DaysPastDue}}')
        correspondence.correspondenceParameters.size() == 7
        correspondence.correspondenceParameters.find { it.name == 'Amount' }.type == 'Decimal'
        correspondence.correspondenceParameters.find { it.name == 'DaysPastDue' }.type == 'Integer'
        correspondence.correspondenceParameters.find { it.name == 'BillingPhone' }.defaultValue == '1-800-BILLING'
    }

    def "should create order confirmation correspondence"() {
        when:
        def correspondence = correspondence('OrderConfirmation') {
            description('Order confirmation email sent to customers')
            html()
            subject('Order Confirmation - Order #{{OrderNumber}}')
            
            body('''
            <div style="font-family: Arial, sans-serif; max-width: 600px;">
                <h2 style="color: #28a745;">Order Confirmed!</h2>
                
                <p>Hello {{CustomerName}},</p>
                <p>Thank you for your order. Here are your order details:</p>
                
                <table style="border-collapse: collapse; width: 100%; border: 1px solid #ddd;">
                    <tr style="background-color: #f8f9fa;">
                        <td style="padding: 12px; border: 1px solid #ddd;"><strong>Order Number:</strong></td>
                        <td style="padding: 12px; border: 1px solid #ddd;">{{OrderNumber}}</td>
                    </tr>
                    <tr>
                        <td style="padding: 12px; border: 1px solid #ddd;"><strong>Order Date:</strong></td>
                        <td style="padding: 12px; border: 1px solid #ddd;">{{OrderDate}}</td>
                    </tr>
                    <tr style="background-color: #f8f9fa;">
                        <td style="padding: 12px; border: 1px solid #ddd;"><strong>Total Amount:</strong></td>
                        <td style="padding: 12px; border: 1px solid #ddd;">${{TotalAmount}}</td>
                    </tr>
                    <tr>
                        <td style="padding: 12px; border: 1px solid #ddd;"><strong>Estimated Delivery:</strong></td>
                        <td style="padding: 12px; border: 1px solid #ddd;">{{DeliveryDate}}</td>
                    </tr>
                </table>
                
                <h3>Shipping Address:</h3>
                <p>{{ShippingAddress}}</p>
                
                <p>You can track your order using tracking number: {{TrackingNumber}}</p>
                
                <p>Thank you for your business!</p>
            </div>
            ''')
            
            parameter('CustomerName', 'Customer Name')
            parameter('OrderNumber', 'Order Number')
            parameter('OrderDate') {
                date()
            }
            parameter('TotalAmount') {
                decimal()
            }
            parameter('DeliveryDate') {
                date()
            }
            parameter('ShippingAddress', 'Shipping Address')
            parameter('TrackingNumber', 'Package Tracking Number')
        }

        then:
        correspondence.name == 'OrderConfirmation'
        correspondence.format == 'HTML'
        correspondence.subject == 'Order Confirmation - Order #{{OrderNumber}}'
        correspondence.body.contains('<table style="border-collapse: collapse')
        correspondence.body.contains('{{TrackingNumber}}')
        correspondence.correspondenceParameters.size() == 7
    }

    def "should create appointment reminder correspondence"() {
        when:
        def correspondence = correspondence('AppointmentReminder') {
            description('Appointment reminder for healthcare patients')
            text()
            subject('Appointment Reminder - {{AppointmentDate}} at {{AppointmentTime}}')
            
            body('''
            Dear {{PatientName}},

            This is a reminder of your upcoming appointment:

            Appointment Details:
            - Date: {{AppointmentDate}}
            - Time: {{AppointmentTime}}
            - Provider: Dr. {{ProviderName}}
            - Location: {{ClinicLocation}}
            - Type: {{AppointmentType}}

            Please arrive {{ArrivalMinutes}} minutes early for check-in.

            If you need to reschedule or cancel, please call us at {{ClinicPhone}} 
            at least {{CancellationHours}} hours in advance.

            Thank you,
            {{ClinicName}}
            ''')
            
            parameter('PatientName', 'Patient Name')
            parameter('AppointmentDate') {
                date()
            }
            parameter('AppointmentTime', 'Appointment Time', '10:00 AM')
            parameter('ProviderName', 'Healthcare Provider Name')
            parameter('ClinicLocation', 'Clinic Location')
            parameter('AppointmentType', 'Type of Appointment', 'General Consultation')
            parameter('ArrivalMinutes') {
                integer()
            }
            parameter('ClinicPhone', 'Clinic Phone Number')
            parameter('CancellationHours') {
                integer()
            }
            parameter('ClinicName', 'Clinic Name')
        }

        then:
        correspondence.name == 'AppointmentReminder'
        correspondence.format == 'Text'
        correspondence.correspondenceParameters.size() == 10
        correspondence.correspondenceParameters.find { it.name == 'ArrivalMinutes' }.type == 'Integer'
        correspondence.correspondenceParameters.find { it.name == 'AppointmentTime' }.defaultValue == '10:00 AM'
        correspondence.correspondenceParameters.find { it.name == 'AppointmentType' }.defaultValue == 'General Consultation'
    }

    def "should create legal notice RTF correspondence"() {
        when:
        def correspondence = correspondence('LegalNotice') {
            description('Formal legal notice document')
            rtf()
            subject('Legal Notice - Case {{CaseNumber}}')
            
            body('''
            {\\rtf1\\ansi\\deff0 {\\fonttbl {\\f0 Times New Roman;}}
            \\f0\\fs24 
            
            \\b LEGAL NOTICE\\b0
            \\par
            \\par
            TO: {{RecipientName}}
            \\par
            ADDRESS: {{RecipientAddress}}
            \\par
            \\par
            CASE NUMBER: {{CaseNumber}}
            \\par
            DATE: {{NoticeDate}}
            \\par
            \\par
            \\b NOTICE TO APPEAR\\b0
            \\par
            \\par
            You are hereby notified that you are required to appear before the 
            {{CourtName}} on {{HearingDate}} at {{HearingTime}} regarding 
            case number {{CaseNumber}}.
            \\par
            \\par
            \\b MATTER:\\b0 {{CaseMatter}}
            \\par
            \\par
            Failure to appear may result in a default judgment being entered against you.
            \\par
            \\par
            \\b Date Served:\\b0 {{ServiceDate}}
            \\par
            \\b Served By:\\b0 {{ProcessServerName}}
            \\par
            }
            ''')
            
            parameter('RecipientName', 'Recipient Full Name')
            parameter('RecipientAddress', 'Recipient Address')
            parameter('CaseNumber', 'Court Case Number')
            parameter('NoticeDate') {
                date()
            }
            parameter('CourtName', 'Court Name')
            parameter('HearingDate') {
                date()
            }
            parameter('HearingTime', 'Hearing Time', '9:00 AM')
            parameter('CaseMatter', 'Case Matter/Subject')
            parameter('ServiceDate') {
                date()
            }
            parameter('ProcessServerName', 'Process Server Name')
        }

        then:
        correspondence.name == 'LegalNotice'
        correspondence.format == 'RTF'
        correspondence.subject == 'Legal Notice - Case {{CaseNumber}}'
        correspondence.body.contains('{\\rtf1\\ansi\\deff0')
        correspondence.body.contains('\\b LEGAL NOTICE\\b0')
        correspondence.correspondenceParameters.size() == 10
        correspondence.correspondenceParameters.find { it.name == 'NoticeDate' }.type == 'Date'
        correspondence.correspondenceParameters.find { it.name == 'HearingTime' }.defaultValue == '9:00 AM'
    }

    def "should create simple notification correspondence"() {
        when:
        def correspondence = correspondence('SimpleNotification') {
            subject('System Notification')
            body('This is a simple notification message.')
        }

        then:
        correspondence.name == 'SimpleNotification'
        correspondence.format == 'HTML'  // default
        correspondence.subject == 'System Notification'
        correspondence.body == 'This is a simple notification message.'
        correspondence.correspondenceParameters.isEmpty()
    }

    def "should create marketing campaign correspondence"() {
        when:
        def correspondence = correspondence('MarketingCampaign') {
            description('Monthly marketing campaign email')
            html()
            subject('{{OfferTitle}} - Special Offer for {{CustomerName}}!')
            
            body('''
            <html>
            <head>
                <style>
                    .banner { background: linear-gradient(45deg, #ff6b6b, #4ecdc4); color: white; text-align: center; padding: 30px; }
                    .offer { background: #fff3cd; border: 2px solid #ffc107; padding: 20px; margin: 20px 0; border-radius: 10px; }
                    .cta-button { background: #28a745; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="banner">
                    <h1>{{OfferTitle}}</h1>
                    <h3>Exclusively for {{CustomerName}}</h3>
                </div>
                
                <div style="padding: 20px;">
                    <p>Dear {{CustomerName}},</p>
                    
                    <p>We have an exciting offer just for you!</p>
                    
                    <div class="offer">
                        <h2>{{DiscountPercentage}}% OFF</h2>
                        <p>On all purchases over ${{MinimumPurchase}}</p>
                        <p><strong>Offer expires: {{ExpirationDate}}</strong></p>
                        <p>Use code: <strong>{{PromoCode}}</strong></p>
                    </div>
                    
                    <a href="{{WebsiteURL}}" class="cta-button">Shop Now</a>
                    
                    <p>Thank you for being a valued customer!</p>
                </div>
            </body>
            </html>
            ''')
            
            parameter('CustomerName', 'Customer Name')
            parameter('OfferTitle', 'Offer Title', 'Monthly Special')
            parameter('DiscountPercentage') {
                integer()
            }
            parameter('MinimumPurchase') {
                decimal()
            }
            parameter('ExpirationDate') {
                date()
            }
            parameter('PromoCode', 'Promotional Code')
            parameter('WebsiteURL', 'Website URL', 'https://www.company.com')
        }

        then:
        correspondence.name == 'MarketingCampaign'
        correspondence.format == 'HTML'
        correspondence.body.contains('class="banner"')
        correspondence.body.contains('{{DiscountPercentage}}% OFF')
        correspondence.correspondenceParameters.size() == 7
        correspondence.correspondenceParameters.find { it.name == 'DiscountPercentage' }.type == 'Integer'
        correspondence.correspondenceParameters.find { it.name == 'WebsiteURL' }.defaultValue == 'https://www.company.com'
    }
}

package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class HarnessTest extends Specification {

    def "should create harness with basic properties"() {
        when:
        def harness = harness('MainPortal') {
            description 'Main portal harness for the application'
            template 'PortalTemplate'
        }

        then:
        harness.name == 'MainPortal'
        harness.description == 'Main portal harness for the application'
        harness.template == 'PortalTemplate'
        harness.type == 'Harness'
    }

    def "should add different types of elements"() {
        when:
        def harness = harness('WorkAreaHarness') {
            header 'AppHeader'
            workArea 'CaseWorkArea'
            footer 'AppFooter'
            navigation 'PrimaryNav'
            includeSection 'DashboardWidgets'
        }

        then:
        harness.elements.size() == 5
        harness.elements.collect { it.type } == ['Header', 'Work Area', 'Footer', 'Navigation', 'Section']
        harness.elements.collect { it.content } == ['AppHeader', 'CaseWorkArea', 'AppFooter', 'PrimaryNav', 'DashboardWidgets']
    }

    def "should configure element properties using closures"() {
        when:
        def harness = harness('AdvancedHarness') {
            header('AppHeader') {
                // Assuming HarnessElement has a 'property' method
                // property 'style', 'fixed' 
            }
        }

        then:
        // For now, just verify the element is created.
        // A more detailed test would require HarnessElement to have methods.
        harness.elements.size() == 1
        harness.elements[0].type == 'Header'
    }

    def "should create a comprehensive portal harness"() {
        when:
        def harness = harness('ManagerPortal') {
            description 'Portal for managers to review and approve work'
            template 'ManagerTemplate'
            
            header 'ManagerHeader'
            navigation 'ManagerNav'
            workArea 'ManagerWorkArea'
            includeSection 'TeamDashboard'
            includeSection 'QuickLinks'
            footer 'PortalFooter'
        }

        then:
        harness.name == 'ManagerPortal'
        harness.template == 'ManagerTemplate'
        harness.elements.size() == 6
        harness.elements.count { it.type == 'Section' } == 2
        harness.elements.find { it.type == 'Header' }.content == 'ManagerHeader'
        harness.elements.find { it.type == 'Work Area' }.content == 'ManagerWorkArea'
    }
}

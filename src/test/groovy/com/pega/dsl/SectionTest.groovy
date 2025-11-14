package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class SectionTest extends Specification {

    def "should create section with different layout types"() {
        when:
        def dynamicSection = section('DynamicLayout') { dynamic() }
        def freeformSection = section('FreeformLayout') { freeform() }
        def smartLayoutSection = section('SmartLayout') { smartLayout() }

        then:
        dynamicSection.layoutType == 'Dynamic'
        freeformSection.layoutType == 'Freeform'
        smartLayoutSection.layoutType == 'Smart Layout'
    }

    def "should create section with all input control types"() {
        when:
        def section = section('InputControls') {
            input('.p1') { textInput() }
            input('.p2') { textArea() }
            input('.p3') { dropdown() }
            input('.p4') { checkbox() }
            input('.p5') { radioButtons() }
            input('.p6') { calendar() }
            input('.p7') { currency() }
            input('.p8') { richTextEditor() }
            input('.p9') { attachContent() }
            input('.p10') { autoComplete() }
            input('.p11') { smartPrompt() }
        }

        then:
        section.elements.size() == 11
        section.elements.collect { it.control }.toSet() == [
            'Text Input', 'Text Area', 'Dropdown', 'Checkbox', 'Radio Buttons', 
            'Calendar', 'Currency', 'Rich Text Editor', 'Attach Content', 
            'AutoComplete', 'Smart Prompt'
        ] as Set
    }

    def "should configure various input properties"() {
        when:
        def section = section('InputProperties') {
            input('.prop1', 'Prop 1') {
                required()
                readOnly()
            }
            input('.prop2') {
                disabled()
            }
            input('.prop3') {
                visible('.ShowAdvanced == true')
            }
        }

        then:
        def input1 = section.elements.find { it.property == '.prop1' } as InputElement
        input1.label == 'Prop 1'
        input1.properties['required'] == true
        input1.properties['readOnly'] == true

        def input2 = section.elements.find { it.property == '.prop2' } as InputElement
        input2.properties['disabled'] == true

        def input3 = section.elements.find { it.property == '.prop3' } as InputElement
        input3.visibility == 'If'
        input3.condition == '.ShowAdvanced == true'
    }

    def "should create section with all button styles and actions"() {
        when:
        def section = section('ButtonSection') {
            button('Submit', 'SubmitAction') { primary() }
            button('Cancel') { secondary() }
            button('Save Draft') { tertiary() }
            button('Help') { simple() }
            button('Delete') { strong() }
        }

        then:
        section.elements.size() == 5
        def buttons = section.elements.findAll { it instanceof ButtonElement }
        buttons.size() == 5
        buttons[0].style == 'Primary'
        buttons[0].action == 'SubmitAction'
        buttons[1].style == 'Secondary'
        buttons[2].style == 'Tertiary'
        buttons[3].style == 'Simple'
        buttons[4].style == 'Strong'
    }

    def "should create section with a detailed repeating grid"() {
        when:
        def section = section('GridSection') {
            repeatingGrid('.OrderItems') {
                column('ID', 'Order ID') { link(); sortable(); width(100) }
                column('Product', 'Product Name') { textInput(); filterable() }
                column('Amount', 'Amount') { currency(); readOnly() }
                column('Status', 'Status') { dropdown() }
            }
        }

        then:
        def grid = section.elements[0] as RepeatingGridElement
        grid.pageList == '.OrderItems'
        grid.columns.size() == 4

        def col1 = grid.columns[0]
        col1.property == 'ID'
        col1.control == 'Link'
        col1.sortable == true
        col1.width == 100

        def col2 = grid.columns[1]
        col2.filterable == true

        def col3 = grid.columns[2]
        col3.readOnly == true
    }

    def "should create section with included sections and labels"() {
        when:
        def section = section('CompositeSection') {
            label('Customer Details')
            includeSection('CustomerDetailsSection') {
                when('.CustomerType == "Individual"')
            }
            label('Order Information')
            includeSection('OrderDetailsSection')
        }

        then:
        section.elements.size() == 4
        (section.elements[0] as LabelElement).text == 'Customer Details'
        
        def includedSection1 = section.elements[1] as IncludeSectionElement
        includedSection1.sectionName == 'CustomerDetailsSection'
        includedSection1.condition == '.CustomerType == "Individual"'
    }

    def "should handle empty section gracefully"() {
        when:
        def section = section('EmptySection') {
            description 'This section is intentionally empty'
        }

        then:
        section.name == 'EmptySection'
        section.elements.isEmpty()
    }

    def "should handle inheritance properties from Rule"() {
        when:
        def section = section('InheritedSection') {
            className 'MyApp-Work-UI'
            setStatus 'Approved'
        }

        then:
        section.className == 'MyApp-Work-UI'
        section.status == 'Approved'
    }

    def "should throw error for invalid element placement"() {
        when: "A column is defined outside a repeatingGrid"
        section('InvalidGrid') {
            column('ID', 'ID')
        }

        then:
        thrown(MissingMethodException)
    }
}

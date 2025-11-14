package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class DecisionTreeTest extends Specification {

    def "should create basic decision tree"() {
        when:
        def tree = decisionTree('BasicTree') {
            description 'Simple routing tree'
            root {
                condition '.Customer.Tier', '=='
                branch 'Gold', 'RouteToGoldTeam'
                branch 'Silver', 'RouteToSilverTeam'
                branch 'Bronze', 'RouteToBronzeTeam'
            }
        }

        then:
        tree.name == 'BasicTree'
        tree.rootNode.property == '.Customer.Tier'
        tree.rootNode.operator == '=='
        tree.rootNode.children.size() == 3
        tree.rootNode.children[0].value == 'Gold'
        tree.rootNode.children[0].result == 'RouteToGoldTeam'
        tree.rootNode.children[1].value == 'Silver'
        tree.rootNode.children[2].value == 'Bronze'
    }

    def "should create nested decision tree"() {
        when:
        def tree = decisionTree('NestedTree') {
            root {
                condition '.Request.Type', '=='
                branch('Technical') {
                    condition '.Request.Priority', '=='
                    branch('High', 'EscalateToTier2')
                    branch('Medium', 'AssignToTier1')
                }
                branch('Billing') {
                    condition '.Request.Amount', '>='
                    branch('1000') {
                        result 'HandleAsDispute'
                    }
                    branch('0') {
                        result 'StandardBilling'
                    }
                }
            }
        }

        then:
        tree.rootNode.property == '.Request.Type'
        tree.rootNode.children.size() == 2

        def techBranch = tree.rootNode.children[0]
        techBranch.value == 'Technical'
        techBranch.property == '.Request.Priority'
        techBranch.children.size() == 2
        techBranch.children[0].value == 'High'
        techBranch.children[0].result == 'EscalateToTier2'

        def billingBranch = tree.rootNode.children[1]
        billingBranch.value == 'Billing'
        billingBranch.property == '.Request.Amount'
        billingBranch.children.size() == 2
        billingBranch.children[0].value == '1000'
        billingBranch.children[0].result == 'HandleAsDispute'
    }

    def "should handle multiple levels of nesting"() {
        when:
        def tree = decisionTree('DeeplyNestedTree') {
            root {
                condition 'level1', '=='
                branch('A') {
                    condition 'level2', '=='
                    branch('B') {
                        condition 'level3', '=='
                        branch('C', 'ResultC')
                        branch('D', 'ResultD')
                    }
                    branch('E', 'ResultE')
                }
                branch('F', 'ResultF')
            }
        }

        then:
        def level1 = tree.rootNode
        level1.property == 'level1'
        level1.children.size() == 2

        def branchA = level1.children[0]
        branchA.value == 'A'
        branchA.property == 'level2'
        branchA.children.size() == 2

        def branchB = branchA.children[0]
        branchB.value == 'B'
        branchB.property == 'level3'
        branchB.children.size() == 2
        branchB.children[0].value == 'C'
        branchB.children[0].result == 'ResultC'

        branchA.children[1].value == 'E'
        branchA.children[1].result == 'ResultE'

        level1.children[1].value == 'F'
        level1.children[1].result == 'ResultF'
    }

    def "should handle inheritance properties"() {
        when:
        def tree = decisionTree('InheritedTree') {
            description 'Tree with inheritance'
            className 'MyApp-Work-Decision'
            setStatus 'Approved'
            setTreeProperty 'Purpose', 'Routing'

            root {
                condition '.Urgency', '>'
                branch '10', 'HighPriority'
            }
        }

        then:
        tree.name == 'InheritedTree'
        tree.description == 'Tree with inheritance'
        tree.className == 'MyApp-Work-Decision'
        tree.status == 'Approved'
        tree.properties['Purpose'] == 'Routing'
    }

    def "should throw error for invalid structure"() {
        when: "A branch is defined outside the root"
        decisionTree('InvalidTree') {
            branch 'Orphan', 'ShouldFail'
        }

        then:
        thrown(MissingMethodException)

        when: "A condition is defined outside the root or a branch"
        decisionTree('InvalidTree2') {
            condition '.Prop', '=='
        }

        then:
        thrown(MissingMethodException)
    }
}

package vn.com.fecredit.pega.activity

import static ActivityBuilder.activity

class Main {
    static void main(String[] args) {
        // Register When rules
        WhenRegistry.register('IsFoo', { ctx -> ctx['foo'] == 'bar' })
        WhenRegistry.register('CountIsTwo', { ctx -> (ctx['count'] ?: 0) == 2 })

        // existing demo activity
        def sub = activity {
            name 'SubActivity'
            step {
                id 1
                method 'Property-Set'
                params subProp: { ctx -> 'subValue-' + (ctx['count'] ?: 0) }


            }
            step {
                id 2
                method 'Write-Message'
                param 'message', { ctx -> "Inside sub, subProp=${ctx['subProp']}" }
            }
        }

        def act = activity {
            name 'MyActivity'
            step {
                id 1
                method 'Property-Set'
                params foo: 'bar', count: { ctx -> (ctx['count'] ?: 0) + 1 }, 'page.one': [name: 'source', value: 123]
            }

            step {
                id 2
                method 'Write-Message'
                param 'message', { ctx -> "Hello from activity, foo=${ctx['foo']}, count=${ctx['count']}, page.one.name=${PropertyUtils.get(ctx, 'page.one.name')}" }
            }

            // Use named When rule from registry by name
            step {
                id 3
                method 'When'
                param 'condition', 'IsFoo' // uses WhenRegistry.get('IsFoo')
            }

            step {
                id 4
                method 'Page-Copy'
                params source: 'page.one', target: 'page.two'
            }

            step {
                id 5
                method 'Obj-Open'
                params target: 'LoadedPage', id: 'ID-123', class: 'MyClass', data: [field: 'value']
            }

            step {
                id 6
                method 'Write-Message'
                param 'message', { ctx -> "After Obj-Open, LoadedPage.id=${PropertyUtils.get(ctx, 'LoadedPage.id')}" }
            }

            step {
                id 7
                method 'Property-Remove'
                params 'foo': true
            }

            step {
                id 8
                method 'Call'
                param 'activity', sub
            }

            step {
                id 9
                method 'Write-Message'
                param 'message', { ctx -> "Final context keys: ${ctx.keySet().toList()}" }
            }
        }

        // New demo: loop + jump
        def loopJump = activity {
            name 'LoopJumpActivity'
            step {
                id 1
                method 'Property-Set'
                params count: 0
            }

            step {
                id 2
                label 'INC'
                method 'Property-Set'
                params count: { ctx -> (ctx['count'] ?: 0) + 1 }
                loop true
                loopCondition { ctx -> (ctx['count'] ?: 0) < 3 }
            }

            step {
                id 3
                method 'Write-Message'
                param 'message', { ctx -> "After loop, count=${PropertyUtils.get(ctx, 'count')}" }
            }

            step {
                id 4
                method 'Property-Set'
                params skip: { ctx -> (ctx['count'] ?: 0) == 3 }
                // jump immediately after this step if skip true
                jumpTo 'END'
                jumpCondition { ctx -> ctx['skip'] }
            }

            step {
                id 5
                method 'Write-Message'
                param 'message', { ctx -> "This message may be skipped" }
            }

            step {
                id 6
                label 'END'
                method 'Write-Message'
                param 'message', { ctx -> "Reached END" }
            }
        }

        println '--- Running MyActivity ---'
        ActivityRunner.run(act, [:])

        println '\n--- Running LoopJumpActivity ---'
        ActivityRunner.run(loopJump, [:])

        // Demonstrate using a When rule name directly in a step precondition or whenCondition
        def demoWhenActivity = activity {
            name 'WhenRuleDemo'
            step {
                id 1
                method 'Property-Set'
                params foo: 'bar', count: 2
            }
            step {
                id 2
                method 'When'
                param 'condition', 'When:CountIsTwo' // explicitly prefixed
            }
            step {
                id 3
                method 'Write-Message'
                param 'message', { ctx -> "When rule evaluated true, count=${ctx['count']}" }
            }
        }

        println '\n--- Running WhenRuleDemo ---'
        ActivityRunner.run(demoWhenActivity, [:])

        // New demo: expression evaluator demo (supports >, <, &&, ||, etc.)
        def expressionDemo = activity {
            name 'ExpressionDemo'
            step {
                id 1
                method 'Property-Set'
                params foo: 'bar', count: 5
            }
            step {
                id 2
                method 'When'
                // use a Groovy expression string; ActivityRunner.evaluateCondition will evaluate it
                param 'condition', "count > 1 && foo == 'bar'"
            }
            step {
                id 3
                method 'Write-Message'
                param 'message', { ctx -> "Expression evaluated true: count=${ctx['count']}, foo=${ctx['foo']}" }
            }
        }

        println '\n--- Running ExpressionDemo ---'
        ActivityRunner.run(expressionDemo, [:])
    }
}

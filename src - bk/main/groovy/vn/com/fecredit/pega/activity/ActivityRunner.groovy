package vn.com.fecredit.pega.activity

import com.pega.pegarules.pub.clipboard.ClipboardPage
import com.pega.pegarules.pub.clipboard.ClipboardProperty
import groovy.transform.CompileStatic
import vn.com.fecredit.pega.activity.method.*
import vn.com.fecredit.pega.activity.model.Activity
import vn.com.fecredit.pega.activity.model.Step

/**
 * ActivityRunner executes steps defined in an Activity.
 */
@CompileStatic
class ActivityRunner {

    static final int MAX_TOTAL_ITERATIONS = 10000
    static final int MAX_STEP_ITERATIONS = 1000

    private static final String PX_RETURN_PAGE = 'pxReturnPage'
    private static final String TARGET = 'target'
    private static final String ID = 'id'
    private static final String CLASS = 'class'
    private static final String DATA = 'data'
    private static final String WHEN_PREFIX = 'When:'
    private static final String EQUALS_OP = '=='
    private static final String NOT_EQUALS_OP = '!='
    private static final String GET_AT = 'getAt'
    private static final int ZERO = 0
    private static final int MINUS_ONE = -1
    private static final String DOUBLE_QUOTE = '"'
    private static final String SINGLE_QUOTE = "'"
    // private static final String CLOSING_PAREN = ')'

    static void run(Activity act, Map<String, Object> ctx = [:], boolean stopOnError = false) {
        SimpleLogger logger = new SimpleLogger()
        logger.info("Running activity: ${act?.name}")

        // Ensure the execution context contains the runtime environment, a ParameterPage for activity parameters,
        // and a clipboard-like map the activity can operate on. Tests or nested activities may provide these,
        // so only create defaults when missing.
        if (!ctx.containsKey('environment')) {
            ctx['environment'] = [:]
        }
        if (!ctx.containsKey('parameterPage')) {
            ctx['parameterPage'] = new com.pega.pegarules.pub.runtime.ParameterPage()
        }
        if (!ctx.containsKey('clipboard')) {
            ctx['clipboard'] = [:]
        }
        if (!ctx.containsKey('loopContextStack')) {
            ctx['loopContextStack'] = new java.util.ArrayDeque<Map<String, Object>>()
        }

        Map<String, Integer> labelIndex = [:]
        Map<Object, Integer> idIndex = [:]
        act.steps.eachWithIndex { Step s, int idx ->
            if (s?.label) {
                labelIndex[s.label] = idx
            }
            if (s?.id != null) {
                idIndex[s.id] = idx
            }
        }

        Map<String, Object> executionCtx = [
                act         : act,
                ctx         : ctx,
                stopOnError : stopOnError,
                logger      : logger,
                labelIndex  : labelIndex,
                idIndex     : idIndex
        ]

        try {
            executeActivitySteps(executionCtx)
        } catch (GroovyRuntimeException e) {
            logger.error("Activity ${act?.name} failed: ${e?.message}", e)
            throw e
        } finally {
            logger.info("Activity finished. Context: ${ctx}")
        }
    }

    private static final Map<String, StepHandler> HANDLERS = [:]

    static {
        HANDLERS['Property-Set'] = new PropertySetHandler()
        HANDLERS['Write-Message'] = new WriteMessageHandler()
        HANDLERS['Call'] = new CallHandler()
        HANDLERS['Page-New'] = new PageNewHandler()
        HANDLERS['Page-Copy'] = new PageCopyHandler()
        HANDLERS['Page-Remove'] = new PageRemoveHandler()
        HANDLERS['Page-Delete'] = HANDLERS['Page-Remove']
        HANDLERS['Page-List'] = new PageListHandler()
        HANDLERS['Page-Sort'] = new PageSortHandler()
        HANDLERS['Property-Remove'] = new PropertyRemoveHandler()
        HANDLERS['Obj-Save'] = new ObjSaveHandler()
        HANDLERS['Obj-Delete'] = new ObjDeleteHandler()
        HANDLERS['Obj-List'] = new ObjListHandler()
        HANDLERS['Obj-Query'] = new ObjQueryHandler()
        HANDLERS['Obj-Open'] = new ObjOpenHandler()
    }

    static void executeStepOperation(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        if (s == null) {
            return
        }
        String method = s.method ?: ''
        StepHandler h = HANDLERS[method]
        if (h != null) {
            h.handle(s, ctx, logger, stopOnError)
        } else {
            logger.warn("Unsupported method: ${s?.method}")
        }
    }

    static boolean evaluateCondition(Object cond, Map<String, Object> ctx) {
        if (cond == null) {
            return false
        }
        try {
            if (isCallable(cond)) {
                return ExpressionEvaluator.truthy(((Closure<?>) cond).call(ctx))
            }
            if (cond == true || cond == false) {
                return (Boolean) cond
            }
            if (cond != null && cond.getClass() == String) {
                String name = cond as String
                if (name.startsWith(WHEN_PREFIX)) {
                    name = name.substring(WHEN_PREFIX.length())
                }
                Closure<?> whenClosure = WhenRegistry.get(name)
                if (whenClosure != null) {
                    try {
                        return ExpressionEvaluator.truthy(whenClosure.call(ctx))
                    } catch (GroovyRuntimeException e) {
                        return false
                    }
                }

                try {
                    return ExpressionEvaluator.evaluate(name, ctx)
                } catch (GroovyRuntimeException e) {
                    try {
                        if (name.contains(EQUALS_OP)) {
                            List<String> parts = name.split(EQUALS_OP)*.trim()
                            String left = parts[ZERO]
                            String right = parts.size() > 1 ? parts[1] : ''
                            Object leftVal = PropertyUtils.get(ctx, left)
                            String rightVal = stripQuotes(right)
                            return String.valueOf(leftVal) == rightVal
                        }
                        if (name.contains(NOT_EQUALS_OP)) {
                            List<String> parts = name.split(NOT_EQUALS_OP)*.trim()
                            String left = parts[ZERO]
                            String right = parts.size() > 1 ? parts[1] : ''
                            Object leftVal = PropertyUtils.get(ctx, left)
                            String rightVal = stripQuotes(right)
                            return String.valueOf(leftVal) != rightVal
                        }
                        Object v = PropertyUtils.get(ctx, name)
                        return v ? true : false
                    } catch (GroovyRuntimeException ex) {
                        return false
                    }
                }
            }
            return false
        } catch (GroovyRuntimeException ex) {
            return false
        }
    }

    static String stripQuotes(String s) {
        if (s == null) {
            return null
        }
        String t = s.trim()
        if ((t.startsWith(DOUBLE_QUOTE) && t.endsWith(DOUBLE_QUOTE)) ||
            (t.startsWith(SINGLE_QUOTE) && t.endsWith(SINGLE_QUOTE))) {
            return t.substring(1, t.length() - 1)
        }
        return t
    }

    static boolean isCallable(Object o) {
        return o != null && Closure.isInstance(o)
    }

    static Integer resolveJumpTarget(Object jumpTo, Map<String, Integer> labelIndex, Map<Object, Integer> idIndex) {
        if (jumpTo == null) {
            return null
        }
        if (jumpTo != null && Number.isInstance(jumpTo)) {
            return ((Number) jumpTo).intValue()
        }
        String jt = jumpTo as String
        if (labelIndex.containsKey(jt)) {
            return labelIndex[jt]
        }
        try {
            int num = Integer.parseInt(jt)
            if (idIndex.containsKey(num)) {
                return idIndex[num]
            }
        } catch (NumberFormatException nfe) {
            new SimpleLogger().debug("Invalid numeric jump target: ${jt}")
        }
        if (idIndex.containsKey(jt)) {
            return idIndex[jt]
        }
        return null
    }

    static String resolvePath(String path, Step step) {
        if (!path) {
            return path
        }
        if (path.contains('.')) {
            return path
        }
        if (step?.stepPage) {
            return "${step.stepPage}.${path}"
        }
        return path
    }

    static Object deepCopy(Object obj) {
        if (obj != null && obj.metaClass?.getMetaMethod('each') != null &&
            obj.metaClass?.getMetaMethod(GET_AT, Object) != null) {
            Map<Object, Object> m2 = [:]
            ((Map) obj).each { Object k, Object v ->
                m2[k] = deepCopy(v)
            }
            return m2
        }
        if (obj != null && obj.metaClass?.getMetaMethod('collect') != null) {
            return ((List) obj).collect { Object item -> deepCopy(item) }
        }
        if (obj != null && obj.class.array) {
            return ((Object[]) obj).collect { Object item -> deepCopy(item) }
        }
        return obj
    }

    private static void executeActivitySteps(Map<String, Object> executionCtx) {
        Activity act = (Activity) executionCtx['act']
        Map<String, Object> ctx = (Map<String, Object>) executionCtx['ctx']
        boolean stopOnError = executionCtx['stopOnError'] as boolean
        SimpleLogger logger = (SimpleLogger) executionCtx['logger']
        Map<String, Integer> labelIndex = (Map<String, Integer>) executionCtx['labelIndex']
        Map<Object, Integer> idIndex = (Map<Object, Integer>) executionCtx['idIndex']

        int i = ZERO
        int totalIterations = ZERO
        while (i < act.steps.size()) {
            if (totalIterations++ > MAX_TOTAL_ITERATIONS) {
                String errorMessage = """Exceeded max total iterations (${MAX_TOTAL_ITERATIONS})"""
                throw new IllegalStateException(errorMessage)
            }

            Step s = act.steps[i]
            String stepDesc = s?.description ? " - ${s.description}" : ''
            logger.info("Step ${s?.id} - ${s?.method}${stepDesc}")

            if (!checkPrecondition(s, ctx, logger)) {
                i++
                continue
            }

            if (s?.stepPage) {
                logger.debug("Step ${s?.id} stepPage=${s.stepPage}")
            }

            boolean stepThrew = false
            try {
                stepThrew = executeStepWithLoop(s, ctx, logger, stopOnError)
            } catch (GroovyRuntimeException e) {
                stepThrew = true
                logger.error("Error in step ${s?.id}: ${e?.message}", e)
                if (s.required || stopOnError) {
                    throw e
                }
            }

            if (s?.returns) {
                ctx[PX_RETURN_PAGE] = s.returnPage ?: s.stepPage
                logger.info("Step ${s?.id} set return page to ${ctx[PX_RETURN_PAGE]}")
            }

            int nextStep = handleJump(s, ctx, logger, labelIndex, idIndex)
            if (nextStep != MINUS_ONE) {
                i = nextStep
            } else {
                i++
            }
        }
    }

    private static boolean checkPrecondition(Step s, Map<String, Object> ctx, SimpleLogger logger) {
        if (s?.precondition != null) {
            boolean preOk = evaluateCondition(s.precondition, ctx)
            if (s.preconditionNegate) {
                preOk = !preOk
            }
            logger.debug("Step ${s?.id} precondition -> ${preOk}")
            if (!preOk) {
                logger.info("Skipping step ${s?.id} due to precondition=false")
                return false
            }
        }

        if (s?.whenCondition != null) {
            boolean condResult = evaluateCondition(s.whenCondition, ctx)
            logger.debug("Step ${s?.id} whenCondition -> ${condResult}")
            if (!condResult) {
                logger.info("Skipping step ${s?.id} due to whenCondition=false")
                return false
            }
        }
        return true
    }

    private static int handleJump(Step s, Map<String, Object> ctx, SimpleLogger logger,
                                Map<String, Integer> labelIndex, Map<Object, Integer> idIndex) {
        if (s?.jumpTo) {
            boolean jumpOk = true
            if (s.jumpCondition != null) {
                jumpOk = evaluateCondition(s.jumpCondition, ctx)
            }
            if (jumpOk) {
                Integer target = resolveJumpTarget(s.jumpTo, labelIndex, idIndex)
                if (target == null) {
                    logger.warn("Jump target not found: ${s.jumpTo}")
                } else {
                    logger.info("Jumping from step ${s?.id} to index ${target} (via ${s.jumpTo})")
                    return target
                }
            }
        }
        return MINUS_ONE
    }

    /**
     * Executes a step, handling loops. This method assumes the Step class has the following properties for looping:
     * - loop (boolean): true if the step is a loop
     * - loopProperty (String): the property to iterate over (for Page List or Page Group)
     * - loopPage (String): the name of the page to use for each iteration
     * - loopCount (int): the number of times to loop
     * - steps (List<Step>): a list of child steps to execute in each iteration
     */
    private static boolean executeStepWithLoop(Step s, Map<String, Object> ctx,
                                               SimpleLogger logger, boolean stopOnError) {
        if (s == null) {
            return false
        }

        // Handle 'Call' method inside a loop
        if (s.method == 'Call') {
            Deque<Map<String, Object>> loopContextStack = (Deque<Map<String, Object>>) ctx.get('loopContextStack')
            if (loopContextStack != null && !loopContextStack.isEmpty()) {
                Map<String, Object> currentLoopContext = loopContextStack.peek()

                com.pega.pegarules.pub.runtime.ParameterPage callParams = new com.pega.pegarules.pub.runtime.ParameterPage()
                if (s.params) {
                    s.params.each { k, v -> callParams.putObject(k as String, v) }
                }

                callParams.putObject('pyIterationType', currentLoopContext.get('type'))
                callParams.putObject('pyIterationTarget', currentLoopContext.get('target'))
                if (currentLoopContext.get('value') != null) {
                    callParams.putObject('pyPropertyValue', currentLoopContext.get('value'))
                }
                callParams.putObject('pyForEachCount', currentLoopContext.get('count'))

                Map<String, Object> subActivityCtx = [
                        'environment'   : ctx['environment'],
                        'clipboard'     : ctx['clipboard'],
                        'parameterPage' : callParams,
                        'loopContextStack': new java.util.ArrayDeque<Map<String, Object>>(),
                        'logger'        : logger
                ]

                Object target = s.params['activity']
                try {
                    if (target instanceof Activity) {
                        run((Activity) target, subActivityCtx, stopOnError)
                    } else {
                        logger.warn("Call target not Activity: ${target}")
                    }
                    return false // no error
                } catch (GroovyRuntimeException rte) {
                    logger.error("Error calling activity: ${rte?.message}", rte)
                    if (stopOnError) {
                        throw rte
                    }
                    return true // error
                }
            }
        }

        if (!s.loop) {
            try {
                executeStepOperation(s, ctx, logger, stopOnError)
                return false // no error
            } catch (GroovyRuntimeException e) {
                logger.error("Error in step ${s?.id}: ${e?.message}", e)
                if (s.required || stopOnError) {
                    throw e
                }
                return true // error occurred
            }
        }

        // Loop execution
        boolean anyIterationThrew = false
        List<Step> stepsToExecute = s.steps ?: []

        Deque<Map<String, Object>> loopContextStack = (Deque<Map<String, Object>>) ctx.get('loopContextStack')

        if (s.loopProperty) {
            Object loopProp = PropertyUtils.get(ctx, s.loopProperty)
            if (loopProp instanceof ClipboardPage) { // Page Group
                int count = 0
                for (Object page in ((ClipboardPage) loopProp).values()) {
                    if (page instanceof ClipboardPage) {
                        Map<String, Object> loopContext = [
                                type  : 'propertygroup',
                                target: s.loopProperty,
                                value : page,
                                count : ++count
                        ]
                        loopContextStack.push(loopContext)

                        def originalLoopPage = ctx.get(s.loopPage)
                        ctx.put(s.loopPage, page)

                        for (Step childStep in stepsToExecute) {
                            anyIterationThrew |= executeStepWithLoop(childStep, ctx, logger, stopOnError)
                        }

                        if (originalLoopPage != null) {
                            ctx.put(s.loopPage, originalLoopPage)
                        } else {
                            ctx.remove(s.loopPage)
                        }
                        loopContextStack.pop()
                    }
                }
            } else if (loopProp instanceof ClipboardProperty && (loopProp.getMode() == ClipboardProperty.MODE_LIST || loopProp.getMode() == ClipboardProperty.MODE_GROUP)) { // Page List
                int count = 0
                for (ClipboardProperty prop in (ClipboardProperty) loopProp) {
                    if (prop.isPage()) {
                        Map<String, Object> loopContext = [
                                type  : 'propertylist',
                                target: s.loopProperty,
                                value : prop.getPageValue(),
                                count : ++count
                        ]
                        loopContextStack.push(loopContext)

                        def originalLoopPage = ctx.get(s.loopPage)
                        ctx.put(s.loopPage, prop.getPageValue())

                        for (Step childStep in stepsToExecute) {
                            anyIterationThrew |= executeStepWithLoop(childStep, ctx, logger, stopOnError)
                        }

                        if (originalLoopPage != null) {
                            ctx.put(s.loopPage, originalLoopPage)
                        } else {
                            ctx.remove(s.loopPage)
                        }
                        loopContextStack.pop()
                    }
                }
            }
            return anyIterationThrew
        }

        if (s.loopCount != null) {
            int max = s.loopCount as int
            for (int i = 0; i < max; i++) {
                if (i >= MAX_STEP_ITERATIONS) {
                    String errorMessage = """Exceeded max iterations for step ${s.id} (${MAX_STEP_ITERATIONS})"""
                    throw new IllegalStateException(errorMessage)
                }

                Map<String, Object> loopContext = [
                        type  : 'repeat',
                        target: '',
                        value : null,
                        count : i + 1
                ]
                loopContextStack.push(loopContext)

                for (Step childStep in stepsToExecute) {
                    anyIterationThrew |= executeStepWithLoop(childStep, ctx, logger, stopOnError)
                }

                loopContextStack.pop()
            }
            return anyIterationThrew
        }

        // if loop is true, but no loopProperty or loopCount, execute child steps once
        try {
            for (Step childStep in stepsToExecute) {
                executeStepOperation(childStep, ctx, logger, stopOnError)
            }
        } catch (GroovyRuntimeException e) {
            anyIterationThrew = true
            logger.error("Error in step ${s?.id}: ${e?.message}", e)
            if (s.required || stopOnError) {
                throw e
            }
        }
        return anyIterationThrew
    }

}

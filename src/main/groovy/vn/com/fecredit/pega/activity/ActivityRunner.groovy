package org.example

import groovy.transform.CompileStatic

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

    // StepHandler interface + registry for polymorphic handlers
    static interface StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError)
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
        HANDLERS['Property-Remove'] = new PropertyRemoveHandler()
        HANDLERS['Obj-Save'] = new ObjSaveHandler()
        HANDLERS['Obj-Delete'] = new ObjDeleteHandler()
        HANDLERS['Obj-List'] = new ObjListHandler()
        HANDLERS['Obj-Query'] = new ObjQueryHandler()
        HANDLERS['Obj-Open'] = new ObjOpenHandler()
    }

    static class PropertySetHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            s.params.each { Object k, Object v ->
                Object val = isCallable(v) ? ((Closure<Object>) v).call(ctx) : v
                String resolved = resolvePath(k as String, s)
                PropertyUtils.set(ctx, resolved, val)
                logger.debug("Property-Set ${resolved} = ${val}")
            }
        }
    }

    static class WriteMessageHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            Object msg = s.params['message']
            msg = isCallable(msg) ? ((Closure<Object>) msg).call(ctx) : msg
            logger.info("MESSAGE: ${msg}")
        }
    }

    static class CallHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            Object target = s.params['activity']
            try {
                if (target != null && target.metaClass?.getMetaProperty('steps') != null) {
                    run((Activity) target, ctx, stopOnError)
                } else {
                    logger.warn("Call target not Activity: ${target}")
                }
            } catch (GroovyRuntimeException rte) {
                logger.error("Error calling activity: ${rte?.message}", rte)
                if (stopOnError) {
                    throw rte
                }
            }
        }
    }

    static class PageNewHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            String newTargetKey = s.params[TARGET] as String
            String newTargetPath = resolvePath(newTargetKey, s)
            Object initData = s.params[DATA] ?: [:]
            PropertyUtils.set(ctx, newTargetPath, deepCopy(initData))
            logger.debug("Page-New created ${newTargetPath}")
        }
    }

    static class PageCopyHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            String src = s.params['source'] as String
            String dest = s.params[TARGET] as String
            String srcPath = resolvePath(src, s)
            String destPath = resolvePath(dest, s)
            Object srcMap = PropertyUtils.get(ctx, srcPath)
            if (srcMap != null && srcMap.metaClass?.getMetaMethod(GET_AT, String) != null) {
                PropertyUtils.set(ctx, destPath, deepCopy(srcMap))
                logger.debug("Page-Copy from ${srcPath} to ${destPath}")
            } else {
                logger.warn("Page-Copy source not found or not a page: ${srcPath}")
            }
        }
    }

    static class PageRemoveHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            String remPathKey = s.params[TARGET] as String
            String remPath = resolvePath(remPathKey, s)
            PropertyUtils.remove(ctx, remPath)
            logger.debug("${s.method} removed ${remPath}")
        }
    }

    static class PageListHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            String listTargetKey = s.params[TARGET] as String
            String listTargetPath = resolvePath(listTargetKey, s)
            Object source = s.params['source']
            List listVal = []
            if (source != null) {
                Object srcObj = isCallable(source) ? ((Closure) source).call(ctx) : source
                if (srcObj instanceof Collection) {
                    srcObj.each { Object item -> listVal << deepCopy(item) }
                } else {
                    listVal << deepCopy(srcObj)
                }
            }
            PropertyUtils.set(ctx, listTargetPath, listVal)
            logger.debug("Page-List set ${listTargetPath} with ${listVal.size()} items")
        }
    }

    static class PropertyRemoveHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            s.params.each { Object k, Object v ->
                String resolved = resolvePath(k as String, s)
                PropertyUtils.remove(ctx, resolved)
                logger.debug("Property-Remove ${resolved}")
            }
        }
    }

    static class ObjSaveHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            String saveTargetKey = s.params[TARGET] as String
            String savePath = resolvePath(saveTargetKey, s)
            Map pageToSave = (Map) PropertyUtils.get(ctx, savePath)
            if (pageToSave == null || !pageToSave.containsKey(ID)) {
                logger.warn("Obj-Save missing page or id at ${savePath}")
                return
            }
            String cls = (pageToSave[CLASS] ?: s.params[CLASS] ?: 'Unknown') as String
            Object objId = pageToSave[ID]
            Map<Object, Map> store = (Map) ctx.get('_objStore')
            if (store == null) {
                store = [:]
                ctx['_objStore'] = store
            }
            Map byClass = (Map) store.get(cls)
            if (byClass == null) {
                byClass = [:]
                store[cls] = byClass
            }
            byClass[objId] = deepCopy(pageToSave[DATA] ?: [:])
            logger.debug("Obj-Save saved ${cls}#${objId}")
        }
    }

    static class ObjDeleteHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            String delTargetKey = s.params[TARGET] as String
            String delPath = resolvePath(delTargetKey, s)
            Map pageToDel = (Map) PropertyUtils.get(ctx, delPath)
            if (pageToDel != null && pageToDel.containsKey(ID)) {
                String dcls = (pageToDel[CLASS] ?: s.params[CLASS] ?: 'Unknown') as String
                Object did = pageToDel[ID]
                Map<Object, Map> dstore = (Map) ctx.get('_objStore')
                if (dstore != null && dstore.containsKey(dcls)) {
                    ((Map) dstore[dcls]).remove(did)
                }
                PropertyUtils.remove(ctx, delPath)
                logger.debug("Obj-Delete removed ${dcls}#${did} and page ${delPath}")
            } else {
                logger.warn("Obj-Delete: page or id not found at ${delPath}")
            }
        }
    }

    static class ObjListHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            String listTarget = s.params[TARGET] as String
            String className = s.params[CLASS] ?: s.pageClass ?: 'Unknown'
            String listPath = resolvePath(listTarget, s)
            Map<Object, Map> objStore = (Map) ctx.get('_objStore')
            List results = []
            if (objStore != null && objStore.containsKey(className)) {
                ((Map) objStore[className]).each { Object k, Object v ->
                    Map entry = [id: k, data: deepCopy(v)]
                    results << entry
                }
            }
            PropertyUtils.set(ctx, listPath, results)
            logger.debug("Obj-List set ${listPath} with ${results.size()} entries for class ${className}")
        }
    }

    static class ObjQueryHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            String qClass = s.params[CLASS] ?: s.pageClass ?: 'Unknown'
            String qTarget = s.params[TARGET] as String
            String qPath = resolvePath(qTarget, s)
            Map<Object, Map> qstore = (Map) ctx.get('_objStore')
            List qres = []
            if (qstore != null && qstore.containsKey(qClass)) {
                Map byCls = (Map) qstore[qClass]
                if (s.params.containsKey(ID)) {
                    Object qid = s.params[ID]
                    if (byCls.containsKey(qid)) {
                        qres << [id: qid, data: deepCopy(byCls[qid])]
                    }
                } else if (s.params.containsKey('where') && s.params['where'] instanceof Map) {
                    Map where = s.params['where'] as Map
                    byCls.each { Object k, Object v ->
                        Map vMap = (Map) v
                        boolean match = true
                        where.each { Object wk, Object wv ->
                            if (!String.valueOf(vMap[wk]).equals(String.valueOf(wv))) {
                                match = false
                            }
                        }
                        if (match) {
                            qres << [id: k, data: deepCopy(vMap)]
                        }
                    }
                } else {
                    byCls.each { Object k, Object v -> qres << [id: k, data: deepCopy((Map) v)] }
                }
            }
            PropertyUtils.set(ctx, qPath, qres)
            logger.debug("Obj-Query set ${qPath} with ${qres.size()} entries for class ${qClass}")
        }
    }

    static class ObjOpenHandler implements StepHandler {
        void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
            String targetPathKey = s.params[TARGET] as String
            Object id = s.params[ID]
            Map<String, Object> page = [:]
            page[ID] = id
            page[CLASS] = s.params[CLASS] ?: s.pageClass ?: 'Unknown'
            Map<Object, Map> store = (Map) ctx.get('_objStore')
            Map data = [:]
            String clsName = (String) page[CLASS]
            Map clsMap = store != null && clsName != null ? (Map) store.get(clsName) : null
            if (clsMap != null && id != null && clsMap.containsKey(id)) {
                data = (Map) deepCopy((Map) clsMap.get(id))
            } else {
                data = (Map) (s.params[DATA] ?: [:])
            }
            page[DATA] = data
            String targetPath = resolvePath(targetPathKey, s)
            PropertyUtils.set(ctx, targetPath, page)
            logger.debug("Obj-Open loaded ${targetPath} with id ${id} (class ${page[CLASS]})")
        }
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
            } else if (!stepThrew) {
                i++
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

    private static boolean executeStepWithLoop(Step s, Map<String, Object> ctx,
                                               SimpleLogger logger, boolean stopOnError) {
        if (s == null) {
            return false
        }
        if (s.loop) {
            int iter = ZERO
            boolean cont = true
            while (cont) {
                if (++iter > MAX_STEP_ITERATIONS) {
                    String errorMessage = """Exceeded max iterations for step ${s.id} (${MAX_STEP_ITERATIONS})"""
                    throw new IllegalStateException(errorMessage)
                }
                executeStepOperation(s, ctx, logger, stopOnError)

                if (s.loopCondition != null) {
                    Object condVal = evaluateCondition(s.loopCondition, ctx)
                    cont = ExpressionEvaluator.truthy(condVal)
                } else if (s.loopCount != null) {
                    cont = (iter < s.loopCount)
                } else {
                    cont = false
                }
            }
            return false
        }
        executeStepOperation(s, ctx, logger, stopOnError)
        return false
    }

}

package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.Activity
import vn.com.fecredit.pega.activity.ActivityRunner
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.Step
import vn.com.fecredit.pega.activity.StepHandler

class CallHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        Object target = s.params['activity']
        try {
            if (target != null && target.metaClass?.getMetaProperty('steps') != null) {
                ActivityRunner.run((Activity) target, ctx, stopOnError)
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
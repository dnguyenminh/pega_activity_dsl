package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.model.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.isCallable
import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

class PropertySetHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        s.params.each { Object k, Object v ->
            Object val = isCallable(v) ? ((Closure<Object>) v).call(ctx) : v
            String resolved = resolvePath(k as String, s)
            PropertyUtils.set(ctx, resolved, val)
            logger.debug("Property-Set ${resolved} = ${val}")
        }
    }
}
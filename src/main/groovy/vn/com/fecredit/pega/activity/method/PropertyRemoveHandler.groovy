package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

class PropertyRemoveHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        s.params.each { Object k, Object v ->
            String resolved = resolvePath(k as String, s)
            PropertyUtils.remove(ctx, resolved)
            logger.debug("Property-Remove ${resolved}")
        }
    }
}
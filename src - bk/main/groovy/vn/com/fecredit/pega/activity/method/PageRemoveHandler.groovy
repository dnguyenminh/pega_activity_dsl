package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.model.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

class PageRemoveHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        String remPathKey = s.params['target'] as String
        String remPath = resolvePath(remPathKey, s)
        PropertyUtils.remove(ctx, remPath)
        logger.debug("${s.method} removed ${remPath}")
    }
}
package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.deepCopy
import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

class PageNewHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        String newTargetKey = s.params['target'] as String
        String newTargetPath = resolvePath(newTargetKey, s)
        Object initData = s.params['data'] ?: [:]
        PropertyUtils.set(ctx, newTargetPath, deepCopy(initData))
        logger.debug("Page-New created ${newTargetPath}")
    }
}
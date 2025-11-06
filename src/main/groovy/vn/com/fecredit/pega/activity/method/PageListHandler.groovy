package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.deepCopy
import static vn.com.fecredit.pega.activity.ActivityRunner.isCallable
import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

class PageListHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        String listTargetKey = s.params['target'] as String
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
package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.deepCopy
import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

class PageCopyHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        String src = s.params['source'] as String
        String dest = s.params['target'] as String
        String srcPath = resolvePath(src, s)
        String destPath = resolvePath(dest, s)
        Object srcMap = PropertyUtils.get(ctx, srcPath)
        if (srcMap != null && srcMap.metaClass?.getMetaMethod('getAt', String) != null) {
            PropertyUtils.set(ctx, destPath, deepCopy(srcMap))
            logger.debug("Page-Copy from ${srcPath} to ${destPath}")
        } else {
            logger.warn("Page-Copy source not found or not a page: ${srcPath}")
        }
    }
}
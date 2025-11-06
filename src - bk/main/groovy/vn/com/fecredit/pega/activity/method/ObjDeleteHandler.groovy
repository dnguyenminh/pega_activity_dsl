package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.model.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

class ObjDeleteHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        String delTargetKey = s.params['target'] as String
        String delPath = resolvePath(delTargetKey, s)
        Map pageToDel = (Map) PropertyUtils.get(ctx, delPath)
        if (pageToDel != null && pageToDel.containsKey('id')) {
            String dcls = (pageToDel['class'] ?: s.params['class'] ?: 'Unknown') as String
            Object did = pageToDel['id']
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
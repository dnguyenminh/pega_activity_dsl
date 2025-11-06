package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.deepCopy
import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

class ObjSaveHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        String saveTargetKey = s.params['target'] as String
        String savePath = resolvePath(saveTargetKey, s)
        Map pageToSave = (Map) PropertyUtils.get(ctx, savePath)
        if (pageToSave == null || !pageToSave.containsKey('id')) {
            logger.warn("Obj-Save missing page or id at ${savePath}")
            return
        }
        String cls = (pageToSave['class'] ?: s.params['class'] ?: 'Unknown') as String
        Object objId = pageToSave['id']
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
        byClass[objId] = deepCopy(pageToSave['data'] ?: [:])
        logger.debug("Obj-Save saved ${cls}#${objId}")
    }
}
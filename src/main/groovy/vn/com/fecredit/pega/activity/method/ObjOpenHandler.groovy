package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.deepCopy
import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

class ObjOpenHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        String targetPathKey = s.params['target'] as String
        Object id = s.params['id']
        Map<String, Object> page = [:]
        page['id'] = id
        page['class'] = s.params['class'] ?: s.pageClass ?: 'Unknown'
        Map<Object, Map> store = (Map) ctx.get('_objStore')
        Map data = [:]
        String clsName = (String) page['class']
        Map clsMap = store != null && clsName != null ? (Map) store.get(clsName) : null
        if (clsMap != null && id != null && clsMap.containsKey(id)) {
            data = (Map) deepCopy((Map) clsMap.get(id))
        } else {
            data = (Map) (s.params['data'] ?: [:])
        }
        page['data'] = data
        String targetPath = resolvePath(targetPathKey, s)
        PropertyUtils.set(ctx, targetPath, page)
        logger.debug("Obj-Open loaded ${targetPath} with id ${id} (class ${page['class']})")
    }
}
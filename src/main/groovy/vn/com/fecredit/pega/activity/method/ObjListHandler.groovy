package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.deepCopy
import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

class ObjListHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        String listTarget = s.params['target'] as String
        String className = s.params['class'] ?: s.pageClass ?: 'Unknown'
        String listPath = resolvePath(listTarget, s)
        Map<Object, Map> objStore = (Map) ctx.get('_objStore')
        List results = []
        if (objStore != null && objStore.containsKey(className)) {
            ((Map) objStore[className]).each { Object k, Object v ->
                Map entry = [id: k, data: deepCopy(v)]
                results << entry
            }
        }
        PropertyUtils.set(ctx, listPath, results)
        logger.debug("Obj-List set ${listPath} with ${results.size()} entries for class ${className}")
    }
}
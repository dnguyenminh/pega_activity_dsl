package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.PropertyUtils
import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.model.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.deepCopy
import static vn.com.fecredit.pega.activity.ActivityRunner.resolvePath

class ObjQueryHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        String qClass = s.params['class'] ?: s.pageClass ?: 'Unknown'
        String qTarget = s.params['target'] as String
        String qPath = resolvePath(qTarget, s)
        Map<Object, Map> qstore = (Map) ctx.get('_objStore')
        List qres = []
        if (qstore != null && qstore.containsKey(qClass)) {
            Map byCls = (Map) qstore[qClass]
            if (s.params.containsKey('id')) {
                Object qid = s.params['id']
                if (byCls.containsKey(qid)) {
                    qres << [id: qid, data: deepCopy(byCls[qid])]
                }
            } else if (s.params.containsKey('where') && s.params['where'] instanceof Map) {
                Map where = s.params['where'] as Map
                byCls.each { Object k, Object v ->
                    Map vMap = (Map) v
                    boolean match = true
                    where.each { Object wk, Object wv ->
                        if (!String.valueOf(vMap[wk]).equals(String.valueOf(wv))) {
                            match = false
                        }
                    }
                    if (match) {
                        qres << [id: k, data: deepCopy(vMap)]
                    }
                }
            } else {
                byCls.each { Object k, Object v -> qres << [id: k, data: deepCopy((Map) v)] }
            }
        }
        PropertyUtils.set(ctx, qPath, qres)
        logger.debug("Obj-Query set ${qPath} with ${qres.size()} entries for class ${qClass}")
    }
}
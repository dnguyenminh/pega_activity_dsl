package vn.com.fecredit.pega.activity

import vn.com.fecredit.pega.activity.model.Step

interface StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError)
}

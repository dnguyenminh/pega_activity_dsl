package vn.com.fecredit.pega.activity.method

import vn.com.fecredit.pega.activity.SimpleLogger
import vn.com.fecredit.pega.activity.Step
import vn.com.fecredit.pega.activity.StepHandler

import static vn.com.fecredit.pega.activity.ActivityRunner.isCallable

class WriteMessageHandler implements StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError) {
        Object msg = s.params['message']
        msg = isCallable(msg) ? ((Closure<Object>) msg).call(ctx) : msg
        logger.info("MESSAGE: ${msg}")
    }
}
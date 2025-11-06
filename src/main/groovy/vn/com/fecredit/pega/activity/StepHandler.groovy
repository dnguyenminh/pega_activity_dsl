package vn.com.fecredit.pega.activity

interface StepHandler {
    void handle(Step s, Map<String, Object> ctx, SimpleLogger logger, boolean stopOnError)
}

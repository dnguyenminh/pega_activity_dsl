package com.pega.pegarules.pub.runtime

import java.util.HashMap

interface UnitTestCaseExecutor {
    String PARAM_NAME_FOR_RUT_DEFAULT_PRIMARY_PAGE_FOR_EXECUTION = "RUT_DEFAULT_PRIMARY_PAGE_FOR_EXECUTION"
    String RUT_DEFAULT_PRIMARY_PAGE_FOR_EXECUTION = "pyDefaultPrimaryPageForExecution"
    String VERSION = "8.4.0"

    void execute(HashMap<String, Object> keys, ParameterPage globalParamPage)
    void postExecute(HashMap<String, Object> keys, ParameterPage globalParamPage)
    void preExecute(HashMap<String, Object> keys, ParameterPage globalParamPage)
}

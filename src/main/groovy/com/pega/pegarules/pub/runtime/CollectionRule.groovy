package com.pega.pegarules.pub.runtime

import com.pega.pegarules.pub.util.StringMap

interface CollectionRule {
    String VERSION = "8.4.0"
    String checkPrecondition()
    String checkStopConditions()
    java.util.Date getCircumstanceDateOverride()
    String getCircumstanceOverride()
    java.util.Date getDateOverride()
    StringMap getRuleKeys()
    String getRuleSetName()
    ParameterPage getStepParams()
    void performPresets()
}

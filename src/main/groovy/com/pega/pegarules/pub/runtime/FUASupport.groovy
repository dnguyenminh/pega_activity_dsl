package com.pega.pegarules.pub.runtime

import com.pega.pegarules.priv.generator.DependentRuleInfo
import com.pega.pegarules.priv.tracer.RuleTraceInfo

interface FUASupport {
    String VERSION = "8.4.0"
    void fuaDestroy()
    void fuaInit()
    String getAspect()
    String getDefinitionAppliesToClass()
    DependentRuleInfo[] getDependentRuleInfos()
    String getPersonal()
    RuleTraceInfo getTraceInfoForUnqualifiedVersion()
    String[] getUsedRules()
    boolean hasPersonal()
    boolean isTransient()
    void setTransient()
}

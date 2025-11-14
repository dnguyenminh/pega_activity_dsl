package com.pega.pegarules.pub.runtime

import com.google.gson.JsonObject
import com.pega.pegarules.priv.runtime.IUIComponentRuntime

interface IUIComponentInfo {
    String VERSION = "8.4.0"

    void evaluateWhenForRow(JsonObject truthTable, IUIComponentRuntime uiComponentRuntime, int index, String pyUniqueComponentId)
    String getContext()
    Map getContextParams()
    List<IUIComponentInfo> getInfoList()
    boolean getIsrepeatingLayout()
    String getVisibleWhenId()
    String getWhenRule()
    void setContext(String string)
    void setContextParams(Map map)
    void setInfoList(List<IUIComponentInfo> infoList)
    void setIsrepeatingLayout(boolean b)
    void setVisibilityWhenRule(String whenRule)
    void setVisibleWhenId(String uniqueID)
}

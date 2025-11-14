package com.pega.pegarules.pub.runtime

import com.pega.pegarules.pub.clipboard.ClipboardProperty
import com.pega.pegarules.pub.util.StringMap

interface StreamBuilderToolKit extends PublicAPI {
    String VERSION = "8.4.0"

    void appendCSF(String aSource)
    void appendFormatted(String aSource, byte aFormat)
    void appendParamCSF(String aSource)
    void appendProperty(ClipboardProperty aProperty, String aStreamSpec, boolean aWantInput)
    void appendStream(StringMap aKeys)
    void appendStream(StringMap aKeys, ParameterPage aParamPage)
    void appendString(String aSource)
    void appendURL(String aURL)
    void disableInput()
    ClipboardProperty getActive()
    String getActiveName()
    String getActiveValue()
    IControlRuntimeUtilities getControlUtilities()
    String getCurrentStream()
    StringBuilder getCurrentStreamBuffer()
    String getEntryHandle(String aReference)
    String getOpposite(String aValue)
    String getParamValueCSF(String aSource)
    String getRootParamValue(String aSource)
    String getSaveValue(String aName)
    String getTargetName()
    String getTargetValue()
    boolean hasEditableEnabled()
    boolean hasInputEnabled()
    boolean includeShowDeclarativeProperty()
    @Deprecated
    boolean includeShowMe()
    boolean includeShowProperty()
    void popStreamBody()
    StringBuilder pushStreamBody()
    ClipboardProperty putActive(ClipboardProperty aNewActive)
    void putSaveValue(String aName, String aValue)
    void putTarget(Object aTarget)
    String returnFormatted(String aSource, byte aFormat)
    void setEditable(boolean bEditable)
    void setInput(boolean aInput)
    @Deprecated
    void setShowProperty(boolean aValue)
    String thisSubscript()
    boolean useModeInput()
}

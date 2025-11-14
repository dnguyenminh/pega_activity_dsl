package com.pega.pegarules.pub.runtime

import com.pega.ibm.icu.math.BigDecimal
import com.pega.pegarules.pub.clipboard.ClipboardPage
import com.pega.pegarules.pub.clipboard.ClipboardProperty
import com.pega.pegarules.pub.clipboard.InvalidParameterException
import com.pega.pegarules.pub.clipboard.WrongModeException
import com.pega.pegarules.pub.clipboard.mapping.ClipboardDataStreamMapper
import com.pega.pegarules.pub.clipboard.mapping.DataFormat
import com.pega.pegarules.pub.context.AgentUtils
import com.pega.pegarules.pub.context.ICustomEventLogger
import com.pega.pegarules.pub.context.PRAuthorization
import com.pega.pegarules.pub.context.PRRequestor
import com.pega.pegarules.pub.context.PRStackFrame
import com.pega.pegarules.pub.context.PRThread
import com.pega.pegarules.pub.context.ProcessingStatus
import com.pega.pegarules.pub.crypto.IPRCrypto
import com.pega.pegarules.pub.database.Database
import com.pega.pegarules.pub.dictionary.Dictionary
import com.pega.pegarules.pub.dictionary.ImmutablePropertyAliasInfo
import com.pega.pegarules.pub.generator.FirstUseAssemblerException
import com.pega.pegarules.pub.generator.UnresolvedAssemblyError
import com.pega.pegarules.pub.pal.PAL
import com.pega.pegarules.pub.presence.PresenceService
import com.pega.pegarules.pub.simpleurl.SimpleURLAPI
import com.pega.pegarules.pub.tracer.TracerUserWatchList
import com.pega.pegarules.pub.util.DateTimeUtils
import com.pega.pegarules.pub.util.EFormUtils
import com.pega.pegarules.pub.util.PRFile
import com.pega.pegarules.pub.util.PRSystemSettings
import com.pega.pegarules.pub.util.ParseUtils
import com.pega.pegarules.pub.util.ServiceUtils
import com.pega.pegarules.pub.util.StringMap
import com.pega.pegarules.pub.util.StructuredUtils
import com.pega.pegarules.pub.util.XMLUtils
import com.pega.pegarules.pub.util.PDFUtils
import com.pega.pegarules.priv.InfEngUtils
import com.pega.pegarules.priv.tracer.RuleTraceInfo
import java.io.InputStream
import java.io.Reader
import java.util.Date
import java.util.Map

interface PublicAPI {
    String VERSION = "8.4.0"

    void applyCollection(StringMap aCollectionKeys, ClipboardPage aPage, ParameterPage aParams)
    void applyModel(ClipboardPage aClipboardPage, ParameterPage aNewParamPage, String aModelName)
    void applyModel(ClipboardPage aClipboardPage, StringMap aKeys, ParameterPage aNewParamPage)
    boolean checkCachingHeaders(long aLastModifiedDate, long aFileLength)
    void closeHTMLDoc()
    ClipboardPage createPage(String aClassName, String aPageName)
    void doAction(StringMap aKeys, ClipboardPage aNewStepPage, ParameterPage aNewParam)
    void doActivity(StringMap aKeys, ClipboardPage aNewPrimaryPage, ParameterPage aNewParam)
    ParameterPage doAutomation(StringMap aKeys, ParameterPage input)
    void editInput(ClipboardProperty aTarget, String aValue, String aEditName)
    boolean editValidate(ClipboardProperty aTarget, String aValidateName)
    String entryHandle2Reference(String aEntryHandle)
    @Deprecated
    boolean eTagMatches(long aLastModifiedDate)
    boolean evaluateWhen(StringMap aKeys)
    ClipboardPage findPage(String aPageReference)
    ClipboardPage findPage(String aPageReference, boolean aIfPresent)
    ClipboardPage findPage(String aPageReference, ParameterPage aParameterPage)
    ClipboardPage findPage(String aPageReference, ParameterPage aParameterPage, boolean aIfPresent)
    ClipboardPage findPageByHandle(String aInsKey)
    ClipboardPage findPageWithException(String aPageReference)
    String formatMessage(String aKey, Database aDb)
    ClipboardPage generateAgentDataPage(ClipboardPage aRuleAgentQueue)
    String getActivityClassName()
    ProcessingStatus getActivityStatus()
    AgentUtils getAgentUtils()
    Object getAliasValue(ClipboardPage aPage, ImmutablePropertyAliasInfo aPropertyAliasInfo)
    Object getAliasValue(ClipboardPage aPage, String aClassName, String aAliasName)
    Object getAliasValue(String aClassName, String aAliasName)
    PRAuthorization getAuthorizationHandle()
    ClipboardDataStreamMapper getClipboardDataStreamMapper(DataFormat format)
    IControlRuntimeUtilities getControlUtilities()
    String getCSRFSessionToken()
    String getCSRFToken(PRThread thread)
    Database getDatabase()
    DateTimeUtils getDateTimeUtils()
    Dictionary getDictionary()
    EFormUtils getEFormUtils()
    String getHTMLIDForProperty(boolean aRepeating, String aUiElement)
    String getHTMLIDForProperty(String aUiElement)
    ClipboardProperty getIfPresent(String aReference)
    @Deprecated
    GeneratedJava getImplementation(StringMap aKeys) throws FirstUseAssemblerException
    InfEngUtils getInfEngUtils()
    String getLocalizedText(String aRef)
    String getLocalizedTextForParameterizedString(String aRef, String aString)
    String getLocalizedTextForString(String aRef, String aString)
    String getLocalizedTextForString(String aRef, String aString, boolean aReturnEmptyForLocalisedString)
    String getLocalizedTextForString(String aRef, String aString, byte aMode)
    String getLocalizedTextForString(String aRef, String aString, byte aMode, boolean aReturnEmptyForLocalisedString)
    Map getMetadata(StringMap aKeys, ClipboardPage aPrimaryPage)
    String getObfuscationKey()
    PAL getPAL()
    BigDecimal getParamAsBigDecimal(char aType, String aName)
    boolean getParamAsBoolean(char aType, String aName)
    Date getParamAsDate(char aType, String aName)
    double getParamAsDouble(char aType, String aName)
    int getParamAsInteger(char aType, String aName)
    String getParamCSF(String aSource)
    ParameterPage getParameterPage()
    String getParamValue(String aName)
    ParseState getParseState(InputStream stream)
    ParseState getParseState(Reader reader)
    ParseUtils getParseUtils()
    PDFUtils getPDFUtils()
    IPRCrypto getPRCrypto()
    PresenceService getPresenceService()
    ClipboardPage getPrimaryPage()
    ClipboardProperty getProperty(String aReference)
    PRRequestor getRequestor()
    String getRuleMessage(String aMsgDescr)
    String getRuleMessage(String aMsgDescr, String messageClass)
    String getRuleSecurityMode()
    ICustomEventLogger getSecEventLogger()
    ServiceUtils getServiceUtils()
    SimpleURLAPI getSimpleURLAPI()
    PRStackFrame getStackFrame()
    ClipboardPage getStepPage()
    ProcessingStatus getStepStatus()
    String getStream(StringMap aKeys, ClipboardPage aNewPrimary)
    String getStream(StringMap aKeys, ClipboardPage aNewPrimary, long aMode)
    StructuredUtils getStructuredUtils()
    PRSystemSettings getSystemSettings()
    PRThread getThread()
    TracerUserWatchList getTracerUserWatchList()
    XMLUtils getXMLUtils()
    boolean hasIfNoneMatch()
    boolean hasImplicitPrivilege(RuleTraceInfo aRuleInUse)
    boolean hasImplicitPrivilege(String objClass, String appliesToClassName, String instanceName, String ruleSetName, String ruleSetVersion)
    boolean hasJSONWebToken()
    boolean interpretBoolean(String aBooleanString)
    ClipboardPage invokeConnector(ClipboardPage aConnector, ClipboardPage aStepPage, ParameterPage aParams)
    boolean isAsciiEncodable(char aCharacter)
    boolean isCSRFMitigationEnabled()
    boolean isFirstActivity()
    boolean isNotModifiedSet()
    boolean isValidDataPage(String dataPageName)
    boolean isVTableEnabledForControls()
    void openHTMLDoc()
    void preventStaleHTTPRequestProcessing(String aFrameName)
    void putParamValue(String aName, boolean aValue)
    void putParamValue(String aName, Boolean aValue)
    void putParamValue(String aName, char aType, BigDecimal aValue)
    void putParamValue(String aName, char aType, boolean aValue)
    void putParamValue(String aName, char aType, Boolean aValue)
    void putParamValue(String aName, char aType, Date aValue)
    void putParamValue(String aName, char aType, double aValue)
    void putParamValue(String aName, char aType, Double aValue)
    void putParamValue(String aName, char aType, int aValue)
    void putParamValue(String aName, char aType, Integer aValue)
    void putParamValue(String aName, char aType, Object aValue)
    void putParamValue(String aName, char aType, String aValue)
    void putParamValue(String aName, ClipboardProperty aValue)
    void putParamValue(String aName, double aValue)
    void putParamValue(String aName, Double aValue)
    void putParamValue(String aName, int aValue)
    void putParamValue(String aName, Integer aValue)
    void putParamValue(String aName, Object aValue)
    void putParamValue(String aName, String aValue)
    boolean removeDataPage(String aDataPageName)
    boolean removeDataPage(String aDataPageName, ParameterPage aParamPage)
    void resetNotModified()
    String sendFile(byte[] aFileData, String aFileName, boolean aPersistFileToServiceExport, StringMap aHttpHeaders, boolean aSendForDownload)
    String sendFile(ClipboardPage aInstancePage, String aFileSourceReference, boolean aIsBase64Encoded, String aFileNameReference, String aFileName, boolean aPersistFileToServiceExport, StringMap aHttpHeaders, boolean aSendForDownload)
    String sendFile(InputStream aFileInputStream, String aFileName, StringMap aHttpHeaders, boolean aSendForDownload)
    String sendFile(PRFile aFile, boolean aDeleteFile, StringMap aHttpHeaders, boolean aSendForDownload)
    String sendFile(StringMap aInstanceKeys, String aFileSourceReference, boolean aIsBase64Encoded, String aFileNameReference, String aFileName, boolean aPersistFileToServiceExport, StringMap aHttpHeaders, boolean aSendForDownload)
    String sendFile(String aInstanceHandle, String aFileSourceReference, boolean aIsBase64Encoded, String aFileNameReference, String aFileName, boolean aPersistFileToServiceExport, StringMap aHttpHeaders, boolean aSendForDownload)
    void sendHTTPRedirect(String aLocation)
    void setCachingHeaders(long aLastModifiedDate)
    @Deprecated
    void setETag(long aLastModifiedDate)
    void setNotModified()
    void setNotModified(boolean aModified)
}

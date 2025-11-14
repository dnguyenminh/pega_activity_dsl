package com.pega.pegarules.pub.dictionary

import com.pega.pegarules.pub.clipboard.ClipboardProperty

/**
 * Simplified interface stub based on Pega 8.4 Javadocs.
 */
interface ImmutablePropertyInfo {

    boolean hasTypeInteger()
    boolean hasTypeDecimal()
    boolean hasTypeDouble()
    boolean hasTypeAmount()
    boolean hasTypeTopLevelPage()
    boolean hasTypeIncludedPage()
    boolean hasTypeDistinctPage()

    char getMode()
    char getType()
    int getMaxLength()
    String getEmbeddedPageClass()
    String getJavaObjectClass()

    boolean hasEditDefined()

    boolean isModeString()
    boolean isModeJavaObject()
    boolean isModeJavaObjectGroup()
    boolean isModeStringList()
    boolean isModeStringGroup()
    boolean isModePage()
    boolean isModeGroup()
    boolean isModeList()
    boolean isModeUnknown()
    boolean isModePageList()
    boolean isModePageGroup()

    boolean hasTypeUnknown()
    boolean hasTypeFreeform()
    boolean hasTypeText()
    boolean hasTypeIdentifier()
    boolean hasTypePassword()
    boolean hasTypeTextEncrypted()
    boolean hasTypeDateTime()
    boolean hasTypeDate()
    boolean hasTypeTimeOfDay()
    boolean hasTypeTrueFalse()
    boolean hasTypeYesOrNo()
    boolean hasTypeNumber()

    boolean hasAccessStandard()
    boolean hasAccessSpecial()
    boolean hasTableEdit()

    String getEntryCode()

    boolean validate(ClipboardProperty aProperty, boolean aForceExpand)

    String getLocalizedText(ClipboardProperty aProp)
    String getLocalizedText(ClipboardProperty aProp, String aString)
    String getStandardText(ClipboardProperty aProp, String aKey)

    boolean isPropertyADeclarativeTarget(String aReference, String aClassName)
}

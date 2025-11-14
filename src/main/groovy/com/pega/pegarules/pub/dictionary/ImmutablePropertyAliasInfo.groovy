package com.pega.pegarules.pub.dictionary

import com.pega.pegarules.pub.clipboard.ClipboardProperty

/**
 * Simplified interface stub based on Pega 8.4 Javadocs.
 */
interface ImmutablePropertyAliasInfo {

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
    boolean hasTypeInteger()
    boolean hasTypeDecimal()
    boolean hasTypeDouble()
    boolean hasTypeAmount()

    boolean hasTypeNumberDeprecated() // placeholder for deprecated name

    ClipboardProperty getPropertyReferences()

    String getEntryCode()

    boolean usesExpression()

    String getReference()

    String getName()

    String getClassName()

    String comparator()

    String getStreamName()
}

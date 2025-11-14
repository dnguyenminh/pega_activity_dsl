package com.pega.pegarules.pub.dictionary

import com.pega.pegarules.pub.clipboard.ClipboardPage
import com.pega.pegarules.pub.clipboard.ClipboardProperty

import java.util.Map
import java.util.Set

/**
 * Interface generated from Pega 8.4 Javadocs (overview).
 * Method signatures added to match the upstream API surface.
 */
interface Dictionary {

    ImmutablePropertyInfo getImmutablePropertyInfo(String aClassName, String aPropertyName)

    ClassInfo getClassInfo(String aClassName)

    void reset()

    String fromDefinition(ClipboardProperty aProperty, String aReference)

    String fromDefinition(String aClassName, String aPropertyName, String aReference)

    String fromDefinition(ClipboardPage aClassInstance, String aReference)

    String fromDefinition(String aClassName, String aReference)

    boolean hasQualifier(ClipboardProperty aProperty, String aQualifierName)

    boolean hasQualifier(String aClassName, String aPropertyName, String aQualifierName)

    boolean hasRuntimeQualifier(ClipboardProperty aProperty, String aQualifierName)

    String getQualifier(ClipboardProperty aProperty, String aQualifierName)

    String getQualifier(String aClassName, String aPropertyName, String aQualifierName)

    String getExpectedSize(String aClassName, String aPropertyName)

    ClipboardProperty getPropertyViaQualifier(ClipboardProperty aProperty, String aQualifierName)

    boolean setPropertyViaQualifier(ClipboardProperty aProperty, String aQualifierName, String aValue)

    boolean setPropertyViaQualifier(ClipboardProperty aProperty, String aQualifierName, ClipboardProperty aValue)

    boolean validate(ClipboardPage aPage, boolean aForceExpand)

    boolean validate(ClipboardProperty aProp)

    String validate(String aClassName, String aPropertyName, String aValue)

    boolean validate(ClipboardProperty aProp, boolean aUseDictionary)

    boolean validate(ClipboardProperty aProp, boolean aUseDictionary, boolean aAvoidAutoChain)

    boolean validate(ClipboardProperty aProp, String aValue, boolean aUseDictionary, boolean aAvoidAutoChain)

    boolean validateReference(String aClassName, String aReference)

    boolean validateReferenceWithCaseSensitivity(String aClassName, String aReference)

    boolean validateReferenceWithWildcards(String aClassName, String aReference)

    String getTypeName(char aType)

    String getModeName(char aMode)

    String getClassName(String aRootClass, String aReference)

    String getClassName(String aRootClass, String aReference, boolean aAllowEmptySubscripts)

    String getRuleSet(String aClassName, String aPropertyName)

    String getRuleSetVersion(String aClassName, String aPropertyName)

    String getPropertyClass(String aClassName, String aPropertyName)

    String getPropertyLabel(String aClassName, String aPropertyName)

    Set getJavaPropertyNames(String aClassName)

    boolean isReference(String aClassName, String aPropertyName)

    boolean isRetrieveEachPageSeperately(String aClassName, String aPropertyName)

    boolean usesQualifiedRuleResolution(String aClassName)

    String getFormType(String aClassName)

    String getAppletHarness(String aClassName)

    String getApplet(String aClassName)

    String getCategory(String aClassName)

    String getMethodStatus(String aClassName)

    String getDefinedPropertyName(String aClassName, String aPropertyName)

    String getGatewayClass(String aClassName, String aPropertyName)

    Map getGatewayPropertyMap(String aClassName, String aPropertyName)

    boolean isThereAnyLinkedPropertyInThisPropertyReference(String aClassName, String aPropRef)

    String shouldSkipLocalization(String aClassName, String aPropertyName)
}

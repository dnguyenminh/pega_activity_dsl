package com.pega.pegarules.pub.dictionary

/**
 * Simplified interface stub based on Pega 8.4 Javadocs.
 */
interface PropertyInfo {

    String getName()

    boolean hasAccessReserved()

    ClassInfo getOwner()

    String fromDefinition(String aReference)

    String getQualifier(String aQualifierName)

    boolean hasQualifier(String aQualifierName)
}
package com.pega.pegarules.pub.presence

import java.util.Map

/**
 * Simplified interface stub based on Pega 8.4 Javadocs.
 */
interface PresenceService {

    boolean setAttributes(String operatorId, String customCategoryName, Map<String,String> customAttributeNameAndValues, Map<String,String> standardAttributeNameAndValues) throws PresenceException

    boolean setAttributesWithoutOverwrite(String operatorId, String customCategoryName, Map<String,String> customAttributeNameAndValues, Map<String,String> standardAttributeNameAndValues) throws PresenceException

    void setCurrentRequestorStateAsDisconnected()

    boolean clearCustomAttributes(String operatorId, String customCategoryName) throws PresenceException
}

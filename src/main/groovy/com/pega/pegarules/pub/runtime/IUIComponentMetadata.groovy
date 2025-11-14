package com.pega.pegarules.pub.runtime

import com.google.gson.JsonObject

interface IUIComponentMetadata {
    String PY_TEMPLATES = "pyTemplates"
    String VERSION = "8.4.0"

    void addMetadataInArray(String key, IUIComponentMetadata metadata)
    void adoptJSON(String jsonString)
    IUIComponentInfo getInfo()
    String getJSON()
    JsonObject getNestedObject(String key)
    String getNestedProperty(String key)
    JsonObject getObject()
    String getString(String key)
    String getStringIfPresent(String aKey)
    void put(String key, boolean value)
    void put(String key, IUIComponentMetadata[] metadatas)
    void put(String key, String value)
    void putMetadata(String key, IUIComponentMetadata metadata)
    void putNestedProperty(String key, String value)
    void putString(String key, String value)
    Object remove(String key)
}

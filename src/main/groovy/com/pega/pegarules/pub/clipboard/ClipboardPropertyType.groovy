package com.pega.pegarules.pub.clipboard

enum ClipboardPropertyType {
    STRING,
    INTEGER,
    DECIMAL,
    BOOLEAN,
    DATE,
    PAGE,
    PAGELIST,
    // Java-specific property modes (from Pega):
    // Java ObjectList: array/list of references to Java objects (1-based subscripts)
    JAVA_OBJECT_LIST,
    // Java Object Group: unordered group of references to Java objects
    JAVA_OBJECT_GROUP,
    // Java Property: single property of an external Java object (JavaBean mapping)
    JAVA_PROPERTY,
    // Java Property List: array property of an external Java object
    JAVA_PROPERTY_LIST
}
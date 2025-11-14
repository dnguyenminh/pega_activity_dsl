package com.pega.pegarules.pub.util

import java.util.Map

/**
 * This interface specializes the <code>java.util.Map</code> interface to expect
 * that both the key and the value will be instances of <code>String</code>.
 * This is a common pattern for typical usage where a <code>String</code> value
 * is identified by its name.
 *
 * @author    (various)
 */
interface StringMap extends Map<String, String> {

    /**
     * Identifies the <code>String</code> value associated with the given name.
     * @param aName the name of the value to be located
     * @return the <code>String</code> value, or <code>null</code> if there is no such association
     */
    String getString(String aName)

    /**
     * Establishes an association between the specified value and the specified
     * name. If an association for this name already exists, it is replaced.
     * @param aName the name to be associated with the value
     * @param aValue the value to be associated with the name
     * @return the previous value associated with the name, or <code>null</code> if there was no such association
     */
    String putString(String aName, String aValue)

    /**
     * Returns a string representation of the key/value pairs in this object.
     * @return a string representation of the key/value pairs in this object
     */
    String toString()

}
package com.pega.pegarules.pub.runtime

import com.pega.pegarules.pub.clipboard.ClipboardProperty
import com.pega.pegarules.pub.util.StringMap

/**
 * The <code>ParameterPage</code> class manages a collection of named parameters with
 * <code>String</code> values. An instance of this class is sometimes referred to
 * as a "parameter page".
 * <p>
 * The parameters may be defined directly, by providing a <code>String</code> or
 * other <code>Object</code> value, or indirectly, by associating the parameter
 * with a <code>ClipboardProperty</code> instance.
 *
 * @author    (various)
 */
class ParameterPage extends HashMap<String,String> implements StringMap {

    /**
     * Checks if a string is a valid property name.
     * @param aName the string to be checked
     * @return <code>true</code> if the string is a valid property name, otherwise <code>false</code>
     */
    static boolean validName(String aName) {
        return aName != null && aName.matches("[a-zA-Z_][a-zA-Z0-9_]*")
    }

    /**
     * Removes all parameters.
     */
    @Override
    void clear() {
        super.clear()
    }

    /**
     * Checks if a parameter with the given name exists.
     * @param aName the name of the parameter
     * @return <code>true</code> if a parameter with the given name exists, otherwise <code>false</code>
     */
    @Override
    boolean containsKey(Object aName) {
        return super.containsKey(aName)
    }

    /**
     * Establishes a direct parameter.
     * @param aName the name of the parameter
     * @param aValue the value of the parameter
     */
    void define(String aName, String aValue) {
        put(aName, aValue)
    }

    /**
     * Establishes a direct parameter.
     * @param aName the name of the parameter
     * @param aValue the value of the parameter
     */
    void define(String aName, Object aValue) {
        putString(aName, aValue != null ? String.valueOf(aValue) : null)
    }

    /**
     * Establishes an indirect parameter linked to a <code>ClipboardProperty</code>.
     * @param aName the name of the parameter
     * @param aProperty the property to link to
     */
    void define(String aName, ClipboardProperty aProperty) {
        putString(aName, aProperty != null ? aProperty.getStringValue() : null)
    }

    /**
     * Retrieves the <code>String</code> value of a named parameter.
     * @param aName the name of the parameter
     * @return the <code>String</code> value of the parameter, or <code>null</code> if the parameter does not exist
     */
    @Override
    String getString(String aName) {
        Object value = get(aName)
        /* groovylint-disable-next-line Instanceof */
        if (value instanceof ClipboardProperty) {
            return ((ClipboardProperty) value).getStringValue()
        }
        return value as String
    }

    /**
     * Retrieves the <code>Object</code> value of a named parameter.
     * @param aName the name of the parameter
     * @return the <code>Object</code> value of the parameter, or <code>null</code> if the parameter does not exist
     */
    Object getObject(String aName) {
        return get(aName)
    }

    /**
     * Assigns a value to a named parameter.
     * @param aName the name of the parameter
     * @param aValue the value to be assigned
     * @return the previous value of the parameter, or <code>null</code> if it did not have one
     */
    Object putObject(String aName, Object aValue) {
        return putString(aName, aValue != null ? String.valueOf(aValue) : null)
    }

    /**
     * Assigns a <code>String</code> value to a named parameter.
     * @param aName the name of the parameter
     * @param aValue the <code>String</code> value to be assigned
     * @return the previous value of the parameter, or <code>null</code> if it did not have one
     */
    @Override
    String putString(String aName, String aValue) {
        return super.put(aName, aValue)
    }

    /**
     * Removes a named parameter.
     * @param aName the name of the parameter to be removed
     * @return the previous value of the parameter, or <code>null</code> if it did not have one
     */
    @Override
    String remove(Object aName) {
        return super.remove(aName)
    }

}

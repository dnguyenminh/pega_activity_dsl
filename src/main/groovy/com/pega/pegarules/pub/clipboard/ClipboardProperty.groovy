package com.pega.pegarules.pub.clipboard

import java.math.BigDecimal
import java.util.Date
import java.util.Iterator

/**
 * An interface to a PegaRULES clipboard property.
 * <p>
 * A <code>ClipboardProperty</code> has a name, a mode, a type, and a value.
 * The name, mode, and type are established by a Rule-Obj-Property rule.
 * The value may be manipulated using this interface.
 *
 * @author    (various)
 */
interface ClipboardProperty extends Iterable<ClipboardProperty> {

    String COPYRIGHT = "Copyright (c) 2018 Pegasystems Inc."
    String VERSION = "@version \$Id: ClipboardProperty.java 181231 2018-03-22 14:39:59Z SachinMittal \$"

    int TYPE_UNKNOWN = 0
    int TYPE_PAGE = 1
    int TYPE_TEXT = 2
    int TYPE_INTEGER = 3
    int TYPE_DECIMAL = 4
    int TYPE_DOUBLE = 5
    int TYPE_TRUEFALSE = 6
    int TYPE_DATE = 7
    int TYPE_TIME = 8
    int TYPE_DATETIME = 9
    int TYPE_ID = 10
    int TYPE_PASSWORD = 11

    int MODE_UNKNOWN = 0
    int MODE_SINGLE = 1
    int MODE_LIST = 2
    int MODE_GROUP = 3

    /**
     * Adds a value to this property.
     * @param aValue the value to be added
     */
    void add(Object aValue)

    /**
     * Adds a value to this property at a specified index.
     * @param aIndex the index at which the specified element is to be inserted
     * @param aValue the value to be added
     */
    void add(int aIndex, Object aValue)

    /**
     * Clears the value of this property.
     */
    void clearValue()

    /**
     * Returns <code>true</code> if this property contains the specified value.
     * @param aValue the value whose presence in this property is to be tested
     * @return <code>true</code> if this property contains the specified value
     */
    boolean contains(Object aValue)

    /**
     * Performs backward chaining for this property.
     */
    void doBackwardChain()

    /**
     * Returns the property at the specified position in this property.
     * @param aIndex index of the element to return
     * @return the property at the specified position in this property
     */
    ClipboardProperty get(int aIndex)

    /**
     * Returns the property with the specified name.
     * @param aIndex name of the element to return
     * @return the property with the specified name
     */
    ClipboardProperty get(String aIndex)

    /**
     * Returns the absolute reference to this property.
     * @return the absolute reference to this property
     */
    String getAbsoluteReference()

    /**
     * Returns the <code>BigDecimal</code> value of this property.
     * @return the <code>BigDecimal</code> value of this property
     */
    BigDecimal getBigDecimalValue()

    /**
     * Returns the <code>boolean</code> value of this property.
     * @return the <code>boolean</code> value of this property
     */
    boolean getBooleanValue()

    /**
     * Returns the definition of this property.
     * @return the definition of this property
     */
    Object getDefinition()

    /**
     * Returns the <code>double</code> value of this property.
     * @return the <code>double</code> value of this property
     */
    double getDoubleValue()

    /**
     * Returns a handle to this property for client input.
     * @return a handle to this property for client input
     */
    Object getEntryHandle()

    /**
     * Returns an iterator for the errors on this property.
     * @return an <code>Iterator</code> of <code>String</code> objects
     */
    Iterator getErrors()

    /**
     * Returns the <code>int</code> value of this property.
     * @return the <code>int</code> value of this property
     */
    int getIntegerValue()

    /**
     * Returns the justification for this property.
     * @return the justification for this property
     */
    String getJustification()

    /**
     * Returns the length of this property.
     * @return the length of this property
     */
    int getLength()

    /**
     * Returns an iterator for the messages on this property.
     * @return an <code>Iterator</code> of <code>String</code> objects
     */
    Iterator getMessages()

    /**
     * Returns the mode of this property.
     * @return the mode of this property
     */
    int getMode()

    /**
     * Returns the name of this property.
     * @return the name of this property
     */
    String getName()

    /**
     * Returns the parent of this property.
     * @return the parent of this property
     */
    ClipboardPage getParent()

    /**
     * Returns the page value of this property.
     * @return the page value of this property
     */
    ClipboardPage getPageValue()

    /**
     * Returns the value of this property.
     * @return the value of this property
     */
    Object getPropertyValue()

    /**
     * Returns the reference to this property.
     * @return the reference to this property
     */
    String getReference()

    /**
     * Returns the string value of this property.
     * @return the string value of this property
     */
    String getStringValue()

    /**
     * Returns the type of this property.
     * @return the type of this property
     */
    int getType()

    /**
     * Checks if this property has any messages.
     * @return <code>true</code> if this property has messages, otherwise <code>false</code>
     */
    boolean hasMessages()

    /**
     * Checks if this property has an error.
     * @return <code>true</code> if this property has an error, otherwise <code>false</code>
     */
    boolean isError()

    /**
     * Checks if this property is incompatible.
     * @return <code>true</code> if this property is incompatible, otherwise <code>false</code>
     */
    boolean isIncompatible()

    /**
     * Checks if this property is protected.
     * @return <code>true</code> if this property is protected, otherwise <code>false</code>
     */
    boolean isProtected()

    /**
     * Checks if this property is undefined.
     * @return <code>true</code> if this property is undefined, otherwise <code>false</code>
     */
    boolean isUndefined()

    /**
     * Returns an iterator for this property.
     * @return an <code>Iterator</code> of <code>ClipboardProperty</code> objects
     */
    Iterator<ClipboardProperty> iterator()

    /**
     * Removes the property at the specified position in this property.
     * @param aIndex the index of the element to be removed
     */
    void remove(int aIndex)

    /**
     * Removes the property with the specified name.
     * @param aIndex the name of the element to be removed
     */
    void remove(String aIndex)

    /**
     * Sets the justification for this property.
     * @param aJustification the justification for this property
     */
    void setJustification(String aJustification)

    /**
     * Sets the value of this property.
     * @param aValue the value to be set
     */
    void setValue(Object aValue)

    /**
     * Returns the number of elements in this property.
     * @return the number of elements in this property
     */
    int size()

    /**
     * Returns the <code>boolean</code> value of this property.
     * @return the <code>boolean</code> value of this property
     */
    boolean toBoolean()

    /**
     * Returns the <code>Date</code> value of this property.
     * @return the <code>Date</code> value of this property
     */
    Date toDate()

    /**
     * Returns the <code>double</code> value of this property.
     * @return the <code>double</code> value of this property
     */
    double toDouble()

    /**
     * Returns the <code>int</code> value of this property.
     * @return the <code>int</code> value of this property
     */
    int toInteger()

    /**
     * Returns the string value of this property.
     * @return the string value of this property
     */
    String toString()

}
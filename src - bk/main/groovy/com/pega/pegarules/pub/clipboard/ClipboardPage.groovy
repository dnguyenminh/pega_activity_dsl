package com.pega.pegarules.pub.clipboard

import com.pega.pegarules.pub.util.StringMap

import java.math.BigDecimal
import java.util.Date
import java.util.Collection
import java.util.Iterator

/**
 * Provides a common interface to PegaRULES pages. A <code>ClipboardPage</code> is a collection of
 * <code>ClipboardProperty</code> objects, along with some other descriptive information and messages.
 * Properties on a <code>ClipboardPage</code> may themselves have page values, so pages may be nested.
 * <p>
 * A <code>ClipboardPage</code> may be manipulated using this interface.
 *
 * @author    (various)
 */
interface ClipboardPage extends StringMap {

    String COPYRIGHT = "Copyright (c) 2018 Pegasystems Inc."
    String VERSION = "@version \$Id: ClipboardPage.java 181231 2018-03-22 14:39:59Z SachinMittal \$"

    int REPLACE_ALL = 0
    int NO_REPLACE = 1
    int REPLACE_NULL_ONLY = 2

    int GETXML_INCLUDE_PAGECLASS = 1
    int GETXML_INCLUDE_PAGETYPE = 2
    int GETXML_INCLUDE_NULL = 4
    int GETXML_INCLUDE_EMPTY = 8
    int GETXML_INCLUDE_MESSAGES = 16
    int GETXML_INCLUDE_READONLY = 32
    int GETXML_INCLUDE_TYPE = 64
    int GETXML_INCLUDE_ENCODED = 128
    int GETXML_ALLOW_EMPTY_TAGS = 256
    int GETXML_DONT_ENCODE_CHARS = 512
    int GETXML_IGNORE_INVISIBLE_PROPS = 1024
    int GETXML_INCLUDE_ATTRIBUTES = 2048
    int GETXML_INCLUDE_REFERENCES = 4096
    int GETXML_INCLUDE_REFERENCED_PAGES = 8192
    int GETXML_REMOVE_NAMESPACE = 16384

    String GETXML_NODE_NAME_PAGE_LIST = "pxPageList"
    String GETXML_NODE_NAME_PAGE_GROUP = "pxPageGroup"
    String GETXML_NODE_NAME_VALUE_LIST = "pxValueList"
    String GETXML_NODE_NAME_VALUE_GROUP = "pxValueGroup"
    String GETXML_NODE_NAME_PAGE = "pxPage"
    String GETXML_NODE_NAME_ROW = "pxResults"
    String GETXML_NODE_NAME_CONTENT = "content"
    String GETXML_USE_ELEMENT_FOR_TEXT = "pyUseElementForText"
    String GETXML_TEXT_PROPERTY_NAME = "pyXMLTextPropertyName"
    String GETXML_TEXT_PROPERTY_VALUE = "pyXMLTextPropertyValue"
    String GETXML_ATTRIBUTE_PROPERTY_NAME = "pyXMLAttributePropertyName"
    String GETXML_ATTRIBUTE_PROPERTY_VALUE = "pyXMLAttributeValue"
    String GETXML_ATTRIBUTE_NAMESPACE_PREFIX = "pyXMLNamespacePrefix"
    String GETXML_ATTRIBUTE_NAMESPACE_URI = "pyXMLNamespaceURI"

    String GETXML_FORMAT_XML = "xml"
    String GETXML_FORMAT_JSON = "json"
    String GETXML_FORMAT_HTML = "html"

    /**
     * Adds a message to this page.
     * @param aMessage the message to be added
     */
    void addMessage(String aMessage)

    /**
     * Adds a message to this page, associated with a property.
     * @param aMessage the message to be added
     * @param aProperty the property to associate the message with
     */
    void addMessage(String aMessage, String aProperty)

    /**
     * Adds a message to this page, associated with a property and with a given severity.
     * @param aMessage the message to be added
     * @param aProperty the property to associate the message with
     * @param aSeverity the severity of the message
     */
    void addMessage(String aMessage, String aProperty, int aSeverity)

    /**
     * Removes all messages from this page.
     */
    void clearMessages()

    /**
     * Returns an iterator for all messages on this page.
     * @return an <code>Iterator</code> of <code>String</code> objects
     */
    Iterator getMessagesAll()

    /**
     * Checks if this page has any messages.
     * @return <code>true</code> if this page has messages, otherwise <code>false</code>
     */
    boolean hasMessages()

    /**
     * Returns the property with the specified name (Map API).
     * @param aPropertyName a <code>String</code> value
     * @return the <code>String</code> value
     */
    String get(Object aPropertyName)

    /**
     * Returns the property with the specified name.
     * @param aReference a <code>String</code> value
     * @return a <code>ClipboardProperty</code> value
     */
    ClipboardProperty getProperty(String aReference)

    /**
     * Returns the string value of a property.
     * @param aReference a <code>String</code> value
     * @return a <code>String</code> value
     */
    String getString(String aReference)

    /**
     * Sets the value of a property (Map API).
     * @param aPropertyName a <code>String</code> value
     * @param aValue a <code>String</code> value
     * @return the previous value of the property, or <code>null</code> if it did not have one
     */
    String put(String aPropertyName, String aValue)

    /**
     * Sets the string value of a property.
     * @param aPropertyName a <code>String</code> value
     * @param aValue a <code>String</code> value
     */
    String putString(String aPropertyName, String aValue)

    /**
     * Clears the value of a property.
     * @param aReference a <code>String</code> value
     */
    void clearValue(String aReference)

    /**
     * Removes a property from the page.
     * @param aReference a <code>String</code> value
     * @return the removed <code>ClipboardProperty</code>
     */
    ClipboardProperty removeProperty(String aReference)

    /**
     * Remove a property via Map API (returns previous String value).
     * @param aReference an <code>Object</code> value
     * @return the previous <code>String</code> value, or <code>null</code>
     */
    String remove(Object aReference)

    /**
     * Returns the page as a JSON string.
     * @param aEncode <code>true</code> to encode the string, ----------------false</code> otherwise
     * @return a JSON <code>String</code>
     */
    String getJSON(boolean aEncode)

    /**
     * Adopts the content of a JSONObject.
     * @param aJO a <code>JSONObject</code>
     */
    void adoptJSONObject(Object aJO)

    /**
     * Returns the page as an XML string.
     * @param aEncode <code>true</code> to encode the string, <code>false</code> otherwise
     * @return an XML <code>String</code>
     */
    String getXML(boolean aEncode)

    /**
     * Returns the page as an XML string.
     * @param aOptions options for XML generation
     * @return an XML <code>String</code>
     */
    String getXML(int aOptions)

    /**
     * Returns the page as an XML string.
     * @param aPageName the name of the page
     * @param aOptions options for XML generation
     * @return an XML <code>String</code>
     */
    String getXML(String aPageName, int aOptions)

    /**
     * Adopts the content of an XML form.
     * @param aXMLForm an XML <code>String</code>
     * @param aOptions options for XML adoption
     */
    void adoptXMLForm(String aXMLForm, int aOptions)

    /**
     * Creates a copy of this page.
     * @return a <code>ClipboardPage</code>
     */
    ClipboardPage copy()

    /**
     * Copies the properties from this page to another page.
     * @param aDestPage the destination page
     */
    void copyTo(ClipboardPage aDestPage)

    /**
     * Copies the properties from another page to this page.
     * @param aSourcePage the source page
     */
    void copyFrom(ClipboardPage aSourcePage)

    /**
     * Renames this page.
     * @param aNewName the new name of the page
     */
    void rename(String aNewName)

    /**
     * Replaces this page with another page.
     * @param aSourcePage the source page
     */
    void replace(ClipboardPage aSourcePage)

    /**
     * Checks if this page is an embedded page.
     * @return <code>true</code> if this page is embedded, <code>false</code> otherwise
     */
    boolean isEmbedded()

    /**
     * Checks if this page is empty.
     * @return <code>true</code> if this page is empty, <code>false</code> otherwise
     */
    boolean isEmpty()

    /**
     * Checks if this page is valid.
     * @return <code>true</code> if this page is valid, <code>false</code> otherwise
     */
    boolean isValid()

    /**
     * Sets the value of a property.
     * @param aReference a <code>String</code> value
     * @param aValue an <code>Object</code> value
     */
    void setValue(String aReference, Object aValue)

    /**
     * Returns the BigDecimal value of a property.
     * @param aReference a <code>String</code> value
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getBigDecimal(String aReference)

    /**
     * Returns the boolean value of a property.
     * @param aReference a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    boolean getBoolean(String aReference)

    /**
     * Returns the Date value of a property.
     * @param aReference a <code>String</code> value
     * @return a <code>Date</code> value
     */
    Date getDate(String aReference)

    /**
     * Puts a property onto the clipboard.
     * @param aProperty the property to be put
     */
    void putProperty(ClipboardProperty aProperty)

    /**
     * Returns a handle to the referenced property for client input.
     * @param aPropertyReference a <code>String</code> value
     * @return an <code>Object</code> value
     */
    Object getEntryHandle(String aPropertyReference)

    /**
     * Returns the class name of the page.
     * @return the class name of the page
     */
    String getClassName()

    /**
     * Returns the name of the page.
     * @return the name of the page
     */
    String getName()

    /**
     * Indicates whether the page is read-only.
     * @return <code>true</code> if the page is read-only, <code>false</code> otherwise
     */
    boolean isReadOnly()

    /**
     * Returns a collection of messages on the page.
     * @return a <code>Collection</code> of <code>String</code> objects
     */
    Collection<String> getMessages()

    /**
     * Checks if the page's class extends <code>Embed-Java-</code>.
     * @return <code>true</code> if the page's class extends <code>Embed-Java-</code>, <code>false</code> otherwise
     */
    boolean isJavaPage()

    /**
     * Copies this page and all its properties to a new page.
     * @param aPage the destination page
     * @return the new page
     */
    ClipboardPage copy(ClipboardPage aPage)

    /**
     * Removes the page from the clipboard.
     */
    void removeFromClipboard()
}

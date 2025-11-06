package vn.com.fecredit.pega.activity

import com.pega.pegarules.pub.clipboard.ClipboardPage

/**
 * Clipboard interface - simplified Pega-like API skeleton.
 *
 * This interface provides common operations for accessing and manipulating
 * pages and properties on a clipboard-like structure. Method signatures and
 * Javadoc-style comments are provided to match the public API surface expected
 * by code that interacts with Pega clipboard objects.
 */
public interface Clipboard {

    /**
     * Return the root page of the clipboard.
     * @return root {@link ClipboardPage}
     */
    ClipboardPage getRootPage()

    /**
     * Retrieve a page at the given path (dot-separated).
     * Example: "Account" or "Primary.Orders(1)".
     * @param path path to page
     * @return {@link ClipboardPage} or null if not found
     */
    ClipboardPage getPage(String path)

    /**
     * Create and return a new page at the given path.
     * If a page already exists at the path, behavior is implementation-specific.
     * @param path path for the new page
     * @return newly created {@link ClipboardPage}
     */
    ClipboardPage newPage(String path)

    /**
     * Put (replace) a page at the given path.
     * @param path destination path
     * @param page page to store
     */
    void putPage(String path, ClipboardPage page)

    /**
     * Remove a page at the given path.
     * @param path path of the page to remove
     * @return the removed {@link ClipboardPage} or null if none existed
     */
    ClipboardPage removePage(String path)

    /**
     * Return whether a page exists at the given path.
     * @param path page path
     * @return true if present
     */
    boolean containsPage(String path)

    /**
     * Get a property value at a path. Path may address a page property, e.g.
     * "Account.Balance".
     * @param path property path
     * @return value or null
     */
    Object getProperty(String path)

    /**
     * Set or create a property value at a path.
     * @param path property path
     * @param value value to set
     */
    void putProperty(String path, Object value)

    /**
     * Remove a property at path.
     * @param path property path
     * @return removed value or null
     */
    Object removeProperty(String path)

    /**
     * Return a list of top-level pages or all pages (implementation-specific).
     * @return list of pages
     */
    List<ClipboardPage> getPages()

    /**
     * Obtain a map-like representation of the clipboard.
     * @return map view
     */
    Map<String, Object> asMap()

}
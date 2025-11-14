package org.example.simulator

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class BaseClassDefaultsTest {
    @Test
    void defaults_are_applied_to_new_map_pages() {
        def cb = new Clipboard()
        // set class-level baseclass defaults on ClipboardPage
    def oldDefaults = com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults
    def oldDefaultsBackup = new HashMap(oldDefaults)
    try {
            com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults.clear()
            com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults.putAll([pxCreateDate: '1970-01-01', pyLabel: 'DefaultLabel'])

            // create a new page with empty map
            try {
                cb.set('Customer', [:])
                def c = cb.get('Customer')
                println "DEBUG base defaults page class: ${c?.getClass()?.name}, pyLabel: ${c?.getAt('pyLabel')?.getClass()?.name}, pyLabelValue=${c?.getAt('pyLabel')}, pxCreateDateValue=${c?.getAt('pxCreateDate')}"
                assertNotNull(c)
                assertEquals('DefaultLabel', c.getProperty('pyLabel').getPropertyValue())
                assertEquals('1970-01-01', c.getProperty('pxCreateDate').getPropertyValue())
            } catch(Exception e) {
                println 'DEBUG exception during base defaults test:'
                e.printStackTrace()
                throw e
            }

            // create a new page with some properties - provided properties override defaults
            cb.set('Order', [amount:100, pyLabel:'Overridden'])
            def o = cb.get('Order')
            assertEquals(100, o.getProperty('amount').getPropertyValue())
            assertEquals('Overridden', o.getProperty('pyLabel').getPropertyValue())
            // defaults still present for missing keys
            assertEquals('1970-01-01', o.getProperty('pxCreateDate').getPropertyValue())
        } finally {
            com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults.clear()
            com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults.putAll(oldDefaultsBackup)
        }
    }

    @Test
    void page_copy_applies_defaults_to_copied_map_pages() {
        def cb = new Clipboard()
    def oldDefaults2 = com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults
    def oldDefaults2Backup = new HashMap(oldDefaults2)
    try {
            com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults.clear()
            com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults.putAll([pyLabel:'BaseLabel'])
            cb.set('Source', [a:1])
    // use the runner's Page-Copy behavior via direct invocation
    SimulatorRunner.methodRegistry['Page-Copy'].call(cb, [source:'Source', target:'Dest'], [:])
            def d = cb.get('Dest')
            assertNotNull(d)
            assertEquals(1, d.getProperty('a').getPropertyValue())
            assertEquals('BaseLabel', d.getProperty('pyLabel').getPropertyValue())
        } finally {
            com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults.clear()
            com.pega.pegarules.pub.clipboard.ClipboardPage.baseClassDefaults.putAll(oldDefaults2Backup)
        }
    }
}

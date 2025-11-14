package com.pega.pegarules.pub.runtime

import com.pega.pegarules.pub.clipboard.ClipboardProperty

interface EditInput extends GeneratedJava {
    String VERSION = "8.4.0"
    void assign(ClipboardProperty theProperty, String theValue)
}

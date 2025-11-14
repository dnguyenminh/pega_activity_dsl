package com.pega.pegarules.pub.runtime

import com.pega.pegarules.pub.clipboard.ClipboardProperty

interface EditValidate extends GeneratedJava {
    String VERSION = "8.4.0"
    boolean evaluate(ClipboardProperty theProperty)
}

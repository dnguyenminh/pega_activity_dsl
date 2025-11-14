package com.pega.pegarules.pub.runtime

import com.pega.pegarules.pub.clipboard.ClipboardPage

interface ControlBuilder extends StreamBuilder {
    String VERSION = "8.4.0"
    void execute(ClipboardPage cellPage)
}

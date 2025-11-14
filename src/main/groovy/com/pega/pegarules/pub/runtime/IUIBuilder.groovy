package com.pega.pegarules.pub.runtime

interface IUIBuilder {
    String VERSION = "8.4.0"
    void begin(IUIComponentMetadata metadata)
    void end(IUIComponentMetadata metadata)
}

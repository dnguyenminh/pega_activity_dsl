package com.pega.pegarules.pub.runtime

interface WhenBlock extends GeneratedJava {
    String VERSION = "8.4.0"

    boolean evaluate() throws IndeterminateConditionalException
}

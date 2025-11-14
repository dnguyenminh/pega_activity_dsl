package com.pega.pegarules.pub.runtime

interface Function {
    String VERSION = "8.4.0"
    Object invoke(Object[] aArgs)
    String[] pzGetMetaData()
}

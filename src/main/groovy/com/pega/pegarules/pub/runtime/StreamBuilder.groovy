package com.pega.pegarules.pub.runtime

interface StreamBuilder extends GeneratedJava {
    String VERSION = "8.4.0"

    String sInputEnabled = "true"
    String sNoInput = "false"

    byte FMT_LITERAL = 0
    byte FMT_NORMAL = 1
    byte FMT_STREAM = 2
    byte FMT_BLOCK = 3
    byte FMT_TEXT = 4
    byte FMT_JAVASCRIPT = 5
    byte FMT_RICHTEXT = 6

    long MODE_INPUT_ENABLED = 1L
    long MODE_NO_INPUT = 2L
    long MODE_PREVIEW = 4L
    long MODE_LIVE_DESIGN_VIEW = 8L
    long MODE_STREAMING_PERMITTED = 16L
    long MODE_STREAMING_DISABLED = 32L

    void execute()
    boolean isDirectiveStream()
}

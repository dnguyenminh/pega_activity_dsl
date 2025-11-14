package com.pega.pegarules.pub.runtime

import com.pega.pegarules.priv.PegaAPI

interface FUAReusable {
    String VERSION = "8.4.0"
    void cleanForReuse(PegaAPI aContext)
}

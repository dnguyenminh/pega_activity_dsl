package com.pega.pegarules.pub.runtime

interface PegaURLBuilderOld {
    String VERSION = "8.4.0"

    String build()
    PegaURLBuilderOld withDefaultServlet()
    PegaURLBuilderOld withPublicLinkURL()
    PegaURLBuilderOld withQueryParams(String queryParams)
    PegaURLBuilderOld withServlet(String aServlet)
    PegaURLBuilderOld withUniqueSession()
}

package com.pega.dsl

import com.pega.dsl.PegaDslBuilders

class PegaDslConnectors {

    def connector(String name, Map params) {
        def d = PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get()
        if (d instanceof DataPage) {
            return d.connector(name, params)
        }
        return PegaDslBuilders.restConnector(name, { })
    }

    def connector(String name, String activity, Closure closure = null) {
        def d = PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get()
        if (d instanceof Flow) {
            return ((Flow) d).connector(name, activity, closure)
        }
        if (d instanceof DataPage) {
            return d.connector(name, [connector: activity])
        }
        return PegaDslBuilders.restConnector(name, closure ?: { })
    }

    def correspondence(String name, Closure closure) {
        def corr = new Correspondence(name: name)
        closure.delegate = corr
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        return corr
    }
}

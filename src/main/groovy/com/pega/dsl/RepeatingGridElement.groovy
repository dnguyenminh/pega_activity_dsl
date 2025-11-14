package com.pega.dsl

class RepeatingGridElement extends UIElement {
    String pageList
    List<GridColumn> columns = []

    RepeatingGridElement() {
        this.type = 'Repeating Grid'
    }

    def column(String property, String label = '', Closure closure = null) {
        def column = new GridColumn(property: property, label: label)
        if (closure) {
            // Instrument CURRENT_DELEGATE when entering a column closure and
            // ensure static forwarders inside the column closure route to the
            // enclosing RepeatingGridElement as the logical delegate.
            def _prevDelegate = PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.get()
            if (_prevDelegate == null) {
                def ownerDelegate = PegaDeveloperUtilitiesDsl.findOwnerDelegateOfType(closure, RepeatingGridElement)
                if (ownerDelegate == null) ownerDelegate = PegaDeveloperUtilitiesDsl.findOwnerDelegateOfType(closure, Section)
                if (ownerDelegate != null) {
                    PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(ownerDelegate)
                } else {
                    PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(this)
                }
            } else {
                PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(this)
            }
            try {
                closure.delegate = column
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call()
            } finally {
                if (_prevDelegate != null) {
                    PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.set(_prevDelegate)
                } else {
                    PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE.remove()
                }
            }
        }
        columns.add(column)
        return column
    }
}

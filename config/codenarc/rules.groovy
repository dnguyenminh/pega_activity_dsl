ruleset {
    // Use MethodSize rule to enforce max method length of 20 lines
    MethodSize {
        maxLines = 20
    }

    // keep the default basic ruleset for other checks
    ruleset('rulesets/basic.xml')
}

package com.pega.dsl

class PegaDeveloperUtilitiesDsl {
    // Force load ScriptExtensions which installs global helper methods onto Script.metaClass.
    // This makes top-level helpers like application(...) and flow(...) available to tests.
    static final __init = com.pega.dsl.ScriptExtensions.class
    // expose the shared CURRENT_DELEGATE and helpers so existing code that
    // referenced PegaDeveloperUtilitiesDsl.CURRENT_DELEGATE or helper methods
    // keeps working while the canonical implementation lives in PegaDslCore.
    static final ThreadLocal<Object> CURRENT_DELEGATE = PegaDslCore.CURRENT_DELEGATE
    static def findOwnerDelegateOfType(Closure closure, Class type) { PegaDslCore.findOwnerDelegateOfType(closure, type) }
    static def normalizeCandidate(String raw) { PegaDslCore.normalizeCandidate(raw) }

    static PegaRuleBuilder pega() {
        return new PegaRuleBuilder()
    }

    static def methodMissing(String name, Object[] args) {
        def delegate = CURRENT_DELEGATE.get()
        if (delegate != null) {
            try {
                return delegate.invokeMethod(name, args)
            } catch (MissingMethodException e) {
                // Fall through and re-throw the original exception
            }
        }
        throw new MissingMethodException(name, PegaDeveloperUtilitiesDsl.class, args)
    }

    // Convenience static DSL entry points that delegate to a PegaRuleBuilder instance.
    // Tests use static import of this class and call these functions directly.
    static def activity(String name, Closure closure) {
        return pega().activity(name, closure)
    }

    static def dataTransform(String name, Closure closure) {
        return pega().dataTransform(name, closure)
    }

    static def decisionTable(String name, Closure closure) {
        return pega().decisionTable(name, closure)
    }

    static def decisionTree(String name, Closure closure) {
        return pega().decisionTree(name, closure)
    }

    static def dataPage(String name, Closure closure) {
        return pega().dataPage(name, closure)
    }

    static def property(String name, Closure closure) {
        return pega().property(name, closure)
    }

    static def section(String name, Closure closure) {
        return pega().section(name, closure)
    }

    static def harness(String name, Closure closure) {
        return pega().harness(name, closure)
    }

    static def flow(String name, Closure closure) {
        return pega().flow(name, closure)
    }

    static def correspondence(String name, Closure closure) {
        return pega().correspondence(name, closure)
    }

    static def restConnector(String name, Closure closure) {
        return pega().restConnector(name, closure)
    }

    static def soapConnector(String name, Closure closure) {
        return pega().soapConnector(name, closure)
    }

    static def restService(String name, Closure closure) {
        return pega().restService(name, closure)
    }

    static def testCase(String name, Closure closure) {
        return pega().testCase(name, closure)
    }

    static def accessGroup(String name, Closure closure) {
        return pega().accessGroup(name, closure)
    }

    static def accessRole(String name, Closure closure) {
        return pega().accessRole(name, closure)
    }

    static def database(String name, Closure closure) {
        return pega().database(name, closure)
    }

    static def authenticationProfile(String name, Closure closure) {
        return pega().authenticationProfile(name, closure)
    }

    static def application(String name, Closure closure) {
        return pega().application(name, closure)
    }

    static def ruleset(String name, Closure closure) {
        return pega().ruleset(name, closure)
    }

    static def when(String name, Closure closure) {
        return pega().when(name, closure)
    }
}

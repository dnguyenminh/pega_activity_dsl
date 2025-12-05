package com.pega.pegarules.pub

/**
 * Stub generated from Pega 8.4 javadocs.
 */
class PRAppRuntimeException extends RuntimeException {
    double reason
    String ruleSetName

    PRAppRuntimeException(String aRulesetName, double aReason, String aMessage) {
        super((String)aMessage)
        this.ruleSetName = aRulesetName
        this.reason = aReason
    }

    PRAppRuntimeException(String aRulesetName, double aReason, String aMessage, Throwable aCause) {
        super(aMessage, aCause)
        this.ruleSetName = aRulesetName
        this.reason = aReason
    }

    double getReason() { return reason }
    String getRuleSetName() { return ruleSetName }
    String toString() { return "${this.class.name}: ${message} (ruleset=${ruleSetName}, reason=${reason})" }
}
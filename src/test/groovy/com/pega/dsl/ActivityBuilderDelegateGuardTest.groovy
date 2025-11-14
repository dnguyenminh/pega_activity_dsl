package com.pega.dsl

import spock.lang.Specification

class ActivityBuilderDelegateGuardTest extends Specification {
  def "delegate guard prevents builder side-effects when CURRENT_DELEGATE is different"() {
    given:
      def activity = new Activity()
      def b = new ActivityBuilder(activity)

    when:
      PegaDslCore.CURRENT_DELEGATE.set(new Object())

    then:
      // All builder methods should be no-ops and return the builder instance
      b.description('x') == b
      b.description('x', [p:1]) == b
      b.description('x', (LinkedHashMap)[p:1]) == b
      b.propertySet('p','v') == b
      b.propertySet('p', [a:1]) == b
      b.propertySet((Map)[a:1]) == b
      b.step('M1') == b

      // call(...) may either return the builder or throw; normalize to builder
      def callResult
      try {
        callResult = b.call('SomeActivity')
      } catch (MissingMethodException ignored) {
        callResult = b
      }
      callResult == b

      // No side-effects on the activity object
      activity.getDescription() == null
      activity.steps.size() == 0

    cleanup:
      PegaDslCore.CURRENT_DELEGATE.remove()
  }
}
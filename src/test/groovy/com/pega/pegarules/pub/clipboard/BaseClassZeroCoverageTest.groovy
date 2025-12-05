package com.pega.pegarules.pub.clipboard

import spock.lang.Specification
import java.time.Instant
import java.time.LocalDate

class BaseClassZeroCoverageTest extends Specification {

  def "test BaseClass constructor with empty map for coverage"() {
    when:
    def baseClass = new BaseClass([:])
    
    then:
    notThrown(Exception)
  }

  def "test BaseClass constructor with map containing properties for coverage"() {
    when:
    def baseClass = new BaseClass([name: "test", value: 123])
    
    then:
    notThrown(Exception)
  }

  def "test BaseClass constructor with empty list for coverage"() {
    when:
    def baseClass = new BaseClass([])
    
    then:
    notThrown(Exception)
  }

  def "test BaseClass constructor with list containing maps for coverage"() {
    when:
    def baseClass = new BaseClass([
      [name: "test1", value: 100],
      [name: "test2", value: 200]
    ])
    
    then:
    notThrown(Exception)
  }

  def "test BaseClass constructor with list containing properties for coverage"() {
    when:
    def baseClass = new BaseClass([
      new SimpleClipboardProperty("prop1", "value1"),
      new SimpleClipboardProperty("prop2", "value2")
    ])
    
    then:
    notThrown(Exception)
  }

  def "test BaseClass constructor with list containing pages for coverage"() {
    given:
    def page1 = new SimpleClipboardPage([key1: "value1"])
    def page2 = new SimpleClipboardPage([key2: "value2"])
    
    when:
    def baseClass = new BaseClass([page1, page2])
    
    then:
    notThrown(Exception)
  }

  def "test BaseClass constructor with list containing raw values for coverage"() {
    when:
    def baseClass = new BaseClass(["string", 123, new Date()])
    
    then:
    notThrown(Exception)
  }

  def "BaseClass populates timestamp and date defaults"() {
    when:
    def baseClass = new BaseClass([pxCreateDateTime: null, pxCreateDate: null])

    then:
    def timestamp = baseClass.getString("pxCreateDateTime")
    def dateOnly = baseClass.getString("pxCreateDate")
    timestamp
    dateOnly
    Instant.parse(timestamp)
    LocalDate.parse(dateOnly)
  }

  def "BaseClass preserves explicit operator and normalizes blank py fields"() {
    when:
    def baseClass = new BaseClass([pxCreateOperator: "OperatorA", pyLabel: "   ", pyDescription: "kept"])

    then:
    baseClass.getString("pxCreateOperator") == "OperatorA"
    baseClass.getString("pyDescription") == "kept"
    baseClass.getString("pyLabel") == ""
  }

  def "BaseClass copyStandardProps returns defensive copy"() {
    when:
    def copy = BaseClass.copyStandardProps()
    copy.pxObjClass = "changed"

    then:
    BaseClass.STANDARD_BASECLASS_PROPS.pxObjClass == "@baseclass"
  }

  def "applyTo populates missing defaults on plain Page"() {
    given:
    def page = new Page()
    page.put("pxObjClass", "custom")

    when:
    BaseClass.applyTo(page)

    then:
    page.getString("pxObjClass") == "@baseclass"
    page.getString("pyLabel") == ""
    page.getString("pxFlowCount") == "0"
  }

  def "applyTo gracefully ignores null page"() {
    when:
    BaseClass.applyTo(null)

    then:
    notThrown(Exception)
  }

  def "ensureBasePropsPresent converts null py defaults to empty string"() {
    given:
    def original = BaseClass.STANDARD_BASECLASS_PROPS.pyLabel
    BaseClass.STANDARD_BASECLASS_PROPS.pyLabel = null

    when:
    def baseClass = new BaseClass([:])

    then:
    baseClass.getString("pyLabel") == ""

    cleanup:
    BaseClass.STANDARD_BASECLASS_PROPS.pyLabel = original
  }

  def "applyTo reuses ClipboardProperty entries without wrapping"() {
    given:
    def original = BaseClass.STANDARD_BASECLASS_PROPS.pxFlow
    def sentinel = new SimpleClipboardProperty("pxFlow", "sentinel")
    BaseClass.STANDARD_BASECLASS_PROPS.pxFlow = sentinel
    def page = new Page()

    when:
    BaseClass.applyTo(page)

    then:
    page.getProperty("pxFlow").getStringValue() == "sentinel"

    cleanup:
    BaseClass.STANDARD_BASECLASS_PROPS.pxFlow = original
  }
}

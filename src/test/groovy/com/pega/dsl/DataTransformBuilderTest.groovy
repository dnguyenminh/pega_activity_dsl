package com.pega.dsl

import spock.lang.Specification

class DataTransformBuilderTest extends Specification {

    def "methodMissing throws MissingMethodException for non-existent method calls"() {
        given:
        def dataTransform = new DataTransform()
        def builder = new DataTransformBuilder(dataTransform)

        when:
        builder.nonExistentMethod()

        then:
        thrown(MissingMethodException)
    }

    def "description method sets the data transform description"() {
        given:
        def dataTransform = new DataTransform()
        def builder = new DataTransformBuilder(dataTransform)
        def testDescription = "This is a test description"

        when:
        def result = builder.description(testDescription)

        then:
        result == builder
        dataTransform.description == testDescription
    }
}
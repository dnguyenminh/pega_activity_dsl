package com.pega.dsl

import spock.lang.Specification

class SectionBuilderMappingSpec extends Specification {

    def "repeatingGridFor explicit API adds a RepeatingGridElement"() {
        given:
        def section = new Section()
        def builder = new SectionBuilder(section)

    when:
    def grid = builder.repeatingGridFor("MyPageList", { pageList = '.explicit' })

    then:
    grid instanceof RepeatingGridElement
    section.elements.size() == 1
    section.elements[0] == grid
    grid.pageList == '.explicit'
    }

    def "mapping logic via public API behaves like methodMissing mapping"() {
        given:
        def section = new Section()
        def builder = new SectionBuilder(section)

    when:
    // Use the repeatingGrid delegate to add a column and validate the delegate was applied
    def grid = builder.repeatingGridFor('customList', { column('id', 'ID') })

    then:
    grid instanceof RepeatingGridElement
    section.elements.contains(grid)
    grid.columns.size() == 1
    grid.columns[0].property == 'id'
    }

    def "detailslist special-case preserved via public API"() {
        given:
        def section = new Section()
        def builder = new SectionBuilder(section)

        when:
        def grid = builder.repeatingGridFor('DetailsList', { })

        then:
        grid instanceof RepeatingGridElement
        grid.pageList == '.DetailsList'
        section.elements.contains(grid)
    }
}

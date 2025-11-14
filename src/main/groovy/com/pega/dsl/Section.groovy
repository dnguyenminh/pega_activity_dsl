package com.pega.dsl

class Section extends Rule {
    List<UIElement> elements = []
    String layoutType = 'Dynamic'
    String descriptionText = ''

    Section() { this.type = 'Section' }
}

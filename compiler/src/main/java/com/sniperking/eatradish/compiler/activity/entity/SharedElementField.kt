package com.sniperking.eatradish.compiler.activity.entity

import com.sniperking.eatradish.annotations.SharedElement
import com.sun.tools.javac.code.Symbol

class SharedElementField(symbol: Symbol.VarSymbol) : Field(symbol) {

    var elementName: String? = null
        private set

    var elementTargetResId: Int = 0
        private set

    var elementPriority: Int = 1
        private set

    override val prefix: String = "SHARED_ELEMENT_"

    init {
        var sharedElement = symbol.getAnnotation(SharedElement::class.java)
        elementName = sharedElement.name
        elementTargetResId = sharedElement.resId
        elementPriority = sharedElement.priority
    }

    override fun compareTo(other: Field): Int {
        return if (other is SharedElementField) {
            super.compareTo(other)
        } else {
            1
        }
    }
}
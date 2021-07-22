package com.sniperking.eatradish.compiler.activity.entity

import com.sniperking.eatradish.annotations.SharedElement
import com.sun.tools.javac.code.Symbol

class SharedElementField(symbol: Symbol.VarSymbol) : Field(symbol) {

    var elementName: String? = null
        private set

    var elementTargetResId: Int = 0
        private set

    var runEnterAnimDuration: Long
        private set
    var runExitAnimDuration: Long
        private set

    var runEnterAnimTimeInterpolatorType: Int
        private set
    var runExitAnimTimeInterpolatorType: Int
        private set

    var runEnterPriority: Int
        private set
    var runExitPriority: Int
        private set

    override val prefix: String = "SHARED_ELEMENT_"

    init {
        var sharedElement = symbol.getAnnotation(SharedElement::class.java)
        elementName = sharedElement.name
        elementTargetResId = sharedElement.resId
        runEnterAnimDuration = sharedElement.runEnterAnimDuration
        runExitAnimDuration = sharedElement.runExitAnimDuration
        runEnterAnimTimeInterpolatorType = sharedElement.runEnterTimeInterpolatorType
        runExitAnimTimeInterpolatorType = sharedElement.runExitTimeInterpolatorType
        runEnterPriority = sharedElement.runEnterPriority
        runExitPriority = sharedElement.runExitPriority
    }

    override fun compareTo(other: Field): Int {
        return if (other is SharedElementField) {
            super.compareTo(other)
        } else {
            1
        }
    }
}
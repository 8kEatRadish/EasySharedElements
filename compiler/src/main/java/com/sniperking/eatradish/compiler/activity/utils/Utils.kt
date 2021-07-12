package com.sniperking.eatradish.compiler.activity.utils

import java.lang.StringBuilder

fun String.camelToUnderLine(): String {
    return fold(StringBuilder()) { acc, c ->
        if (c.isUpperCase()) {
            acc.append("_").append(c.toLowerCase())
        } else acc.append(c)
    }.toString()
}

fun String.underLineToCamel(): String {
    var upperNext = false
    return fold(StringBuilder()) { acc, c ->
        if (c == '_') upperNext = true
        else acc.append(if (upperNext) c.toUpperCase() else c)
        acc
    }.toString()
}
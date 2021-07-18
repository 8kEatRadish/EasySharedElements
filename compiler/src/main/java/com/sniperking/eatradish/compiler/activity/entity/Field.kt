package com.sniperking.eatradish.compiler.activity.entity

import com.bennyhuo.aptutils.types.asJavaTypeName
import com.bennyhuo.aptutils.types.asKotlinTypeName
import com.squareup.javapoet.TypeName
import com.sun.tools.javac.code.Symbol

open class Field(private val symbol: Symbol.VarSymbol) : Comparable<Field> {

    val name = symbol.qualifiedName.toString()

    open val prefix = "REQUIRED_"

    val isPrivate = symbol.isPrivate

    val isPrimitive = symbol.type.isPrimitive

    override fun toString(): String {
        return "$name:${symbol.type}"
    }

    fun asJavaTypeName(): TypeName = symbol.type.asJavaTypeName().box()

    open fun asKotlinTypeName() = symbol.type.asKotlinTypeName()

    override fun compareTo(other: Field): Int {
        return name.compareTo(other.name)
    }
}
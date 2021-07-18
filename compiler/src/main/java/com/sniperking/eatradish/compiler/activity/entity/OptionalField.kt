package com.sniperking.eatradish.compiler.activity.entity

import com.bennyhuo.aptutils.types.asTypeMirror
import com.sniperking.eatradish.annotations.Optional
import com.squareup.kotlinpoet.TypeName
import com.sun.tools.javac.code.Symbol
import javax.lang.model.type.TypeKind

class OptionalField(symbol: Symbol.VarSymbol) : Field(symbol) {

    var defaultValue: Any? = null
        private set

    override val prefix: String = "OPTIONAL_"

    override fun asKotlinTypeName() = super.asKotlinTypeName().copy(nullable = true)

    init {
        val optional = symbol.getAnnotation(Optional::class.java)
        when (symbol.type.kind) {
            TypeKind.BOOLEAN -> defaultValue = optional.booleanValue
            TypeKind.BYTE, TypeKind.SHORT, TypeKind.INT, TypeKind.LONG, TypeKind.CHAR -> defaultValue =
                optional.intValue
            TypeKind.FLOAT, TypeKind.DOUBLE -> defaultValue = optional.floatValue

            else -> if (symbol.type == String::class.java.asTypeMirror()){
                defaultValue = """"${optional.stringValue}""""
            }

        }
    }

    override fun compareTo(other: Field): Int {
        return if (other is OptionalField){
            super.compareTo(other);
        }else{
            1
        }
    }
}
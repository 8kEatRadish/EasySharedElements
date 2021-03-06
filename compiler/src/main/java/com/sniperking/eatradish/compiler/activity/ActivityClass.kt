package com.sniperking.eatradish.compiler.activity

import com.bennyhuo.aptutils.types.packageName
import com.bennyhuo.aptutils.types.simpleName
import com.sniperking.eatradish.compiler.activity.entity.Field
import java.lang.reflect.Method
import java.util.*
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

class ActivityClass(val typeElement: TypeElement) {
    val simpleName: String = typeElement.simpleName()

    val packageName: String = typeElement.packageName()

    val fields = TreeSet<Field>()

    val isAbstract = typeElement.modifiers.contains(Modifier.ABSTRACT)

    val builder = ActivityClassBuilder(this)

    val isKotlin = typeElement.getAnnotation(Metadata::class.java) != null

    var startMethodName: String = ""

    var endMethodName: String = ""

    override fun toString(): String {
        return "$packageName.$simpleName[${fields.joinToString()}]"
    }
}
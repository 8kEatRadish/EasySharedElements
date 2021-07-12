package com.sniperking.eatradish.compiler.activity.method

import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.utils.camelToUnderLine
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

class ConstantBuilder(private val activityClass: ActivityClass) {


    fun build(typeBuilder: TypeSpec.Builder) {
        activityClass.fields.forEach { field ->
            typeBuilder.addField(
                FieldSpec.builder(
                    String::class.java, field.prefix + field.name.camelToUnderLine().toUpperCase(),
                    Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL
                ).initializer("\$S", field.name).build()
            )
        }
    }
}
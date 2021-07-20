package com.sniperking.eatradish.compiler.activity.method

import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.prebuilt.VIEW_ATTRS
import com.sniperking.eatradish.compiler.activity.utils.camelToUnderLine
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeSpec
import java.lang.reflect.Method
import javax.lang.model.element.Modifier

class ConstantBuilder(private val activityClass: ActivityClass) {


    fun build(typeBuilder: TypeSpec.Builder) {

        typeBuilder.addField(
            FieldSpec.builder(
                List::class.java, "VIEW_ATTRS", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL
            ).initializer("new \$T<\$T>()", ArrayList::class.java, VIEW_ATTRS.java).build()
        )

        typeBuilder.addField(
            FieldSpec.builder(
                String::class.java,
                "START_METHOD",
                Modifier.STATIC,
                Modifier.PRIVATE,
                Modifier.FINAL
            ).initializer("\$S", activityClass.startMethodName).build()
        )
        typeBuilder.addField(
            FieldSpec.builder(
                String::class.java,
                "END_METHOD",
                Modifier.STATIC,
                Modifier.PRIVATE,
                Modifier.FINAL
            ).initializer("\$S", activityClass.endMethodName).build()
        )

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
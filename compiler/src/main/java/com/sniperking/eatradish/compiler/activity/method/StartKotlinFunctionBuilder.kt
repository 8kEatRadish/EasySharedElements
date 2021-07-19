package com.sniperking.eatradish.compiler.activity.method

import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.ActivityClassBuilder
import com.sniperking.eatradish.compiler.activity.entity.OptionalField
import com.sniperking.eatradish.compiler.activity.entity.SharedElementField
import com.sniperking.eatradish.compiler.activity.prebuilt.*
import com.squareup.kotlinpoet.*

class StartKotlinFunctionBuilder(private val activityClass: ActivityClass) {

    fun build(fileBuilder: FileSpec.Builder) {
        val name = ActivityClassBuilder.METHOD_NAME + activityClass.simpleName
        val functionBuilder = FunSpec.builder(name)
            .receiver(ACTIVITY.kotlin)
            .addModifiers(KModifier.PUBLIC)
            .returns(UNIT)
            .addStatement(
                "val intent = %T(this,%T::class.java)",
                INTENT.kotlin,
                activityClass.typeElement
            )

        activityClass.fields.forEach { field ->
            val name = field.name
            val className = field.asKotlinTypeName()
            when (field) {
                is SharedElementField -> {
                    functionBuilder.addParameter(ParameterSpec.builder(name, INT).build())
                    functionBuilder.addStatement(
                            "val viewAttrs = %T.getViewAttrs(this,%L,%L)",
                            ANIMATION_UTILS.kotlin,name,field.elementTargetResId
                    )
                    functionBuilder.addStatement("intent.putExtra(%S,viewAttrs)", name)
                }
                is OptionalField -> {
                    functionBuilder.addParameter(
                            ParameterSpec.builder(name, className).defaultValue("null").build()
                    )
                    functionBuilder.addStatement("intent.putExtra(%S,%L)", name, name)
                }
                else -> {
                    functionBuilder.addParameter(name, className)
                    functionBuilder.addStatement("intent.putExtra(%S,%L)", name, name)
                }
            }


        }

        functionBuilder.addStatement(
            "%T.INSTANCE.startActivity(this,intent)",
            ACTIVITY_BUILDER.kotlin
        )
        fileBuilder.addFunction(functionBuilder.build())
    }
}
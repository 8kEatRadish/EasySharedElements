package com.sniperking.eatradish.compiler.activity.method

import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.ActivityClassBuilder
import com.sniperking.eatradish.compiler.activity.entity.OptionalField
import com.sniperking.eatradish.compiler.activity.entity.SharedElementField
import com.sniperking.eatradish.compiler.activity.prebuilt.*
import com.squareup.kotlinpoet.*

/**
 *文件: StartKotlinFunctionBuilder.kt
 *描述: 构造kotlin扩展方法
 *作者: SuiHongWei 7/21/21
 **/
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
            val fieldName = field.name
            val className = field.asKotlinTypeName()
            when (field) {
                is SharedElementField -> {
                    functionBuilder.addParameter(ParameterSpec.builder(fieldName, INT).build())
                    functionBuilder.addStatement(
                        "val %LViewAttrs = %T.getViewAttrs(this,%L,%L,%L,%L,%L,%L,%L,%L)",
                        fieldName,
                        ANIMATION_UTILS.kotlin,
                        fieldName,
                        field.elementTargetResId,
                        field.runEnterAnimDuration,
                        field.runExitAnimDuration,
                        field.runEnterAnimTimeInterpolatorType,
                        field.runExitAnimTimeInterpolatorType,
                        field.runEnterPriority,
                        field.runExitPriority
                    )
                    functionBuilder.addStatement(
                        "intent.putExtra(%S,%LViewAttrs)",
                        fieldName,
                        fieldName
                    )
                }
                is OptionalField -> {
                    functionBuilder.addParameter(
                        ParameterSpec.builder(fieldName, className).defaultValue("null").build()
                    )
                    functionBuilder.addStatement("intent.putExtra(%S,%L)", fieldName, fieldName)
                }
                else -> {
                    functionBuilder.addParameter(fieldName, className)
                    functionBuilder.addStatement("intent.putExtra(%S,%L)", fieldName, fieldName)
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
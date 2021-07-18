package com.sniperking.eatradish.compiler.activity.method

import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.ActivityClassBuilder
import com.sniperking.eatradish.compiler.activity.entity.OptionalField
import com.sniperking.eatradish.compiler.activity.prebuilt.ACTIVITY_BUILDER
import com.sniperking.eatradish.compiler.activity.prebuilt.CONTEXT
import com.sniperking.eatradish.compiler.activity.prebuilt.INTENT
import com.squareup.kotlinpoet.*

class StartKotlinFunctionBuilder(private val activityClass: ActivityClass) {

    fun build(fileBuilder:FileSpec.Builder){
        val name = ActivityClassBuilder.METHOD_NAME + activityClass.simpleName
        val functionBuilder = FunSpec.builder(name)
            .receiver(CONTEXT.kotlin)
            .addModifiers(KModifier.PUBLIC)
            .returns(UNIT)
            .addStatement("val intent = %T(this,%T::class.java)", INTENT.kotlin,activityClass.typeElement)

        activityClass.fields.forEach {field ->
            val name = field.name
            val className = field.asKotlinTypeName()
            if (field is OptionalField)
            {
                functionBuilder.addParameter(ParameterSpec.builder(name,className).defaultValue("null").build())
            }else{
                functionBuilder.addParameter(name,className)
            }

            functionBuilder.addStatement("intent.putExtra(%S,%L)",name,name);
        }

        functionBuilder.addStatement("%T.INSTANCE.startActivity(this,intent)", ACTIVITY_BUILDER.kotlin)
        fileBuilder.addFunction(functionBuilder.build())
    }
}
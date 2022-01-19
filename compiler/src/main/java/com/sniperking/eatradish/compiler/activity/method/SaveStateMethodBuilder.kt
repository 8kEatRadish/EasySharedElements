package com.sniperking.eatradish.compiler.activity.method

import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.entity.SharedElementField
import com.sniperking.eatradish.compiler.activity.prebuilt.ACTIVITY
import com.sniperking.eatradish.compiler.activity.prebuilt.BUNDLE
import com.sniperking.eatradish.compiler.activity.prebuilt.INTENT
import com.sniperking.eatradish.compiler.activity.prebuilt.PARCELABLE
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

/**
 *文件: SaveStateMethodBuilder.kt
 *描述: 构造保存状态方法
 *作者: SuiHongWei 7/21/21
 **/
class SaveStateMethodBuilder(private val activityClass: ActivityClass) {
    fun build(typeBuilder: TypeSpec.Builder) {
        val methodBuilder = MethodSpec.methodBuilder("saveState")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID)
            .addParameter(ACTIVITY.java, "instance")
            .addParameter(BUNDLE.java, "outState")
            .beginControlFlow("if(instance instanceof \$T)", activityClass.typeElement)
            .addStatement(
                "\$T typedInstance = (\$T)instance",
                activityClass.typeElement,
                activityClass.typeElement
            )
            .addStatement("\$T intent = new \$T()", INTENT.java, INTENT.java)
        activityClass.fields.forEach { field ->
            val name = field.name
            if (field is SharedElementField) {
                methodBuilder.addStatement(
                    "intent.putExtra(\$S,(\$T)typedInstance.getIntent().getParcelableExtra(\$S))",
                    name,
                    PARCELABLE.java,
                    name
                )
            } else if (field.isPrivate) {
                methodBuilder.addStatement(
                    "intent.putExtra(\$S,typedInstance.get\$L())",
                    name,
                    name.capitalize()
                )

            } else {
                methodBuilder.addStatement("intent.putExtra(\$S,typedInstance.\$L)", name, name)

            }
        }

        methodBuilder.addStatement("outState.putAll(intent.getExtras())").endControlFlow()
        typeBuilder.addMethod(methodBuilder.build())
    }
}
package com.sniperking.eatradish.compiler.activity.method

import com.bennyhuo.aptutils.types.asJavaTypeName
import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.entity.OptionalField
import com.sniperking.eatradish.compiler.activity.entity.SharedElementField
import com.sniperking.eatradish.compiler.activity.prebuilt.*
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.typeNameOf
import javax.lang.model.element.Modifier
/**
 *文件: InjectMethodBuilder.kt
 *描述: 构造注入方法
 *作者: SuiHongWei 7/21/21
 **/
class InjectMethodBuilder(private val activityClass: ActivityClass) {
    fun build(typeBuilder: TypeSpec.Builder) {
        val injectMethodBuilder = MethodSpec.methodBuilder("inject")
            .addParameter(activityClass.typeElement.asType().asJavaTypeName(), "instance")
            .addParameter(BUNDLE.java, "savedInstanceState")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID)
            .addStatement(
                "\$T extras = savedInstanceState == null ? instance.getIntent().getExtras() : savedInstanceState",
                BUNDLE.java
            )
            .beginControlFlow("if(extras != null)")

        activityClass.fields.forEach { field ->
            val name = field.name
            val typeName = field.asJavaTypeName()

            val unBoxedTypeName = if (typeName.isBoxedPrimitive) {
                typeName.unbox()
            } else {
                typeName
            }

            when (field) {
                is SharedElementField -> {
                    injectMethodBuilder.addStatement(
                        "\$T \$LValue = \$T.<\$T>get(extras,\$S)", VIEW_ATTRS.java, name,
                        BUNDLE_UTILS.java, VIEW_ATTRS.java, name
                    )
                    injectMethodBuilder.addStatement(
                        "VIEW_ATTRS.add(\$LValue)", name
                    )
                }
                is OptionalField -> {
                    injectMethodBuilder.addStatement(
                        "\$T \$LValue = \$T.<\$T>get(extras,\$S,(\$T)\$L)", typeName, name,
                        BUNDLE_UTILS.java, typeName, name, unBoxedTypeName, field.defaultValue
                    )
                }
                else -> {
                    injectMethodBuilder.addStatement(
                        "\$T \$LValue = \$T.<\$T>get(extras,\$S)", typeName, name,
                        BUNDLE_UTILS.java, typeName, name
                    )
                }
            }

            when {
                field is SharedElementField -> {

                    injectMethodBuilder.addStatement("int \$LResId = \$LValue.getId()", name, name)

                    injectMethodBuilder.addStatement(
                        "instance.\$L = (\$T)instance.findViewById(\$LResId)",
                        name,
                        typeName, name
                    )
                }
                field.isPrivate -> {
                    injectMethodBuilder.addStatement(
                        "instance.set\$L(\$LValue)",
                        name.capitalize(),
                        name
                    )

                }
                else -> {
                    injectMethodBuilder.addStatement("instance.\$L = \$LValue", name, name)

                }
            }
        }

        injectMethodBuilder.addStatement(
            "\$T.RunEnterAnimCallBack runEnterAnimCallBack = \$T.getRunEnterAnimCallBack(instance,START_METHOD,END_METHOD)",
            ANIMATION_UTILS.java, ANIMATION_UTILS.java
        )

        injectMethodBuilder.addStatement(
            "\$T.runEnterAnim(instance,VIEW_ATTRS,runEnterAnimCallBack)",
            ANIMATION_UTILS.java
        )

        injectMethodBuilder.endControlFlow()

        typeBuilder.addMethod(injectMethodBuilder.build())

    }
}
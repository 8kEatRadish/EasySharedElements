package com.sniperking.eatradish.compiler.activity.method

import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.entity.OptionalField
import com.sniperking.eatradish.compiler.activity.prebuilt.ACTIVITY
import com.sniperking.eatradish.compiler.activity.prebuilt.BUNDLE
import com.sniperking.eatradish.compiler.activity.prebuilt.BUNDLE_UTILS
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.kotlinpoet.typeNameOf
import javax.lang.model.element.Modifier

class InjectMethodBuilder(private val activityClass: ActivityClass) {
    fun build(typeBuilder: TypeSpec.Builder) {
        val injectMethodBuilder = MethodSpec.methodBuilder("inject")
            .addParameter(ACTIVITY.java, "instance")
            .addParameter(BUNDLE.java, "savedInstanceState")
            .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
            .returns(TypeName.VOID)
            .beginControlFlow("if(instance instanceof \$T)", activityClass.typeElement)
            .addStatement(
                "\$T typedInstance = (\$T) instance",
                activityClass.typeElement,
                activityClass.typeElement
            )
            .addStatement(
                "\$T extras = savedInstanceState == null ? typedInstance.getIntent().getExtras() : savedInstanceState",
                BUNDLE.java
            )
            .beginControlFlow("if(extras != null)")

        activityClass.fields.forEach { field ->
            val name = field.name
            val typeName = field.asJavaTypeName()

            val unBoxedTypeName = if (typeName.isBoxedPrimitive){
                typeName.unbox()
            }else{
                typeName
            }

            if (field is OptionalField) {
                injectMethodBuilder.addStatement(
                    "\$T \$LValue = \$T.<\$T>get(extras,\$S,(\$T)\$L)", typeName, name,
                    BUNDLE_UTILS.java, typeName, name, unBoxedTypeName,field.defaultValue
                )
            } else {
                injectMethodBuilder.addStatement(
                    "\$T \$LValue = \$T.<\$T>get(extras,\$S)", typeName, name,
                    BUNDLE_UTILS.java, typeName, name
                )
            }

            if (field.isPrivate) {
                injectMethodBuilder.addStatement(
                    "typedInstance.set\$L(\$LValue)",
                    name.capitalize(),
                    name
                )

            } else {
                injectMethodBuilder.addStatement("typedInstance.\$L = \$LValue", name, name)

            }

        }

        injectMethodBuilder.endControlFlow().endControlFlow()

        typeBuilder.addMethod(injectMethodBuilder.build())

    }
}
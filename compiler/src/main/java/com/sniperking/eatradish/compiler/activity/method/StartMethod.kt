package com.sniperking.eatradish.compiler.activity.method

import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.entity.Field
import com.sniperking.eatradish.compiler.activity.entity.SharedElementField
import com.sniperking.eatradish.compiler.activity.prebuilt.*
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

class StartMethod(private val activityClass: ActivityClass, private val name: String) {
    private val fields = ArrayList<Field>()

    private var isStaticMethod = true

    fun staticMethod(staticMethod: Boolean): StartMethod {
        this.isStaticMethod = staticMethod
        return this
    }

    fun addAllField(fields: List<Field>) {
        this.fields += fields
    }

    fun addField(field: Field) {
        this.fields += field
    }

    fun copy(name: String) = StartMethod(activityClass, name).also {
        it.fields.addAll(fields)
    }


    fun build(typeBuilder: TypeSpec.Builder) {
        val methodBuilder = MethodSpec.methodBuilder(name)
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.VOID)
            .addParameter(ACTIVITY.java, "activity")

        methodBuilder.addStatement(
            "\$T intent = new \$T(activity, \$T.class)",
            INTENT.java,
            INTENT.java,
            activityClass.typeElement
        )

        fields.forEach { field ->
            val name = field.name
            if (field is SharedElementField) {
                methodBuilder.addParameter(TypeName.INT, name)
                    .addStatement(
                        "\$T \$Lvalue = \$T.getViewAttrs(activity,\$L,\$L,\$L,\$L,\$L,\$L)",
                        VIEW_ATTRS.java,
                        name,
                        ANIMATION_UTILS.java,
                        name,
                        field.elementTargetResId,
                        field.runEnterAnimDuration,
                        field.runExitAnimDuration,
                        field.runEnterAnimTimeInterpolatorType,
                        field.runExitAnimTimeInterpolatorType
                    )
                    .addStatement("intent.putExtra(\$S,\$Lvalue)", name, name)
            } else {
                methodBuilder.addParameter(field.asJavaTypeName(), name)
                    .addStatement("intent.putExtra(\$S,\$L)", name, name)
            }
        }

        if (isStaticMethod) {
            methodBuilder.addModifiers(Modifier.STATIC)
        } else {
            methodBuilder.addStatement("fillIntent(intent)")
        }

        methodBuilder.addStatement(
            "\$T.INSTANCE.startActivity(activity,intent)",
            ACTIVITY_BUILDER.java
        )
        typeBuilder.addMethod(methodBuilder.build())
    }
}
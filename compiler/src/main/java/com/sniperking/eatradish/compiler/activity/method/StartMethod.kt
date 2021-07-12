package com.sniperking.eatradish.compiler.activity.method

import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.entity.Field
import com.sniperking.eatradish.compiler.activity.prebuilt.ACTIVITY_BUILDER
import com.sniperking.eatradish.compiler.activity.prebuilt.CONTEXT
import com.sniperking.eatradish.compiler.activity.prebuilt.INTENT
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
            .addParameter(CONTEXT.java, "context")

        methodBuilder.addStatement(
            "\$T intent = new \$T(context, \$T.class)",
            INTENT.java,
            INTENT.java,
            activityClass.typeElement
        )

        fields.forEach {field ->
            val name = field.name
            methodBuilder.addParameter(field.asJavaTypeName(),name)
                .addStatement("intent.putExtra(\$S,\$L)",name,name)
        }

        if (isStaticMethod){
            methodBuilder.addModifiers(Modifier.STATIC)
        }else{
            methodBuilder.addStatement("fillIntent(intent)")
        }

        methodBuilder.addStatement("\$T.INSTANCE.startActivity(context,intent)", ACTIVITY_BUILDER.java)
        typeBuilder.addMethod(methodBuilder.build())
    }
}
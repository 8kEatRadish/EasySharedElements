package com.sniperking.eatradish.compiler.activity.method

import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.prebuilt.VIEW_ATTRS
import com.sniperking.eatradish.compiler.activity.prebuilt.VIEW_ATTRS_COMPARATOR
import com.sniperking.eatradish.compiler.activity.utils.camelToUnderLine
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeSpec
import java.util.*
import javax.lang.model.element.Modifier
/**
 *文件: ConstantBuilder.kt
 *描述: 构造常量
 *作者: SuiHongWei 7/21/21
 **/
class ConstantBuilder(private val activityClass: ActivityClass) {


    fun build(typeBuilder: TypeSpec.Builder) {

        typeBuilder.addField(
            FieldSpec.builder(
                Queue::class.java, "ENTER_VIEW_ATTRS", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL
            ).initializer("new \$T<\$T>(new \$T())", PriorityQueue::class.java, VIEW_ATTRS.java, VIEW_ATTRS_COMPARATOR.java).build()
        )

        typeBuilder.addField(
            FieldSpec.builder(
                Queue::class.java, "EXIT_VIEW_ATTRS", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL
            ).initializer("new \$T<\$T>(new \$T())", PriorityQueue::class.java, VIEW_ATTRS.java, VIEW_ATTRS_COMPARATOR.java).build()
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
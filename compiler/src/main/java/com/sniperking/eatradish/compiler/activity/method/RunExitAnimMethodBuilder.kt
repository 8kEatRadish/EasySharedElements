package com.sniperking.eatradish.compiler.activity.method

import com.bennyhuo.aptutils.types.asJavaTypeName
import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.prebuilt.ANIMATION_UTILS
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Modifier
/**
 *文件: RunExitAnimMethodBuilder.kt
 *描述: 构造退出动画方法
 *作者: SuiHongWei 7/21/21
 **/
class RunExitAnimMethodBuilder(private val activityClass: ActivityClass) {
    fun build(typeBuilder: TypeSpec.Builder) {
        val runExitAnimMethodBuilder = MethodSpec.methodBuilder("runExitAnim")
            .returns(TypeName.VOID)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(activityClass.typeElement.asType().asJavaTypeName(), "instance")
            .addParameter(Long::class.java, "duration")
            .addStatement("\$L.runExitAnim(instance,VIEW_ATTRS,duration)", ANIMATION_UTILS.java)

        typeBuilder.addMethod(runExitAnimMethodBuilder.build())

    }
}
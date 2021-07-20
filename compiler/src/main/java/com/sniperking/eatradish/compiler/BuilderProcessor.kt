package com.sniperking.eatradish.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.bennyhuo.aptutils.types.isSubTypeOf
import com.sniperking.eatradish.annotations.*
import com.sniperking.eatradish.compiler.activity.ActivityClass
import com.sniperking.eatradish.compiler.activity.entity.Field
import com.sniperking.eatradish.compiler.activity.entity.OptionalField
import com.sniperking.eatradish.compiler.activity.entity.SharedElementField
import com.sun.tools.javac.code.Symbol
import java.lang.reflect.Method
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

class BuilderProcessor : AbstractProcessor() {

    private val supportedException =
        setOf(Builder::class.java, Required::class.java, Optional::class.java)

    override fun getSupportedAnnotationTypes() =
        supportedException.mapTo(HashSet<String>(), Class<*>::getName)

    override fun getSupportedSourceVersion() = SourceVersion.RELEASE_8

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {

        val activityClasses = HashMap<Element, ActivityClass>()

        roundEnv.getElementsAnnotatedWith(Builder::class.java).filter {
            it.kind.isClass
        }.forEach { element ->
            try {
                if (element.asType().isSubTypeOf("android.app.Activity")) {
                    activityClasses[element] = ActivityClass(element as TypeElement)
                } else {
                    Logger.error(element, "Unsupported typeElement: ${element.simpleName}")
                }
            } catch (e: Exception) {
                Logger.logParsingError(element, Builder::class.java, e)
            }
        }

        roundEnv.getElementsAnnotatedWith(Required::class.java)
            .filter { it.kind == ElementKind.FIELD }
            .forEach { element ->
                activityClasses[element.enclosingElement]?.fields?.add(Field(element as Symbol.VarSymbol))
                    ?: Logger.error(
                        element,
                        "Field $element annotated as Required while ${element.enclosedElements} not annotated"
                    )
            }

        roundEnv.getElementsAnnotatedWith(Optional::class.java)
            .filter { it.kind == ElementKind.FIELD }
            .forEach { element ->
                activityClasses[element.enclosingElement]?.fields?.add(OptionalField(element as Symbol.VarSymbol))
                    ?: Logger.error(
                        element,
                        "Field $element annotated as Required while ${element.enclosedElements} not annotated"
                    )
            }

        roundEnv.getElementsAnnotatedWith(SharedElement::class.java)
            .filter { it.kind == ElementKind.FIELD }
            .forEach { element ->
                activityClasses[element.enclosingElement]?.fields?.add(SharedElementField(element as Symbol.VarSymbol))
                    ?: Logger.error(
                        element,
                        "Field $element annotated as Required while ${element.enclosedElements} not annotated"
                    )
            }

        roundEnv.getElementsAnnotatedWith(RunEnterAnim::class.java)
            .filter { it.kind == ElementKind.METHOD }
            .forEach { element ->
                val runEnterAnim = element.getAnnotation(RunEnterAnim::class.java)
                activityClasses[element.enclosingElement]?.apply {
                    if (runEnterAnim.callBackState == RunEnterAnim.RunEnterAnimState.START) {
                        startMethodName = element.simpleName.toString()
                    } else if (runEnterAnim.callBackState == RunEnterAnim.RunEnterAnimState.END) {
                        endMethodName = element.simpleName.toString()
                    }
                }
            }


        activityClasses.values.forEach {
            it.builder.build(AptContext.filer)
        }
        return true
    }

}
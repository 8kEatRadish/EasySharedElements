package com.sniperking.eatradish.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.sniperking.eatradish.annotations.Builder
import com.sniperking.eatradish.annotations.Optional
import com.sniperking.eatradish.annotations.Required
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

class BuilderProcessor : AbstractProcessor() {

    private val supportedException = setOf(Builder::class.java,Required::class.java,Optional::class.java)

    override fun getSupportedAnnotationTypes() = supportedException.mapTo(HashSet<String>(),Class<*>::getName)

    override fun getSupportedSourceVersion() = SourceVersion.RELEASE_8

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        roundEnv.getElementsAnnotatedWith(Builder::class.java).forEach {
            Logger.warn(it,"看到你了 ${it.simpleName.toString()}")
        }
        return true
    }

}
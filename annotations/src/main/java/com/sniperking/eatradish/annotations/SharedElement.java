package com.sniperking.eatradish.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *文件: SharedElement.java
 *描述: 标注需要共享的动画
 *作者: SuiHongWei 7/21/21
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface SharedElement {
    String name();
    int resId();
    int runEnterTimeInterpolatorType() default 7;
    int runExitTimeInterpolatorType() default 7;
    long runEnterAnimDuration() default 800;
    long runExitAnimDuration() default 800;
    int runEnterPriority() default 0;
    int runExitPriority() default 0;
}

package com.sniperking.eatradish.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *文件: Optional.java
 *描述: 标注可选参数
 *作者: SuiHongWei 7/21/21
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Optional {
    String stringValue() default "";

    int intValue() default 0;

    float floatValue() default 0f;

    boolean booleanValue() default false;
}

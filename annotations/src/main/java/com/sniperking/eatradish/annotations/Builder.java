package com.sniperking.eatradish.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *文件: Builder.java
 *描述: 标注需要构建的activity
 *作者: SuiHongWei 7/21/21
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Builder {
}

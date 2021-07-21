package com.sniperking.eatradish.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *文件: RunEnterAnim.java
 *描述: 标注共享动画状态回调
 *作者: SuiHongWei 7/21/21
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface RunEnterAnim {

    RunEnterAnimState callBackState();

    enum RunEnterAnimState {
        START,
        END
    }
}

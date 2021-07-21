package com.sniperking.runtime.utils;

import android.os.Bundle;

/**
 *文件: BundleUtils.java
 *描述: Bundle获取指定类型类
 *作者: SuiHongWei 7/21/21
 **/
public class BundleUtils {
    public static <T> T get(Bundle bundle,String key){
        return (T)bundle.get(key);
    }

    public static <T> T get(Bundle bundle,String key,Object defaultValue){
        Object object = bundle.get(key);
        if (object == null) {
            object = defaultValue;
        }
        return (T)object;
    }
}

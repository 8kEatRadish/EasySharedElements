package com.sniperking.runtime.utils;

import com.sniperking.runtime.entity.ViewAttrs;

import java.util.Comparator;

/**
 * ViewAttrs 比较器
 *
 * @author zak
 * @date 2021/7/21
 * @email linhenji@163.com / linhenji17@gmail.com
 */
public class ViewAttrsComparator implements Comparator<ViewAttrs> {
    @Override
    public int compare(ViewAttrs o1, ViewAttrs o2) {
        return o2.getPriority() - o1.getPriority();
    }
}

package com.sniperking.runtime.utils;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import com.sniperking.runtime.TimeInterpolatorType;
import com.sniperking.runtime.entity.ViewAttrs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 文件: AnimationUtils.java
 * 描述: 共享动画工具类
 * 作者: SuiHongWei 7/21/21
 **/
public class AnimationUtils {

    private static Boolean flag = false;

    public static ViewAttrs getViewAttrs(Activity activity, int ResId, int targetResId, long runEnterAnimDuration,long runExitAnimDuration, int runEnterAnimTimeInterpolatorType,int runExitAnimTimeInterpolatorType) {
        View view = activity.findViewById(ResId);
        if (view == null) return null;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new ViewAttrs(targetResId, view.getAlpha(), location[0], location[1], view.getWidth(), view.getHeight(), runEnterAnimDuration,runExitAnimDuration, runEnterAnimTimeInterpolatorType,runExitAnimTimeInterpolatorType);
    }

    public interface RunEnterAnimCallBack {
        void callBack(int state); // 0为开始； 1为结束
    }

    public static RunEnterAnimCallBack getRunEnterAnimCallBack(final Activity activity, String startMethodName, String endMethodName) {
        RunEnterAnimCallBack runEnterAnimCallBack;
        Method startMethod = null;
        Method endMethod = null;

        try {
            startMethod = activity.getClass().getMethod(startMethodName);
            endMethod = activity.getClass().getMethod(endMethodName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (startMethod == null && endMethod == null) {
            return null;
        }

        final Method finalStartMethod = startMethod;
        final Method finalEndMethod = endMethod;
        runEnterAnimCallBack = new RunEnterAnimCallBack() {
            @Override
            public void callBack(int state) {
                try {
                    if (state == 0 && finalStartMethod != null) {
                        finalStartMethod.invoke(activity);
                    } else if (state == 1 && finalEndMethod != null) {
                        finalEndMethod.invoke(activity);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        };

        return runEnterAnimCallBack;
    }

    public static void runEnterAnim(Activity activity, final List<ViewAttrs> viewAttrsList, final RunEnterAnimCallBack runEnterAnimCallBack) {

        flag = true;
        if (runEnterAnimCallBack != null) {
            runEnterAnimCallBack.callBack(0);
        }
        long duration = 0;
        for (final ViewAttrs viewAttrs : viewAttrsList) {
            final View view = activity.findViewById(viewAttrs.getId());
            if (view == null) continue;
            duration = Math.max(duration,viewAttrs.getRunEnterAnimDuration());
            Log.d("TAG", "runEnterAnim: view locationX = " + viewAttrs.getScreenX() + "; locationY = " + viewAttrs.getScreenY());

            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float srcAlpha = view.getAlpha();

                    view.setPivotX(0f);
                    view.setPivotY(0f);
                    view.setTranslationX(viewAttrs.getScreenX() - location[0]);
                    view.setTranslationY(viewAttrs.getScreenY() - location[1]);
                    view.setScaleX(viewAttrs.getWidth() * 1.0f / view.getWidth());
                    view.setScaleY(viewAttrs.getHeight() * 1.0f / view.getHeight());
                    view.setAlpha(viewAttrs.getAlpha());

                    view.animate().alpha(srcAlpha)
                            .translationX(0f)
                            .translationY(0f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(viewAttrs.getRunEnterAnimDuration())
                            .setInterpolator(TimeInterpolatorType.timeInterpolatorMap.get(viewAttrs.getRunEnterAnimTimeInterpolatorType()))
//                        .setListener(listener)
                            .start();
                    return true;
                }
            });
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                flag = false;
                if (runEnterAnimCallBack != null) {
                    runEnterAnimCallBack.callBack(1);
                }
            }
        }, duration);
    }

    public static void runExitAnim(final Activity activity, final List<ViewAttrs> viewAttrsList) {

        if (flag) return;
        long duration = 0;
        for (ViewAttrs viewAttrs : viewAttrsList) {
            final View view = activity.findViewById(viewAttrs.getId());
            if (view == null) continue;
            duration = Math.max(duration,viewAttrs.getRunExitAnimDuration());
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            view.setPivotX(0f);
            view.setPivotY(0f);
            view.animate().alpha(viewAttrs.getAlpha())
                    .translationX(viewAttrs.getScreenX() - location[0])
                    .translationY(viewAttrs.getScreenY() - location[1])
                    .scaleX(viewAttrs.getWidth() * 1.0f / view.getWidth())
                    .scaleY(viewAttrs.getHeight() * 1.0f / view.getHeight())
                    .setDuration(viewAttrs.getRunExitAnimDuration())
                    .setInterpolator(TimeInterpolatorType.timeInterpolatorMap.get(viewAttrs.getRunExitAnimTimeInterpolatorType()))
                    .start();
        }

        viewAttrsList.clear();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }
        }, duration + 150);
    }
}

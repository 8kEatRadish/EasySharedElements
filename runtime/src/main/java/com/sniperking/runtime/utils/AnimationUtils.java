package com.sniperking.runtime.utils;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.gson.Gson;
import com.sniperking.runtime.TimeInterpolatorType;
import com.sniperking.runtime.entity.ViewAttrs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 文件: AnimationUtils.java
 * 描述: 共享动画工具类
 * 作者: SuiHongWei 7/21/21
 **/
public class AnimationUtils {

    private static Boolean flag = false;

    private static final String TAG = "AnimationUtils";

    public static ViewAttrs getViewAttrs(Activity activity, int ResId, int targetResId, long runEnterAnimDuration, long runExitAnimDuration, int runEnterAnimTimeInterpolatorType, int runExitAnimTimeInterpolatorType, int runEnterPriority, int runExitPriority) {
        View view = activity.findViewById(ResId);
        if (view == null) return null;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new ViewAttrs(targetResId, view.getAlpha(), location[0], location[1], view.getWidth(), view.getHeight(), runEnterAnimDuration, runExitAnimDuration, runEnterAnimTimeInterpolatorType, runExitAnimTimeInterpolatorType, runEnterPriority, runExitPriority);
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

        PriorityQueue<ViewAttrs> priorityQueue = new PriorityQueue<>(new Comparator<ViewAttrs>() {
            @Override
            public int compare(ViewAttrs o1, ViewAttrs o2) {
                return o1.getRunEnterPriority() - o2.getRunEnterPriority();
            }
        });

        for (ViewAttrs viewAttrs : viewAttrsList) {
            priorityQueue.offer(viewAttrs);
        }

        flag = true;
        if (runEnterAnimCallBack != null) {
            runEnterAnimCallBack.callBack(0);
        }

        long durationSum = 0, currentDuration = 0, needDelay = 0;

        List<ViewAttrs> samePriorityViewAttrs = new ArrayList<>();

        int currentPriority = priorityQueue.peek().getRunEnterPriority();

        while (!priorityQueue.isEmpty()) {
            if (currentPriority != priorityQueue.peek().getRunEnterPriority()) {
                handleSamePriorityRunEnterAnim(activity, samePriorityViewAttrs, needDelay);
                durationSum += currentDuration;
                needDelay = currentDuration;
                currentDuration = 0;
                samePriorityViewAttrs.clear();
            }

            ViewAttrs viewAttrs = priorityQueue.poll();
            currentDuration = Math.max(currentDuration, viewAttrs.getRunEnterAnimDuration());
            samePriorityViewAttrs.add(viewAttrs);
        }

        if (!samePriorityViewAttrs.isEmpty()) {
            handleSamePriorityRunEnterAnim(activity, samePriorityViewAttrs, needDelay);
            durationSum += currentDuration;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                flag = false;
                if (runEnterAnimCallBack != null) {
                    runEnterAnimCallBack.callBack(1);
                }
            }
        }, durationSum);

    }

    private static void handleSamePriorityRunEnterAnim(final Activity activity, final List<ViewAttrs> viewAttrsList, final long needDelay) {


        for (final ViewAttrs viewAttrs : viewAttrsList) {

            final View view = activity.findViewById(viewAttrs.getId());

            if (view == null) continue;

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

                    handleRunEnterAnim(view, srcAlpha, viewAttrs, needDelay);
                    return true;
                }
            });
        }
    }

    private static void handleRunEnterAnim(final View view, final float srcAlpha, final ViewAttrs viewAttrs, long needDelay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.animate().alpha(srcAlpha)
                        .translationX(0f)
                        .translationY(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(viewAttrs.getRunEnterAnimDuration())
                        .setInterpolator(TimeInterpolatorType.timeInterpolatorMap.get(viewAttrs.getRunEnterAnimTimeInterpolatorType()))
//                        .setListener(listener)
                        .start();
            }
        }, needDelay);
    }

    public static void runExitAnim(final Activity activity, final List<ViewAttrs> viewAttrsList) {

        if (flag) return;

        flag = true;

        PriorityQueue<ViewAttrs> priorityQueue = new PriorityQueue<>(new Comparator<ViewAttrs>() {
            @Override
            public int compare(ViewAttrs o1, ViewAttrs o2) {
                return o1.getRunExitPriority() - o2.getRunExitPriority();
            }
        });

        for (ViewAttrs viewAttrs : viewAttrsList) {
            priorityQueue.offer(viewAttrs);
        }

        long durationSum = 0, currentDuration = 0, needDelay = 0;

        List<ViewAttrs> samePriorityViewAttrs = new ArrayList<>();

        int currentPriority = priorityQueue.peek().getRunExitPriority();

        while (!priorityQueue.isEmpty()) {
            if (currentPriority != priorityQueue.peek().getRunExitPriority()) {
                handleRunExitAnim(activity, samePriorityViewAttrs, needDelay);
                durationSum += currentDuration;
                needDelay = currentDuration;
                currentDuration = 0;
                samePriorityViewAttrs.clear();
            }

            ViewAttrs viewAttrs = priorityQueue.poll();
            currentDuration = Math.max(currentDuration, viewAttrs.getRunExitAnimDuration());
            samePriorityViewAttrs.add(viewAttrs);
        }

        if (!samePriorityViewAttrs.isEmpty()) {
            handleRunExitAnim(activity, samePriorityViewAttrs, needDelay);
            durationSum += currentDuration;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                flag = false;
                viewAttrsList.clear();
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }
        }, durationSum + 150);
    }

    private static void handleRunExitAnim(final Activity activity, final List<ViewAttrs> samePriorityViewAttrs, long needDelay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (ViewAttrs viewAttrs : samePriorityViewAttrs) {

                    Log.d(TAG, "run: view Attrs = " + new Gson().toJson(viewAttrs));

                    final View view = activity.findViewById(viewAttrs.getId());
                    if (view == null) continue;
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
            }
        },needDelay);
    }
}

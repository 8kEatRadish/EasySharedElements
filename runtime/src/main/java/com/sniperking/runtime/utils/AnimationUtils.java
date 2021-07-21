package com.sniperking.runtime.utils;

import android.animation.Animator;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import com.sniperking.runtime.entity.ViewAttrs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;

public class AnimationUtils {

    private static Boolean flag = false;

    public static ViewAttrs getViewAttrs(Activity activity, int ResId, int targetResId, int priority) {
        View view = activity.findViewById(ResId);
        if (view == null) return null;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new ViewAttrs(targetResId, view.getAlpha(), location[0], location[1], view.getWidth(), view.getHeight(), priority);
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

    public static void runEnterAnim(Activity activity, final Queue<ViewAttrs> enterViewAttrsQueue,
                                    final Queue<ViewAttrs> exitViewAttrsQueue, final long duration,
                                    final RunEnterAnimCallBack runEnterAnimCallBack) {

        flag = true;
        if (runEnterAnimCallBack != null) {
            runEnterAnimCallBack.callBack(0);
        }
        // 需要串行展示
        boolean samePriority = true;
        ViewAttrs lastItem = enterViewAttrsQueue.peek();
        // 判断优先级并预绘制
        for (final ViewAttrs viewAttrs : enterViewAttrsQueue) {
            if (lastItem != null && viewAttrs.getPriority() != lastItem.getPriority()) {
                // 存在优先级不一样的情况
                samePriority = false;
            }

            final View view = activity.findViewById(viewAttrs.getId());
            if (view == null) continue;
            view.setVisibility(View.GONE);
        }

        if (samePriority) {
            handleEnterSamePriority(activity, enterViewAttrsQueue,
                    exitViewAttrsQueue, duration, runEnterAnimCallBack);
        } else {
            handleEnterPriority(activity, enterViewAttrsQueue,
                    exitViewAttrsQueue, duration, runEnterAnimCallBack);
        }
    }

    /**
     * 处理一样优先级的情况，优先级一致共享时长
     *
     * @param activity  activity
     * @param enterViewAttrsQueue enterViewAttrsQueue
     * @param exitViewAttrsQueue exitViewAttrsQueue
     * @param duration duration
     */
    private static void handleEnterSamePriority(Activity activity, Queue<ViewAttrs> enterViewAttrsQueue,
                                                Queue<ViewAttrs> exitViewAttrsQueue, final long duration,
                                                final RunEnterAnimCallBack runEnterAnimCallBack) {
        while (!enterViewAttrsQueue.isEmpty()) {
            final ViewAttrs viewAttrs = enterViewAttrsQueue.poll();
            if (exitViewAttrsQueue != null) {
                exitViewAttrsQueue.offer(viewAttrs);
            }
            if (viewAttrs == null) continue;
            final View view = activity.findViewById(viewAttrs.getId());
            if (view == null) continue;
            view.setVisibility(View.VISIBLE);

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
                            .setDuration(duration)
                            .setInterpolator(new LinearInterpolator())
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

    /**
     * 处理不一致优先级，每个动画各自完成相应时长
     *
     * @param activity activity
     * @param enterViewAttrsQueue enterViewAttrsQueue
     * @param exitViewAttrsQueue exitViewAttrsQueue
     * @param duration duration
     */
    private static void handleEnterPriority(final Activity activity, final Queue<ViewAttrs> enterViewAttrsQueue,
                                            final Queue<ViewAttrs> exitViewAttrsQueue, final long duration,
                                            final RunEnterAnimCallBack runEnterAnimCallBack) {
        if (enterViewAttrsQueue.isEmpty()) {
            return;
        }
        final ViewAttrs viewAttrs = enterViewAttrsQueue.poll();
        if (viewAttrs == null) return;
        exitViewAttrsQueue.offer(viewAttrs);
        final View view = activity.findViewById(viewAttrs.getId());
        if (view == null) return;
        view.setVisibility(View.VISIBLE);

        Log.d("TAG", "handlePriority: enterViewAttrsQueue size = " + enterViewAttrsQueue.size());
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
                        .setDuration(duration)
                        .setInterpolator(new LinearInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                // do nothing
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (enterViewAttrsQueue.isEmpty()) {
                                    // 进入队列为空，表示最后一个已经执行完了，回调完成方法
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            flag = false;
                                            if (runEnterAnimCallBack != null) {
                                                runEnterAnimCallBack.callBack(1);
                                            }
                                        }
                                    });
                                }
                                handleEnterPriority(activity, enterViewAttrsQueue, exitViewAttrsQueue, duration, runEnterAnimCallBack);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                // do nothing
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                // do nothing
                            }
                        })
                        .start();
                return true;
            }
        });
    }

    public static void runExitAnim(final Activity activity, final Queue<ViewAttrs> exitViewAttrsQueue, final long duration) {

        if (flag) return;

        // 需要串行展示
        boolean samePriority = true;
        ViewAttrs lastItem = exitViewAttrsQueue.peek();
        for (ViewAttrs item : exitViewAttrsQueue) {
            if (lastItem != null && item.getPriority() != lastItem.getPriority()) {
                // 存在优先级不一样的情况
                samePriority = false;
            }
        }

        if (samePriority) {
            handleExitSamePriority(activity, exitViewAttrsQueue, duration);
        } else {
            handleExitPriority(activity, exitViewAttrsQueue, duration);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }
        }, duration + 150);
    }

    /**
     * 处理同一优先级退出
     *
     * @param activity activity
     * @param exitViewAttrsQueue exitViewAttrsQueue
     * @param duration duration
     */
    private static void handleExitSamePriority(Activity activity, Queue<ViewAttrs> exitViewAttrsQueue, final long duration) {
        while (!exitViewAttrsQueue.isEmpty()) {
            final ViewAttrs viewAttrs = exitViewAttrsQueue.poll();
            if (viewAttrs == null) continue;
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
                    .setDuration(duration)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        }
    }

    /**
     * 处理不同呢优先级退出
     *
     * @param activity activity
     * @param exitViewAttrsQueue exitViewAttrsQueue
     * @param duration duration
     */
    private static void handleExitPriority(final Activity activity, final Queue<ViewAttrs> exitViewAttrsQueue, final long duration) {
        final ViewAttrs viewAttrs = exitViewAttrsQueue.poll();
        if (viewAttrs != null) {
            final View view = activity.findViewById(viewAttrs.getId());
            if (view != null) {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                view.setPivotX(0f);
                view.setPivotY(0f);
                view.animate().alpha(viewAttrs.getAlpha())
                        .translationX(viewAttrs.getScreenX() - location[0])
                        .translationY(viewAttrs.getScreenY() - location[1])
                        .scaleX(viewAttrs.getWidth() * 1.0f / view.getWidth())
                        .scaleY(viewAttrs.getHeight() * 1.0f / view.getHeight())
                        .setDuration(duration)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                // do nothing
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                handleExitPriority(activity, exitViewAttrsQueue, duration);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                // do nothing
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                // do nothing
                            }
                        })
                        .setInterpolator(new LinearInterpolator())
                        .start();
            }
        }
    }
}

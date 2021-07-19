package com.sniperking.runtime.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import com.sniperking.runtime.entity.ViewAttrs;

import java.util.logging.Logger;

public class AnimationUtils {

    private static Boolean flag = false;

    public static ViewAttrs getViewAttrs(Activity activity, int ResId, int targetResId) {
        View view = activity.findViewById(ResId);
        if (view == null) return null;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new ViewAttrs(targetResId, view.getAlpha(), location[0], location[1], view.getWidth(), view.getHeight());
    }

    public static void runEnterAnim(Activity activity, final ViewAttrs viewAttrs, final long duration) {
        final View view = activity.findViewById(viewAttrs.getId());
        if (view == null) return;
        flag = true;
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
//                        .setListener(listener)
                        .start();
                return true;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                flag = false;
            }
        }, duration);
    }

    public static void runExitAnim(final Activity activity, final ViewAttrs viewAttrs, final long duration) {

        if (flag) return;

        final View view = activity.findViewById(viewAttrs.getId());
        if (view == null) return;

        view.animate().cancel();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                view.setPivotX(0f);
                view.setPivotY(0f);

                Log.d("suihw ", "runExitAnim x = " + (viewAttrs.getScreenX() - location[0]) + "; y = " + (viewAttrs.getScreenY() - location[1]));

                view.animate().alpha(viewAttrs.getAlpha())
                        .translationX(viewAttrs.getScreenX() - location[0])
                        .translationY(viewAttrs.getScreenY() - location[1])
                        .scaleX(viewAttrs.getWidth() * 1.0f / view.getWidth())
                        .scaleY(viewAttrs.getHeight() * 1.0f / view.getHeight())
                        .setDuration(duration)
                        .setInterpolator(new LinearInterpolator())
                        .start();
            }
        }, 100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }
        }, duration + 150);
    }
}

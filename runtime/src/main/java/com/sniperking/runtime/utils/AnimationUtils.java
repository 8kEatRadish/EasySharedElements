package com.sniperking.runtime.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import com.sniperking.runtime.entity.ViewAttrs;

import java.util.logging.Logger;

public class AnimationUtils {
    public static ViewAttrs getViewAttrs(Activity activity, int ResId,int targetResId){
        View view = activity.findViewById(ResId);
        if (view == null) return null;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new ViewAttrs(targetResId,view.getAlpha(),location[0],location[1],view.getWidth(),view.getHeight());
    }

    public static void runEnterAnim(Activity activity, final ViewAttrs viewAttrs,final long duration){
        final View view = activity.findViewById(viewAttrs.getId());
        Log.d("suihw","runEnterAnim id = " + viewAttrs.getId());
        Log.d("suihw","runEnterAnim view = " + view);

        if (view == null) return;
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
    }
}

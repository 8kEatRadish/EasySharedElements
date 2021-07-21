package com.sniperking.runtime;

import android.animation.TimeInterpolator;
import android.graphics.Path;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;

import java.util.HashMap;
import java.util.Map;

public class TimeInterpolatorType {

    public static final int ACCELERATE_DECELERATE_INTERPOLATOR = 0;
    public static final int ACCELERATE_INTERPOLATOR = 1;
    public static final int ANTICIPATE_INTERPOLATOR = 2;
    public static final int ANTICIPATE_OVERSHOOT_INTERPOLATOR = 3;
    public static final int BOUNCE_INTERPOLATOR = 4;
    public static final int CYCLE_INTERPOLATOR = 5;
    public static final int DECELERATE_INTERPOLATOR = 6;
    public static final int LINEAR_INTERPOLATOR = 7;
    public static final int OVERSHOOT_INTERPOLATOR = 8;
    public static final int PATH_INTERPOLATOR = 9;

    public static Map<Integer, TimeInterpolator> timeInterpolatorMap = new HashMap<Integer, TimeInterpolator>() {
        {
            put(0, new AccelerateDecelerateInterpolator());
            put(1, new AccelerateInterpolator());
            put(2, new AnticipateInterpolator());
            put(3, new AnticipateOvershootInterpolator());
            put(4, new BounceInterpolator());
            put(5, new CycleInterpolator(1.25f));
            put(6, new DecelerateInterpolator());
            put(7, new LinearInterpolator());
            put(8, new OvershootInterpolator());
            put(9, new PathInterpolator(1f,0.1f));
        }
    };
}
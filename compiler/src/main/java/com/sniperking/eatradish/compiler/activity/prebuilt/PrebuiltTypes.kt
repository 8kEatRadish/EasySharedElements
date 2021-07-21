package com.sniperking.eatradish.compiler.activity.prebuilt

import com.bennyhuo.aptutils.types.ClassType

val CONTEXT = ClassType("android.content.Context")
val INTENT = ClassType("android.content.Intent")
val ACTIVITY = ClassType("android.app.Activity")
val BUNDLE = ClassType("android.os.Bundle")

val VIEW_ATTRS = ClassType("com.sniperking.runtime.entity.ViewAttrs")
val ANIMATION_UTILS = ClassType("com.sniperking.runtime.utils.AnimationUtils")
val VIEW_ATTRS_COMPARATOR = ClassType("com.sniperking.runtime.utils.ViewAttrsComparator")

val BUNDLE_UTILS = ClassType("com.sniperking.runtime.utils.BundleUtils")
val ACTIVITY_BUILDER = ClassType("com.sniperking.runtime.ActivityBuilder")
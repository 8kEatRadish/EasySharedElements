package com.sniperking.eatradish.compiler.activity.prebuilt

import com.bennyhuo.aptutils.types.ClassType
/**
 *文件: PrebuiltTypes.kt
 *描述: 定义相关类
 *作者: SuiHongWei 7/21/21
 **/
val CONTEXT = ClassType("android.content.Context")
val INTENT = ClassType("android.content.Intent")
val ACTIVITY = ClassType("android.app.Activity")
val BUNDLE = ClassType("android.os.Bundle")
val PARCELABLE = ClassType("android.os.Parcelable")

val VIEW_ATTRS = ClassType("com.sniperking.runtime.entity.ViewAttrs")
val ANIMATION_UTILS = ClassType("com.sniperking.runtime.utils.AnimationUtils")

val BUNDLE_UTILS = ClassType("com.sniperking.runtime.utils.BundleUtils")
val ACTIVITY_BUILDER = ClassType("com.sniperking.runtime.ActivityBuilder")
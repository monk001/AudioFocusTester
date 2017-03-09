# Copyright 2017 UCWeb Co., Ltd.

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-forceprocessing
-optimizationpasses 5
-renamesourcefileattribute ProGuard

-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses
-keepattributes *Annotation*

-assumenosideeffects class android.util.Log {
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

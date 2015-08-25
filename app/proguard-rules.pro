# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/byfieldj/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontobfuscate
-verbose
-dontpreverify



-include ../proguard-com.digits.sdk.android.digits.txt
-keep class com.j256.** { *; }
-keep class com.parse.** { *; }
-keep class javax.** { *; }
-keep class okio.BufferedSink.** { *; }
-keep class butterknife.internal.** { *;}
-keep class relish.permoveo.com.relish.fragments.** { *; }
-keep class android.app.** { *; }
-keep class android.support.v4.app.** { *; }
-keep class butterknife.** {*;}
-keep class android.animation.** { *; }
-keep class relish.permoveo.com.relish.animation.** { *; }
-keep class android.view.** { *; }
-keep class relish.permoveo.com.relish.activities.** {*;}
-keep class android.support.v7.** {*;}


# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}


## Nineolddroid related classes to ignore

-keep class com.nineoldandroids.animation.** { *; }
-keep interface com.nineoldandroids.animation.** { *; }
-keep class com.nineoldandroids.view.** { *; }
-keep interface com.nineoldandroids.view.** { *; }

-keep public class * extends android.app.Fragment {
    <init>(...);
}
-keep public class * extends android.support.v4.app.Fragment {
    <init>(...);
}

-dontwarn com.j256.**
-dontwarn com.parse.**
-dontwarn javax.**
-dontwarn okio.BufferedSink.**
-dontwarn butterknife.internal.**
-dontwarn sun.misc.**
-dontwarn org.scribe.**



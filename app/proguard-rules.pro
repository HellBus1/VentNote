# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-assumenosideeffects public class androidx.compose.runtime.ComposerKt {
   boolean isTraceInProgress();
   void traceEventStart(int,int,int,java.lang.String);
   void traceEventEnd();
}

-keep class !androidx.core.** { *; }
-keep class !androidx.lifecycle.** { *; }
-keep class !androidx.activity.** { *; }
-keep class !androidx.compose.runtime.** { *; }
-keep class !androidx.compose.ui.** { *; }
-keep class !androidx.compose.material3.** { *; }
-keep class !androidx.lifecycle.** { *; }
-keep class !androidx.datastore.** { *; }
-keep class !androidx.navigation.** { *; }
-keep class !com.google.dagger.** { *; }
-keep class !androidx.compose.material.** { *; }

-dontwarn com.google.android.gms.fido.**
-dontwarn javax.naming.**
-dontwarn javax.servlet.**
-dontwarn org.apache.**
-dontwarn org.ietf.jgss.**
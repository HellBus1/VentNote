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

# Google Play Services Auth
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.api.client.** { *; }
-keep class com.google.oauth.client.** { *; }
-keep class com.google.http.client.** { *; }
-keep class com.google.api.client.json.** { *; }
-keep class com.google.api.client.http.** { *; }
-keep class com.google.api.client.googleapis.** { *; }

# Google Drive API
-keep class com.google.api.services.drive.** { *; }
-keep class com.google.api.client.googleapis.services.** { *; }

# Google API Client for Android
-keep class com.google.api.client.googleapis.extensions.android.** { *; }
-keep class com.google.api.client.extensions.android.** { *; }

# Keep all classes related to Google Auth API
-keep class com.google.api.services.** { *; }
-keep class com.google.auth.** { *; }
-keep class com.google.api.client.auth.** { *; }
-keep class com.google.http.client.auth.** { *; }
-keep class com.google.oauth.client.auth.** { *; }

-keep class com.google.android.gms.auth.api.signin.GoogleSignInClient {
    *;
}
-keep class com.google.android.gms.auth.api.signin.GoogleSignInOptions {
    *;
}
-keep class androidx.compose.runtime.Composable {
    *;
}

# Google HTTP Client & GSON
-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault
-keepclasseswithmembers class * {
  @com.google.api.client.util.Value *;
}

# Room database
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * extends androidx.room.Database {
    <init>(...);
}
-keep class * extends androidx.room.Entity {
    <init>(...);
}
-keep class * extends androidx.room.Dao {
    <init>(...);
}
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-dontwarn androidx.room.**

# Gson specific rules
-keepattributes Signature
-keepattributes *Annotation*

# Keep your NoteModel class and all its fields
-keep class com.digiventure.ventnote.data.persistence.NoteModel { *; }
# If NoteModel has inner classes, keep them too
-keep class com.digiventure.ventnote.data.persistence.NoteModel$* { *; }

# Keep the GoogleDriveService class and all its methods
-keep class com.digiventure.ventnote.data.google_drive.GoogleDriveService { *; }

# Prevent obfuscation of the DatabaseProxy class as it is used within GoogleDriveService
-keep class com.digiventure.ventnote.module.proxy.DatabaseProxy { *; }

-keep class com.digiventure.ventnote.feature.backup.viewmodel.AuthVM

# Keep any classes that are used in Gson serialization
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep all classes that are serialized/deserialized by Gson
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Common rules for all Google APIs
-dontwarn com.google.api.client.extensions.android.**
-dontwarn com.google.api.client.googleapis.extensions.android.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.api.client.json.jackson2.**
-dontwarn javax.annotation.**

-dontwarn javax.naming.**
-dontwarn javax.servlet.**
-dontwarn org.apache.**
-dontwarn org.ietf.jgss.**


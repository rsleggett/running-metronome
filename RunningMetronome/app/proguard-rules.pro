# Preserve line numbers in stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Hilt — keep generated component classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ActivityComponentManager { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.AndroidEntryPoint <init>(...);
}

# DataStore — keep Preferences serialization
-keepclassmembers class androidx.datastore.preferences.protobuf.** { *; }

# Kotlin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Keep enums intact (RunningLevel, AudioUsageType, MetronomeSoundEnum)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep the foreground service
-keep class com.electricbiro.runningmetronome.service.MetronomeService { *; }

# Prevent stripping of Compose internals needed at runtime
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

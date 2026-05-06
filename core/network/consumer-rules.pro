# consumer-rules.pro

##─── Retrofit ────────────────────────────────────────────────────────────────

# Preserve generic signatures so Retrofit can read return types via reflection
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions

# Keep Retrofit service interfaces and their annotated methods intact
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Retain Retrofit's checked-exception types
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

##─── OkHttp ──────────────────────────────────────────────────────────────────

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

##─── Gson ────────────────────────────────────────────────────────────────────

# Keep Gson's internal type-token mechanism (used by GsonConverterFactory)
-keep class com.google.gson.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-dontwarn com.google.gson.**

##─── Custom network layer ────────────────────────────────────────────────────

# Keep your sealed ApiResult and its subclasses so the call adapter
# can match on the raw type at runtime without hitting "must be parameterized"
-keep class com.application.echo.core.network.** { *; }

# Keep all Retrofit API service interfaces so Retrofit's dynamic
# proxy can cast to them correctly at runtime
-keep interface com.application.echo.api.** { *; }
-keep interface com.application.echo.**.api.** { *; }
# proguard-rules.pro

##─── API response models ─────────────────────────────────────────────────────
# Keep any data classes used as Retrofit response/request bodies
# (if they live in the app module rather than a feature module)
-keep class com.application.echo.**.model.** { *; }
-keep class com.application.echo.**.dto.** { *; }
-keep class com.application.echo.**.response.** { *; }
-keep class com.application.echo.**.request.** { *; }

##─── Kotlin ──────────────────────────────────────────────────────────────────
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
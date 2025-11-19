# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Gson
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ExoPlayer/Media3
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-dontwarn com.bumptech.glide.**

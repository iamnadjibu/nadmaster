# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Firebase Firestore — keep data model classes
-keep class nad.master.pa.data.model.** { *; }
-keep class com.google.firebase.** { *; }
-keepattributes Signature

# Kotlin serialization
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class **_HiltModules { *; }

# Compose
-keep class androidx.compose.** { *; }

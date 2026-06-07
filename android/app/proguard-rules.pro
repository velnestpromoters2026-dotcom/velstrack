# Gson specific rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
# Prevents R8 from stripping generics causing java.lang.Class cannot be cast to java.lang.reflect.ParameterizedType
-keep class com.velstrack.app.data.remote.dto.** { *; }
-keep class com.velstrack.app.data.remote.api.** { *; }

# Retrofit & OkHttp
-keep class retrofit2.** { *; }
-keepnames class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keepnames class okhttp3.** { *; }
-keep class okio.** { *; }
-keepnames class okio.** { *; }

-keep class kotlin.coroutines.Continuation { *; }
-keep class kotlinx.coroutines.** { *; }

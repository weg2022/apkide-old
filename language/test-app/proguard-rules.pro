-keeppackagenames com.flurry.*
-keeppackagenames com.google.*
-keeppackagenames androidx.*
-keeppackagenames weg.devj.**
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
#-keepattributes MethodParameters
#-allowaccessmodification
#-dontoptimize
-dontshrink
-dontusemixedcaseclassnames
-verbose
-repackageclasses 'github.applenae'
# Preserve some attributes that may be required for reflection.
-keepattributes Exceptions,
				Deprecated,
				AnnotationDefault,
                EnclosingMethod,
                InnerClasses,
                RuntimeVisibleAnnotations,
                RuntimeVisibleParameterAnnotations,
                RuntimeVisibleTypeAnnotations,
                Signature,
                LineNumberTable

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# For enumeration classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Preserve annotated Javascript interface methods.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# The support libraries contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontnote android.support.**
-dontnote androidx.**
-dontwarn android.support.**
-dontwarn androidx.**

# This class is deprecated, but remains for backward compatibility.
-dontwarn android.util.FloatMath

-keep class androidx.annotation.Keep

-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

-dontnote org.apache.http.**
-dontnote android.net.http.**
-dontnote java.lang.invoke.**

-keep class * extends android.content.ContextWrapper
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep class * extends androidx.core.content.FileProvider

-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.preference.Preference

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-dontwarn com.**
-dontwarn jdk.**
-dontwarn sun.**
-dontwarn groovy.**
-dontwarn groovyjarjarasm.**
-dontwarn java.**
-dontwarn javax.**
-dontwarn org.**
-dontwarn libcore.**
-dontwarn dalvik.**

-keep class com.flurry.** { *; }
-dontwarn com.flurry.**

 -keep class * extends org.eclipse.jgit.nls.TranslationBundle{
    <fields>;
 }

 -keepnames class * implements android.os.Parcelable {
     public static final ** CREATOR;
 }

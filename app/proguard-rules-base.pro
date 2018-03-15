#-------------------------------------------定制化区域----------------------------------------------
#---------------------------------1.实体类---------------------------------
-keepclasseswithmembers  class * implements skean.me.base.db.IBaseModel
-keepclasseswithmembers  class skean.me.base.net.PgyAppInfo { *; }
-keepclasseswithmembers  class skean.me.base.net.PgyVersionInfo { *; }

#-------------------------------------------------------------------------

#---------------------------------2.第三方包-------------------------------

#permissionsdispatcher:2.3.2
-dontwarn permissions.dispatcher.**
-keepclasseswithmembers  class permissions.dispatcher.** {*;}
#androidannotations-api:4.2.0
-dontwarn org.androidannotations.**
-keepclasseswithmembers  class org.androidannotations.** {*;}
#guava:20.0'
-dontwarn com.google.**
-keepclasseswithmembers  class com.google.** {*;}

#Retrofit相关库
#retrofit:2.1.0'
-dontwarn retrofit2.**
-keepclasseswithmembers  class retrofit2.** {*;}
#converter-jackson:2.1.0'
-dontwarn retrofit2.converter.jackson.**
-keepclasseswithmembers  class retrofit2.converter.jackson.** {*;}
#adapter-rxjava:2.1.0'
-dontwarn retrofit2.adapter.rxjava.**
-keepclasseswithmembers  class retrofit2.adapter.rxjava.** {*;}
#logging-interceptor:3.5.0'
-dontwarn okhttp3.logging.**
-keepclasseswithmembers  class okhttp3.logging.** {*;}
#PersistentCookieJar:v1.0.0'
-dontwarn com.franmontiel.persistentcookiejar.**
-keepclasseswithmembers  class com.franmontiel.persistentcookiejar.** {*;}

#RxJava的库
#rxandroid:1.2.1'
-dontwarn rx.android.**
-keepclasseswithmembers  class  rx.android.** {*;}
#rxjava:1.1.6'
-dontwarn rx.**
-keepclasseswithmembers  class rx.** {*;}
-dontwarn io.reactivex.**
-keepclasseswithmembers  class io.reactivex.** {*;}
#rxbinding:0.4.0'
-dontwarn com.jakewharton.rxbinding2.**
-keepclasseswithmembers  class com.jakewharton.rxbinding2.** {*;}

#DBFlow
#dbflow-core:3.1.1'
-dontwarn com.raizlabs.android.dbflow.**
-keepclasseswithmembers  class com.raizlabs.android.dbflow.** {*;}
#dbflow:3.1.1'
-dontwarn com.raizlabs.android.dbflow.**
-keepclasseswithmembers   class com.raizlabs.android.dbflow.** {*;}

#GreenDAO
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**

#MVP框架
#mvp:3.0.0'
#viewstate:3.0.0'
#mvi:3.0.0'
-dontwarn com.hannesdorfmann.mosby3.**
-keepclasseswithmembers  class com.hannesdorfmann.mosby3.** {*;}

#json解析器
#jackson-core:2.9.0.pr1'
-dontwarn com.fasterxml.jackson.core.**
-keepclasseswithmembers   class com.fasterxml.jackson.core.** {*;}
#jackson-databind:2.9.0.pr1'
-dontwarn com.fasterxml.jackson.databind.**
-keepclasseswithmembers  class com.fasterxml.jackson.databind.** {*;}
#jackson-annotations:2.9.0.pr1'
-dontwarn com.fasterxml.jackson.annotation.**
-keepclasseswithmembers  class com.fasterxml.jackson.annotation.** {*;}

#三方工具
#commons-codec:1.10'
-dontwarn org.apache.commons.**
-keepclasseswithmembers  class org.apache.commons.** {*;}
#commons-net:3.5'
#commons-collections4:4.1'
#android-weak-handler:1.1'
-dontwarn com.badoo.mobile.util.**
-keepclasseswithmembers  class com.badoo.mobile.util.** {*;}
#picasso:2.5.2'
-dontwarn com.squareup.picasso.**
-keepclasseswithmembers  class com.squareup.picasso.** {*;}
#pgyersdk:sdk:2.4.0'
-dontwarn com.pgyersdk.**
-keepclasseswithmembers  class com.pgyersdk.** {*;}
-dontwarn com.readystatesoftware.systembartint.**
-keepclasseswithmembers  class com.readystatesoftware.systembartint.** {*;}
#PhotoView:1.3.0'
-dontwarn uk.co.senab.photoview.**
-keepclasseswithmembers  class uk.co.senab.photoview.** {*;}
#subsampling-scale-image-view:3.5.0'
-dontwarn com.davemorrissey.labs.subscaleview.**
-keepclasseswithmembers  class com.davemorrissey.labs.subscaleview.** {*;}
#MultiImageSelector:1.2'
-dontwarn me.nereo.multi_image_selector.**
-keepclasseswithmembers  class me.nereo.multi_image_selector.** {*;}
#viewpagerindicator:library:2.4.1.1@aar'
-dontwarn com.viewpagerindicator.**
-keepclasseswithmembers  class com.viewpagerindicator.** {*;}
#utilcode:1.3.6'
-dontwarn com.blankj.utilcode.**
-keepclasseswithmembers  class com.blankj.utilcode.** {*;}
#browse:1.0.0'
-dontwarn com.tamic.jswebview.**
-keepclasseswithmembers  class com.tamic.jswebview.** {*;}
#numberprogressbar:library:1.2@aar'
-dontwarn com.daimajia.numberprogressbar.**
-keepclasseswithmembers  class com.daimajia.numberprogressbar.** {*;}
#ultra-ptr:1.0.11'
-dontwarn in.srain.cube.views.ptr.**
-keepclasseswithmembers  class in.srain.cube.views.ptr.** {*;}
#BaseRecyclerViewAdapterHelper:2.8.0'
-dontwarn com.chad.library.**
-keepclasseswithmembers  class com.chad.library.** {*;}
#material-dialogs:core:0.9.4.2'
-dontwarn com.afollestad.materialdialogs.**
-keepclasseswithmembers  class com.afollestad.materialdialogs.** {*;}
#sublimepickerlibrary:2.1.1
-dontwarn com.appeaser.sublimepickerlibrary.**
-keepclasseswithmembers class com.appeaser.sublimepickerlibrary.**{*;}
#rxpermissions:0.9.4
-dontwarn com.tbruyelle.rxpermissions.**
-keepclasseswithmembers class com.tbruyelle.rxpermissions.**{*;}
#easypermissiondialog:1.0
-dontwarn skean.yzsm.com.easypermissiondialog.**
-keepclasseswithmembers class skean.yzsm.com.easypermissiondialog.**{*;}
#textdrawable
-dontwarn com.amulyakhare.textdrawable.**
-keepclasseswithmembers class com.amulyakhare.textdrawable.**{*;}


#libs内部包
#百度
-dontwarn com.baidu.**
-keepclasseswithmembers  class com.baidu.** {*;}
-dontwarn vi.com.gdi.bgl.android.java.**
-keepclasseswithmembers  class vi.com.gdi.bgl.android.java.** {*;}

#其他引用的增补
-dontwarn okhttp3.**
-keepclasseswithmembers class okhttp3.** {*;}
-dontwarn okio.**
-keepclasseswithmembers class okio.** {*;}
-dontwarn me.zhanghai.android.materialprogressbar.**
-keepclasseswithmembers class me.zhanghai.android.materialprogressbar.** {*;}

#高德
-dontwarn com.amap.**
-keepclasseswithmembers class com.amap.** {*;}

#EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
#-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
#    <init>(java.lang.Throwable);
#}

#图片触摸ImageTouch
-dontwarn it.sephiroth.android.library.imagezoom.**
-keepclasseswithmembers class it.sephiroth.android.library.imagezoom.** {*;}

#Glide
-dontwarn com.bumptech.glide.**
-keepclasseswithmembers class com.bumptech.glide.** {*;}
#-------------------------------------------------------------------------

#---------------------------------3.与js互相调用的类------------------------



#-------------------------------------------------------------------------

#---------------------------------4.反射相关的类和方法-----------------------



#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5
-dontskipnonpubliclibraryclassmembers
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}
#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------
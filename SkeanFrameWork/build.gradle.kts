plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-allopen")
    id("kotlin-kapt")
    id("maven-publish")
}

android {
    namespace = "me.skean.skeanframework"
    compileSdk = 34
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-Xjvm-default=all-compatibility"
    }

    defaultConfig {
        minSdk = 21
        manifestPlaceholders["AMAP_API_KEY"] = ""
        manifestPlaceholders["BUGLY_APPID"] = ""
        manifestPlaceholders["BUGLY_ENABLE_DEBUG"] = ""
        manifestPlaceholders["PGYER_API_KEY"] = ""
        manifestPlaceholders["PGYER_APP_KEY"] = ""
    }

    kapt {
        arguments {
            arg("eventBusIndex", "me.skean.skeanframework.EventBusIndex")
            arg("androidManifestFile", "$projectDir/src/main/AndroidManifest.xml")
        }
    }

    lint {
        abortOnError = false
    }
    viewBinding.isEnabled = true
    dataBinding.enable = true
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    publishing {
        singleVariant("release"){
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.Skeaner"
            artifactId = "SkeanFrameWork"
            version = "2.3.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

repositories {
    flatDir {
        dirs("libs")
    }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}

val eventbusVersion = "3.1.1"
val retrofitVersion = "2.6.4"
val koinVersion = "4.0.4"
val lifecycleVersion = "2.8.7"
val navigationVersion = "2.8.9"
val roomVersion = "2.7.1"
val jacksonVersion = "2.11.1"
val rxLifecycleVersion = "3.1.0"

val kotlinVersion = rootProject.extra["kotlinVersion"]

dependencies {
    api(fileTree("libs") { include("*.jar") })
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    api("org.greenrobot:eventbus:$eventbusVersion")
    kapt("org.greenrobot:eventbus-annotation-processor:$eventbusVersion")
    //谷歌库
    api("androidx.core:core-ktx:1.13.1")
    api("androidx.appcompat:appcompat:1.7.0")
    api("androidx.recyclerview:recyclerview:1.3.2")
    api("com.google.android.material:material:1.12.0")
    api("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    api("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    api("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    api("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    api("androidx.room:room-runtime:$roomVersion")
    api("androidx.room:room-rxjava2:$roomVersion")
    api("androidx.room:room-ktx:$roomVersion")
    api("androidx.sqlite:sqlite-ktx:2.2.0")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    //retrofit2
    api("com.squareup.retrofit2:retrofit:$retrofitVersion")
    api("com.squareup.retrofit2:converter-jackson:$retrofitVersion")
    api("com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion")
    api("com.squareup.okhttp3:logging-interceptor:4.8.1")
    api("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    //RxJava的库
    api("io.reactivex.rxjava2:rxandroid:2.1.1")
    api("io.reactivex.rxjava2:rxjava:2.2.19")
    api ("com.jakewharton.rxbinding3:rxbinding-core:3.1.0")
    api ("com.jakewharton.rxbinding3:rxbinding-appcompat:3.1.0")
    api ("com.jakewharton.rxbinding3:rxbinding-recyclerview:3.1.0")
    api ("com.jakewharton.rxbinding3:rxbinding-material:3.1.0")
    api("com.trello.rxlifecycle3:rxlifecycle:$rxLifecycleVersion")
    api("com.trello.rxlifecycle3:rxlifecycle-android:$rxLifecycleVersion")
    api("com.trello.rxlifecycle3:rxlifecycle-components:$rxLifecycleVersion")
    api("com.trello.rxlifecycle3:rxlifecycle-android-lifecycle:$rxLifecycleVersion")
    api("com.trello.rxlifecycle3:rxlifecycle-kotlin:$rxLifecycleVersion")
    api("com.trello.rxlifecycle3:rxlifecycle-android-lifecycle-kotlin:$rxLifecycleVersion")
    //高德地图
    api("com.amap.api:map2d:6.0.0")
    api("com.amap.api:location:6.1.0")
    // sql-cipher支持
    api("net.zetetic:android-database-sqlcipher:4.4.0@aar")
    //json解析器
    api("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    //Apache的常用库
    api("commons-codec:commons-codec:1.15")
    api("commons-net:commons-net:3.5")
    api("org.apache.commons:commons-collections4:4.4")
    api("org.apache.commons:commons-lang3:3.9")
    //软引用的handler
    api("com.github.badoo:android-weak-handler:1.3")
    //图片加载
    api("io.coil-kt:coil:2.2.2")
    api("io.coil-kt:coil-gif:2.2.2")
    api("io.coil-kt:coil-video:2.2.2")
    api("com.github.bumptech.glide:glide:4.11.0")
    //图片显示PhotoView
    api("com.github.chrisbanes:PhotoView:2.3.0")
    //图片触摸库
    api("it.sephiroth.android.library.imagezoom:imagezoom:2.3.0")
    //图片显示subsamplingImageView
    api("com.davemorrissey.labs:subsampling-scale-image-view-androidx:3.10.0")
    //图片选择器matisse
    api("com.github.Skeaner:Matisse:0.5.3-beta3-extend-1.5") {
        exclude(module = "library")
    }
    //综合工具库utilcode
    api("com.blankj:utilcodex:1.30.6")
    //RecyclerView综合适配器
    api("io.github.cymchad:BaseRecyclerViewAdapterHelper4:4.1.4")
    //material design日期加时间选择器
    api("com.github.Skeaner:SublimePicker:2.1.4")
    //权限提示
    api("com.github.Skeaner:EasyPermissionDialog:1.8")
    //BUGLY
    api("com.tencent.bugly:crashreport:4.1.9.3")
    //butterknife
    api("com.jakewharton:butterknife:10.2.0")
    kapt("com.jakewharton:butterknife-compiler:10.2.0")
    //数字进度条
    api("com.github.Skeaner:NumberProgressBar:1.4.1")
    //sharedpreferences
    api("com.chibatching.kotpref:kotpref:2.13.1")
    api("com.chibatching.kotpref:preference-screen-dsl:2.13.1")
    api("com.github.cioccarellia:ksprefs:2.3.2")

    //Permission
    api("com.github.Skeaner:XXPermissions:21.3")
    //旧款 MaterialDialog
    api("com.github.Skeaner.OldMaterialDialogs:core:1.0.2")
    api("com.github.Skeaner.OldMaterialDialogs:commons:1.0.2")
    //MaterialDialog
    api("com.afollestad.material-dialogs:core:3.3.0")
    api("com.afollestad.material-dialogs:input:3.3.0")
    api("com.afollestad.material-dialogs:bottomsheets:3.3.0")
    //刷新库
    api("io.github.scwang90:refresh-layout-kernel:2.1.0x")
    api("io.github.scwang90:refresh-header-classics:2.1.0x")  //经典刷新头
    api("io.github.scwang90:refresh-footer-classics:2.1.0x")
    //koin
    api("io.insert-koin:koin-core:$koinVersion")
    api("io.insert-koin:koin-android:$koinVersion")// Koin main features for Android
    api("io.insert-koin:koin-android-compat:$koinVersion")// Java Compatibility
    api("io.insert-koin:koin-androidx-workmanager:$koinVersion")// Jetpack WorkManager
    api("io.insert-koin:koin-androidx-navigation:$koinVersion")// Navigation Graph
    api("io.insert-koin:koin-androidx-compose:$koinVersion")// Jetpack Compose
    //viewBinding快速库
    api("com.hi-dhl:binding:1.2.0")
    api("com.github.DylanCaiCoding.ViewBindingKTX:viewbinding-ktx:2.0.5")
    //FlexibleDivider
    api("com.github.mazenrashed:RecyclerView-FlexibleDivider:1.5.0")
    //快速ActivityLauncher
    api("com.github.DylanCaiCoding:ActivityResultLauncher:1.1.2")
    //mvi框架
    api("com.github.Skeaner:MVVMHabit:4.0.0-androidx"){
        exclude(group = "com.github.tbruyelle", module = "rxpermissions")
    }
    api("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

}

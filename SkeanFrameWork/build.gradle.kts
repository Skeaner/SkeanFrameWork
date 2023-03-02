plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-allopen")
    id("kotlin-kapt")
    id("maven-publish")
}

group = "com.github.Skeaner"

//打包操作
val androidSourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(android.sourceSets.getByName("main").java.srcDirs)
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                artifact(androidSourcesJar.get())
            }
        }
    }
}

android {
    compileSdk = 31
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-Xjvm-default=compatibility"
    }

    defaultConfig {
        minSdk = 21
        targetSdk = 28
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

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
val koinVersion = "3.1.5"
val lifecycleVersion = "2.4.1"
val navigationVersion = "2.3.5"
val roomVersion = "2.3.0"
val jacksonVersion = "2.11.0"
val rxLifecycleVersion = "3.1.0"

val kotlinVersion = rootProject.extra["kotlinVersion"]

dependencies {
    api(fileTree("libs") { include("*.jar") })
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    api("com.github.permissions-dispatcher:permissionsdispatcher:4.8.0")
    kapt("com.github.permissions-dispatcher:permissionsdispatcher-processor:4.8.0")
    api("org.greenrobot:eventbus:$eventbusVersion")
    kapt("org.greenrobot:eventbus-annotation-processor:$eventbusVersion")
    //谷歌库
    api("androidx.core:core-ktx:1.8.0")
    api("androidx.appcompat:appcompat:1.4.2")
    api("androidx.recyclerview:recyclerview:1.2.1")
    api("com.google.android.material:material:1.6.1")
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
    api("com.jakewharton.rxbinding2:rxbinding:2.2.0")
    api("com.jakewharton.rxbinding2:rxbinding-support-v4:2.2.0")
    api("com.jakewharton.rxbinding2:rxbinding-appcompat-v7:2.2.0")
    api("com.jakewharton.rxbinding2:rxbinding-design:2.2.0")
    api("com.jakewharton.rxbinding2:rxbinding-recyclerview-v7:2.2.0")
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
    api("com.badoo.mobile:android-weak-handler:1.1")
    //图片加载
    api("io.coil-kt:coil:2.2.2")
    api("io.coil-kt:coil-gif:2.2.2")
    api("io.coil-kt:coil-video:2.2.2")
    api("com.github.bumptech.glide:glide:4.10.0")
    //图片显示PhotoView
    api("com.github.chrisbanes:PhotoView:2.3.0")
    //图片触摸库
    api("it.sephiroth.android.library.imagezoom:imagezoom:2.3.0")
    //图片显示subsamplingImageView
    api("com.davemorrissey.labs:subsampling-scale-image-view:3.10.0")
    //图片选择器matisse
    api("com.github.Skeaner:Matisse:0.5.3-beta3-extend-1.5") {
        exclude(module = "library")
    }
    //综合工具库utilcode
    api("com.blankj:utilcodex:1.30.6")
    //下拉刷新ultra-ptr
    api("in.srain.cube:ultra-ptr:1.0.11")
    //RecyclerView综合适配器
    api("com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.7")
    //material design日期加时间选择器
    api("com.github.Skeaner:SublimePicker:2.1.2")
    //权限提示
    api("com.github.Skeaner:EasyPermissionDialog:1.8")
    //蒲公英sdk
    api ("com.pgyer:analytics:4.3.2")
    //butterknife
    api("com.jakewharton:butterknife:10.2.0")
    kapt("com.jakewharton:butterknife-compiler:10.2.0")
    //数字进度条
    api("com.github.Skeaner:NumberProgressBar:1.4.1")
    //kotpref
    api("com.chibatching.kotpref:kotpref:2.13.1")
    api("com.chibatching.kotpref:preference-screen-dsl:2.13.1")
    //rxpermission
    api("com.github.tbruyelle:rxpermissions:0.11")
    //旧款 MaterialDialog
    api("com.github.Skeaner.OldMaterialDialogs:core:1.0.0")
    api("com.github.Skeaner.OldMaterialDialogs:commons:1.0.0")
    //MaterialDialog
    api("com.afollestad.material-dialogs:core:3.3.0")
    api("com.afollestad.material-dialogs:input:3.3.0")
    api("com.afollestad.material-dialogs:bottomsheets:3.3.0")
    //刷新库
    api("io.github.scwang90:refresh-layout-kernel:2.0.5")
    api("io.github.scwang90:refresh-header-classics:2.0.5")  //经典刷新头
    api("io.github.scwang90:refresh-footer-classics:2.0.5")
    //koin
    api("io.insert-koin:koin-core:$koinVersion")
    api("io.insert-koin:koin-android:$koinVersion")// Koin main features for Android
    api("io.insert-koin:koin-android-compat:$koinVersion")// Java Compatibility
    api("io.insert-koin:koin-androidx-workmanager:$koinVersion")// Jetpack WorkManager
    api("io.insert-koin:koin-androidx-navigation:$koinVersion")// Navigation Graph
    api("io.insert-koin:koin-androidx-compose:$koinVersion")// Jetpack Compose
    //viewBinding快速库
    api("com.hi-dhl:binding:1.1.3")
    api("com.github.DylanCaiCoding.ViewBindingKTX:viewbinding-ktx:2.0.5")
    api("com.github.DylanCaiCoding.ViewBindingKTX:viewbinding-brvah:2.0.5")
    //FlexibleDivider
    api("com.github.mazenrashed:RecyclerView-FlexibleDivider:1.5.0")
    //快速ActivityLauncher
    api("com.github.DylanCaiCoding:ActivityResultLauncher:1.1.2")

}

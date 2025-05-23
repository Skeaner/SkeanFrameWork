import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

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
        singleVariant("release") {
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
    maven { url = uri("https://nexus.terrestris.de/repository/public/") }
}

val retrofitVer = "2.11.0"
val koinVer = "4.0.4"
val lifecycleVer = "2.8.7"
val navigationVer = "2.8.9"
val roomVer = "2.7.1"
val jacksonVer = "2.17.0"
val rxLifecycleVer = "4.0.2"
val rxbindingVer = "4.0.0"
val coilVer = "2.7.0"
val coroutinesVer = "1.8.1"

val kotlinVersion = rootProject.extra["kotlinVersion"]

dependencies {
    api(fileTree("libs") { include("*.jar") })
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVer")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVer")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$coroutinesVer")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:$coroutinesVer")

    //谷歌库
    api("androidx.core:core-ktx:1.13.1")
    api("androidx.appcompat:appcompat:1.7.0")
    api("androidx.recyclerview:recyclerview:1.3.2")
    api("com.google.android.material:material:1.12.0")
    api("androidx.navigation:navigation-fragment-ktx:$navigationVer")
    api("androidx.navigation:navigation-ui-ktx:$navigationVer")
    api("androidx.navigation:navigation-fragment-ktx:$navigationVer")
    api("androidx.navigation:navigation-ui-ktx:$navigationVer")
    api("androidx.room:room-runtime:$roomVer")
    api("androidx.room:room-rxjava3:$roomVer")
    api("androidx.room:room-ktx:$roomVer")
    api("androidx.sqlite:sqlite-ktx:2.5.1")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVer")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVer")
    api("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVer")
    api("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVer")
    api("androidx.lifecycle:lifecycle-common-java8:$lifecycleVer")
    api("androidx.lifecycle:lifecycle-service:$lifecycleVer")
    api("androidx.lifecycle:lifecycle-process:$lifecycleVer")
    api("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycleVer")
    api("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVer")
    //retrofit2
    api("com.squareup.retrofit2:retrofit:$retrofitVer")
    api("com.squareup.retrofit2:converter-jackson:$retrofitVer")
    api("com.squareup.retrofit2:adapter-rxjava3:$retrofitVer")
    api("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    api("cn.numeron:http:1.0.10")
    //RxJava的库
    api("io.reactivex.rxjava3:rxandroid:3.0.2")
    api("io.reactivex.rxjava3:rxjava:3.1.10")
    api("com.jakewharton.rxbinding4:rxbinding-core:$rxbindingVer")
    api("com.jakewharton.rxbinding4:rxbinding-appcompat:$rxbindingVer")
    api("com.jakewharton.rxbinding4:rxbinding-recyclerview:$rxbindingVer")
    api("com.jakewharton.rxbinding4:rxbinding-material:$rxbindingVer")
    api("com.trello.rxlifecycle4:rxlifecycle:$rxLifecycleVer")
    api("com.trello.rxlifecycle4:rxlifecycle-android:$rxLifecycleVer")
    api("com.trello.rxlifecycle4:rxlifecycle-components:$rxLifecycleVer")
    api("com.trello.rxlifecycle4:rxlifecycle-android-lifecycle:$rxLifecycleVer")
    api("com.trello.rxlifecycle4:rxlifecycle-kotlin:$rxLifecycleVer")
    api("com.trello.rxlifecycle4:rxlifecycle-android-lifecycle-kotlin:$rxLifecycleVer")

    //EVENT
    api("com.github.michaellee123:LiveEventBus:1.8.14")
    //高德地图
    api("com.amap.api:map2d:6.0.0")
    api("com.amap.api:location:6.1.0")
    // sql-cipher支持
    api("net.zetetic:android-database-sqlcipher:4.5.4")
    //json解析器
    api("com.fasterxml.jackson.core:jackson-core:$jacksonVer")
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVer")
    api("com.fasterxml.jackson.core:jackson-annotations:$jacksonVer")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVer")
    //Apache的常用库
    api("commons-codec:commons-codec:1.15")
    api("commons-net:commons-net:3.9.0")
    api("org.apache.commons:commons-collections4:4.4")
    api("org.apache.commons:commons-lang3:3.12.0")
    //软引用的handler
    api("com.github.badoo:android-weak-handler:1.3")
    //图片加载
    api("io.coil-kt:coil:$coilVer")
    api("io.coil-kt:coil-gif:$coilVer")
    api("io.coil-kt:coil-video:$coilVer")
    api("com.github.bumptech.glide:glide:4.16.0")
    //图片显示PhotoView
    api("com.github.chrisbanes:PhotoView:2.3.0")
    //图片触摸库
    api("it.sephiroth.android.library.imagezoom:imagezoom:2.3.0")
    //图片显示subsamplingImageView
    api("com.davemorrissey.labs:subsampling-scale-image-view-androidx:3.10.0")
    //图片选择器
    api("io.github.lucksiege:pictureselector:v3.11.2")
    api("io.github.lucksiege:compress:v3.11.2")
    api("io.github.lucksiege:ucrop:v3.11.2")
    api("io.github.lucksiege:camerax:v3.11.2")
    //综合工具库utilcode
    api("com.blankj:utilcodex:1.31.1")
    //RecyclerView综合适配器
    api("io.github.cymchad:BaseRecyclerViewAdapterHelper4:4.1.7")
    //material design日期加时间选择器
    api("com.github.Skeaner:SublimePicker:2.1.4")
    //权限提示
    api("com.github.Skeaner:EasyPermissionDialog:1.9")
    //BUGLY
    api("com.tencent.bugly:crashreport:4.1.9.3")
    //数字进度条
    api("com.github.Skeaner:NumberProgressBar:1.4.1")
    //sharedpreferences
    api("com.chibatching.kotpref:kotpref:2.13.2")
    api("com.chibatching.kotpref:enum-support:2.13.2")
    api("com.chibatching.kotpref:gson-support:2.13.2")
    api("com.google.code.gson:gson:2.10.1")
    api("com.chibatching.kotpref:livedata-support:2.13.2")
    api("com.chibatching.kotpref:preference-screen-dsl:2.13.2")
    api("com.github.cioccarellia:ksprefs:2.4.1")

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
    api("io.github.scwang90:refresh-layout-kernel:3.0.0-alpha")
    api("io.github.scwang90:refresh-header-classics:3.0.0-alpha")  //经典刷新头
    api("io.github.scwang90:refresh-footer-classics:3.0.0-alpha")
    //koin
    api("io.insert-koin:koin-core:$koinVer")
    api("io.insert-koin:koin-android:$koinVer")// Koin main features for Android
    api("io.insert-koin:koin-android-compat:$koinVer")// Java Compatibility
    api("io.insert-koin:koin-androidx-workmanager:$koinVer")// Jetpack WorkManager
    api("io.insert-koin:koin-androidx-navigation:$koinVer")// Navigation Graph
    api("io.insert-koin:koin-androidx-compose:$koinVer")// Jetpack Compose
    //viewBinding快速库
    api("com.hi-dhl:binding:1.2.0")
    api("com.github.DylanCaiCoding.ViewBindingKTX:viewbinding-ktx:2.1.0")
    //FlexibleDivider
    api("com.github.mazenrashed:RecyclerView-FlexibleDivider:1.5.0")
    //快速ActivityLauncher
    api("com.github.DylanCaiCoding:ActivityResultLauncher:1.1.2")
    //mvvm框架
    api("com.github.hegaojian:JetpackMvvm:1.2.7") {
        exclude(group = "com.kunminx.archi")
        exclude(group = "me.jessyan")
    }
    api("com.kunminx.arch:unpeek-livedata:7.8.0")
    api("com.github.JessYanCoding:RetrofitUrlManager:v1.4.0")
    api("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

}

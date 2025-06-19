plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.kapt)
    id("maven-publish")
}

android {
    namespace = fwlibs.versions.fwNameSpace.get()
    compileSdk = fwlibs.versions.comileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-Xjvm-default=all-compatibility"
    }

    defaultConfig {
        minSdk = fwlibs.versions.minSdk.get().toInt()
        manifestPlaceholders["AMAP_API_KEY"] = "\${AMAP_API_KEY}"
        manifestPlaceholders["BUGLY_APPID"] = "\${BUGLY_APPID}"
        manifestPlaceholders["BUGLY_ENABLE_DEBUG"] = "\${BUGLY_ENABLE_DEBUG}"
        manifestPlaceholders["PGYER_API_KEY"] = "\${PGYER_API_KEY}"
        manifestPlaceholders["PGYER_APP_KEY"] = "\${PGYER_APP_KEY}"
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
            groupId = fwlibs.versions.fwGroupId.get()
            artifactId = fwlibs.versions.fwArtifactId.get()
            version = fwlibs.versions.fwVer.get()

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    coreLibraryDesugaring(fwlibs.desugar.jdk.libs)
    api(fileTree("libs") { include("*.jar", "*.aar") })
    api(fwlibs.kotlin.stdlib.jdk8)
    api(fwlibs.kotlinx.coroutines.core)
    api(fwlibs.kotlinx.coroutines.android)
    api(fwlibs.kotlinx.coroutines.reactive)
    api(fwlibs.kotlinx.coroutines.rx3)

    //谷歌库
    api(fwlibs.core.ktx)
    api(fwlibs.appcompat)
    api(fwlibs.recyclerview)
    api(fwlibs.material)
    api(fwlibs.core.splashscreen)
    api(fwlibs.navigation.fragment)
    api(fwlibs.navigation.ui)
    api(fwlibs.navigation.compose)
    api(fwlibs.navigation.dynamic.features.fragment)
    api(fwlibs.room.runtime)
    api(fwlibs.room.rxjava3)
    api(fwlibs.room.ktx)
    api(fwlibs.sqlite.ktx)
    api(fwlibs.lifecycle.viewmodel.ktx)
    api(fwlibs.lifecycle.viewmodel.compose)
    api(fwlibs.lifecycle.livedata.ktx)
    api(fwlibs.lifecycle.viewmodel.savedstate)
    api(fwlibs.lifecycle.common.java8)
    api(fwlibs.lifecycle.service)
    api(fwlibs.lifecycle.process)
    api(fwlibs.lifecycle.reactivestreams.ktx)
    api(fwlibs.lifecycle.runtime.ktx)
    api(fwlibs.androidx.activity.compose)
    api(platform(fwlibs.androidx.compose.bom))
    api(fwlibs.androidx.ui)
    api(fwlibs.androidx.ui.graphics)
    api(fwlibs.androidx.material3)
    api(fwlibs.androidx.ui.tooling.preview)
    api(fwlibs.guavaListenablefuture.avoidConflict)
    debugApi(fwlibs.androidx.ui.tooling)
    debugApi(fwlibs.androidx.ui.test.manifest)

    //retrofit2
    api(fwlibs.retrofit)
    api(fwlibs.retrofit.converter.jackson)
    api(fwlibs.retrofit.adapter.rxjava3)
    api(fwlibs.persistentCookieJar)
    api(fwlibs.numeron.http)
    //RxJava的库
    api(fwlibs.rxandroid)
    api(fwlibs.rxjava)
    api(fwlibs.rxbinding.core)
    api(fwlibs.rxbinding.appcompat)
    api(fwlibs.rxbinding.recyclerview)
    api(fwlibs.rxbinding.material)
    api(fwlibs.rxlifecycle)
    api(fwlibs.rxlifecycle.android)
    api(fwlibs.rxlifecycle.components)
    api(fwlibs.rxlifecycle.kotlin)
    api(fwlibs.rxlifecycle.android.lifecycle)
    api(fwlibs.rxlifecycle.android.lifecycle.kotlin)
    api(fwlibs.gson)
    //EVENT
    api(fwlibs.liveEventBus)
    //高德地图
    api(fwlibs.amap)
    // sql-cipher支持
    api(fwlibs.androidDatabaseSqlcipher)
    //json解析器
    api(fwlibs.jackson.core)
    api(fwlibs.jackson.databind)
    api(fwlibs.jackson.annotations)
    api(fwlibs.jackson.module.kotlin)
    api(fwlibs.jackson.datatype.jsr310)
    //Apache的常用库
    api(fwlibs.commons.codec)
    api(fwlibs.commons.net)
    api(fwlibs.commons.collections4)
    api(fwlibs.commons.lang3)
    //软引用的handler
    api(fwlibs.androidWeakHandler)
    //图片加载
    api(fwlibs.coil.network)
    api(fwlibs.coil.compose)
    api(fwlibs.glide)
    //图片显示PhotoView
    api(fwlibs.photoView)
    //图片触摸库
    api(fwlibs.imagezoom)
    //图片显示subsamplingImageView
    api(fwlibs.subsamplingScaleImageView)
    //图片选择器
    api(fwlibs.pictureselector)
    api(fwlibs.pictureselector.compress)
    api(fwlibs.pictureselector.ucrop)
    api(fwlibs.pictureselector.camerax)
    //综合工具库utilcode
    api(fwlibs.utilcodex)
    //RecyclerView综合适配器
    api(fwlibs.baseRecyclerViewAdapterHelper4)
    //material design日期加时间选择器
    api(fwlibs.sublimePicker)
    //权限提示
    api(fwlibs.easyPermissionDialog)
    //BUGLY
    api(fwlibs.crashreport)
    //数字进度条
    api(fwlibs.numberProgressBar)
    //sharedpreferences
    api(fwlibs.kotpref)
    api(fwlibs.kotpref.enumSupport)
    api(fwlibs.kotpref.gsonSupport)
    api(fwlibs.kotpref.livedataSupport)
    api(fwlibs.kotpref.preferenceScreenDsl)
    api(fwlibs.ksprefs)
    //Permission
    api(fwlibs.xxPermissions)
    //Dialog
    api(fwlibs.dialogX)
    //刷新库
    api(fwlibs.refresh.layoutKernel)
    api(fwlibs.refresh.headerClassics)
    api(fwlibs.refresh.footerClassics)
    //koin
    api(fwlibs.koin.core)
    api(fwlibs.koin.android)
    api(fwlibs.koin.android.compat)
    api(fwlibs.koin.androidx.navigation)
    api(fwlibs.koin.androidx.compose)
    //viewBinding快速库
    api(fwlibs.binding)
    api(fwlibs.viewbinding.ktx)
    //FlexibleDivider
    api(fwlibs.flexibleDivider)
    //快速ActivityLauncher
    api(fwlibs.activityResultLauncher)
    //mvvm框架
    api(fwlibs.jetpackMvvm)

}

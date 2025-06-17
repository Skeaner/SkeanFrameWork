import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.safeargs)
    alias(libs.plugins.compose.compiler)
}

allOpen {
    annotation("org.androidannotations.api.KotlinOpen")
}

android {
    compileSdk = libs.versions.comileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-Xjvm-default=all-compatibility"
    }
    signingConfigs {
        //todo 修改签名文件
        create("config") {
            keyAlias = "test"
            keyPassword = "123456"
            storeFile = file("test.jks")
            storePassword = "123456"
        }
    }

    var vCode = libs.versions.appVerCode.get().toInt()
    var vName = libs.versions.appVerName.get()
    //判断是否发布任务
    val isRelease = gradle.startParameter.taskRequests.any {
        it.args.any { s ->
            s.endsWith("Release") && !s.endsWith("BetaRelease") && !s.endsWith("DevelopRelease")
        }
    }
    //发布的环境自动增加版本号
    if (isRelease) {
        vCode += 1
        val vNameParts = vName.split(".")
        var vNamePart1 = vNameParts[0].toInt()
        var vNamePart2 = vNameParts[1].toInt()
        var vNamePart3 = vNameParts[2].toInt()
        if (++vNamePart3 > 9) {
            vNamePart3 = 0
            if (++vNamePart2 > 9) {
                vNamePart2 = 0
                ++vNamePart1
            }
        }
        vName = "$vNamePart1.$vNamePart2.$vNamePart3"
        val file = project.rootProject.file("/gradle/libs.versions.toml")
        var text = file.readText()
        text = text.replace(Regex("appVerCode.*"), "appVerCode = \"$vCode\"")
        text = text.replace(Regex("appVerName.*"), "appVerName = \"$vName\"")
        file.writeText(text)
    }

    defaultConfig {
        //todo 修改程序名字相关
        val tag = "FrameworkExample"
        namespace = "me.skean.framework.example"
        applicationId = "me.skean.framework.example"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = vCode
        versionName = vName
        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }

        kapt {
            arguments {
                arg("resourcePackageName", "$applicationId")
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("androidManifestFile", "$projectDir/src/main/AndroidManifest.xml")
            }
        }
        applicationVariants.all {
            //修改打包的apk名字
            outputs.all {
                val dateStr = SimpleDateFormat("yyMMddHHmm", Locale.getDefault()).format(Date())
                val buildTypeName: String = buildType.name
                val versionTag = buildTypeName.get(0)
                (this as BaseVariantOutputImpl).outputFileName =
                    "$tag(${flavorName})-${vName}${versionTag}(b${vCode})-${dateStr}.apk"

            }

        }
        //todo 通用的配置
        manifestPlaceholders["rawApplicationId"] = "$applicationId"
        manifestPlaceholders["applicationIcon"] = "@drawable/ic_launcher"
        buildConfigField("String", "APP_TAG", "\"$tag\"")
        buildConfigField("boolean", "EXTERNAL_DB", "true")
        buildConfigField("boolean", "USE_FILE_LOGGER", "false")
    }
    buildTypes {
        //todo 发布与开发的配置
        release {
            signingConfig = signingConfigs.getByName("config")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        debug {
            signingConfig = signingConfigs.getByName("config")
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    flavorDimensions.add("default")
    productFlavors {
        //todo 渠道打包配置

        // 发布环境
        create("production") {
            dimension = "default"
            resValue("string", "app_name", "SFW")
            manifestPlaceholders["AMAP_API_KEY"] = "b46c4981b8e07d4d613867e03c753f4b"
            manifestPlaceholders["BUGLY_APPID"] = "47b7b8213f"
            manifestPlaceholders["BUGLY_ENABLE_DEBUG"] = "false"
            manifestPlaceholders["PGYER_API_KEY"] = "45de93e56eb3c62c53289ac52e8524c4"
            manifestPlaceholders["PGYER_APP_KEY"] = "88470f142b1734c12f5bf9f9b3303cea"

        }
        // 测试环境
        create("beta") {
            dimension = "default"
            applicationId = "${defaultConfig.applicationId}.beta"
            resValue("string", "app_name", "SFW-beta")
            manifestPlaceholders["applicationIcon"] = "@drawable/ic_launcher_beta"
            manifestPlaceholders["AMAP_API_KEY"] = "b46c4981b8e07d4d613867e03c753f4b"
            manifestPlaceholders["BUGLY_APPID"] = "47b7b8213f"
            manifestPlaceholders["BUGLY_ENABLE_DEBUG"] = "true"
            manifestPlaceholders["PGYER_API_KEY"] = "8c9438bcab1415568aa14299358af9f7"
            manifestPlaceholders["PGYER_APP_KEY"] = "88470f142b1734c12f5bf9f9b3303cea"

        }
        // 开发
        create("develop") {
            dimension = "default"
            applicationId = "${defaultConfig.applicationId}.dev"
            resValue("string", "app_name", "SFW-dev")
            manifestPlaceholders["applicationIcon"] = "@drawable/ic_launcher_dev"
            manifestPlaceholders["AMAP_API_KEY"] = "b46c4981b8e07d4d613867e03c753f4b"
            manifestPlaceholders["BUGLY_APPID"] = "47b7b8213f"
            manifestPlaceholders["BUGLY_ENABLE_DEBUG"] = "true"
            manifestPlaceholders["PGYER_API_KEY"] = "8c9438bcab1415568aa14299358af9f7"
            manifestPlaceholders["PGYER_APP_KEY"] = "88470f142b1734c12f5bf9f9b3303cea"

        }
    }
    sourceSets.getByName("main") {
        assets.srcDirs("$projectDir/schemas")
        jniLibs.srcDirs("libs")
        res.srcDirs("src/main/res/")
    }
    lint.abortOnError = false
    viewBinding.isEnabled = true
    dataBinding.enable = true
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,DEPENDENCIES,NOTICE,LICENSE,LICENSE.txt,NOTICE.txt,rxjava.properties,androidx.exifinterface_exifinterface.version}"
            excludes += "META-INF/proguard/androidx-annotations.pro"
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
//    implementation("com.github.Skeaner:SkeanFrameWork:2.3.6")
    implementation(project(":SkeanFrameWork"))
    implementation(fileTree("libs") { include("*.jar", "*.aar") })
    ksp(libs.room.compiler)
}

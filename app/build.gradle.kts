import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-allopen")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
}

allOpen {
    annotation("org.androidannotations.api.KotlinOpen")
}

android {
    compileSdk = 31
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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

    var vCode = 1
    var vName = "1.0.0"
    //判断是否发布任务
    val isRelease = gradle.startParameter.taskRequests.any {
        it.args.any { s ->
            s.endsWith("Release") && !s.endsWith("BetaRelease") && !s.endsWith("DevelopRelease")
        }
    }
    //发布的环境自动增加版本号
    if (isRelease) {
        val oldVCode = vCode
        val oldVName = vName
        vCode += 1
        val vNameParts = vName.split(".")
        var vNamePart1 = Integer.parseInt(vNameParts[0])
        var vNamePart2 = Integer.parseInt(vNameParts[1])
        var vNamePart3 = Integer.parseInt(vNameParts[2])
        vNamePart3 += 1
        if (vNamePart3 > 9) {
            vNamePart3 = 0
            vNamePart2 += 1
            if (vNamePart2 > 9) {
                vNamePart2 = 0
                vNamePart1 += 1
            }
        }
        vName = "$vNamePart1.$vNamePart2.$vNamePart3"
        val file = project.rootProject.file("/app/build.gradle.kts")
        var text = file.readText()
        text = text.replace("var vCode = $oldVCode", "var vCode = $vCode")
        text = text.replace("var vName = \"${oldVName}\"", "var vName = \"$vName\"")
        file.writeText(text)
    }

    defaultConfig {
        //todo 修改程序ID
        applicationId = "me.skean.framework.example"
        minSdk = 21
        targetSdk = 28
        versionCode = vCode
        versionName = vName
        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("x86")
        }

        kapt {
            arguments {
                arg("eventBusIndex", "${applicationId}.EventBusIndex")
                arg("resourcePackageName", "$applicationId")
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("androidManifestFile", "$projectDir/src/main/AndroidManifest.xml")
            }
        }
        applicationVariants.all {
            //修改打包的apk名字
            outputs.all {
                val dateStr = SimpleDateFormat("MMddHHmm", Locale.getDefault()).format(Date())
                val shortAppId = "${defaultConfig.applicationId}".substring("${defaultConfig.applicationId}".lastIndexOf(".") + 1)
                val buildTypeName: String = buildType.name
                val versionTag = buildTypeName.get(0)
                (this as BaseVariantOutputImpl).outputFileName =
                    "${shortAppId}-${vName}${versionTag}(b${vCode})-${dateStr}.apk"

            }
            //删除自动生成的output.json
//            assembleProvider.get().doLast {
//                val jsonFilePath  = "${packageApplicationProvider.get().outputDirectory.get()}/output-metadata.json"
//                System.out.println("删除文件:$jsonFilePath")
//                delete(jsonFilePath)
//            }
        }
        manifestPlaceholders["rawApplicationId"] = "$applicationId"
        manifestPlaceholders["applicationIcon"] = "@drawable/ic_launcher"
    }
    buildTypes {
        //todo 通用的配置
        val appTag = "\"" + "${defaultConfig.applicationId}".substring("${defaultConfig.applicationId}".lastIndexOf(".") + 1) + "\""
        val useExternalDatabase = true
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("config")
            buildConfigField("String", "APP_TAG", appTag)
            buildConfigField("boolean", "EXTERNAL_DB", "$useExternalDatabase")
        }
        debug {
            signingConfig = signingConfigs.getByName("config")
            buildConfigField("String", "APP_TAG", appTag)
            buildConfigField("boolean", "EXTERNAL_DB", "$useExternalDatabase")
        }
    }
    flavorDimensions.add("default")
    productFlavors {
        //todo 渠道打包配置配置
        // 发布环境
        create("production") {
            dimension = "default"
            resValue("string", "app_name", "SFW")
            buildConfigField("String", "BUGLY_APPID", "\"ccced6668f\"")
            buildConfigField("boolean", "LOG_TO_FILE", "false")
            buildConfigField("boolean", "IS_INTRANET", "false")
        }
        // 测试环境
        create("beta") {
            dimension = "default"
            applicationId = "${defaultConfig.applicationId}.dev"
            resValue("string", "app_name", "SFW-beta")
            buildConfigField("String", "BUGLY_APPID", "\"ccced6668f\"")
            buildConfigField("boolean", "LOG_TO_FILE", "false")
            buildConfigField("boolean", "IS_INTRANET", "false")
            manifestPlaceholders["applicationIcon"] = "@drawable/ic_launcher_beta"
        }
        // 开发
        create("develop") {
            dimension = "default"
            applicationId = "${defaultConfig.applicationId}.dev"
            resValue("string", "app_name", "SFW-dev")
            buildConfigField("String", "BUGLY_APPID", "\"ccced6668f\"")
            buildConfigField("boolean", "LOG_TO_FILE", "false")
            buildConfigField("boolean", "IS_INTRANET", "false")
            manifestPlaceholders["applicationIcon"] = "@drawable/ic_launcher_dev"
        }
    }
    sourceSets.getByName("main") {
        java.srcDirs("build/generated/source/greendao")
        assets.srcDirs("$projectDir/schemas")
        jniLibs.srcDirs("libs")
        res.srcDirs("src/main/res/")
    }
    lint {
        abortOnError = false
    }
    viewBinding {
        isEnabled = true
    }
    dataBinding {
        isEnabled = true
    }
    packagingOptions {
        resources.excludes.apply {
            add("META-INF/DEPENDENCIES")
            add("META-INF/NOTICE")
            add("META-INF/LICENSE")
            add("META-INF/LICENSE.txt")
            add("META-INF/NOTICE.txt")
            add("META-INF/rxjava.properties")
            add("META-INF/androidx.exifinterface_exifinterface.version")
            add("META-INF/proguard/androidx-annotations.pro")
        }

    }
}

repositories {
    flatDir { dirs("libs") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    mavenLocal()
}


dependencies {
//    implementation("com.github.Skeaner:SkeanFrameWork:2.2.0")
    implementation (project( ":SkeanFrameWork"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${rootProject.extra["kotlinVersion"]}")
    implementation(fileTree("libs") { include("*.jar") })
    kapt("com.github.permissions-dispatcher:permissionsdispatcher-processor:4.8.0")
    kapt("org.greenrobot:eventbus-annotation-processor:3.1.1")
    kapt("com.jakewharton:butterknife-compiler:10.2.0")
    kapt("androidx.room:room-compiler:2.3.0")
}

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties
import java.io.FileInputStream

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
    compileSdk = 32
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
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

    val properties = Properties()
    properties.load(FileInputStream(project.rootProject.file("local.properties")))
    val VERSION_CODE = properties.getProperty("VERSION_CODE")
    val VERSION_NAME = properties.getProperty("VERSION_NAME")
    var vcode = Integer.parseInt(VERSION_CODE)
    var vname = VERSION_NAME
    val isRelease = gradle.startParameter.taskRequests.any {
        it.args.any { it.endsWith("Release") }
    }
    if (isRelease) {
        vcode += 1
        val versionNameGroup = VERSION_NAME.split("\\.")
        var versionNamePart1 = Integer.parseInt(versionNameGroup[0])
        var versionNamePart2 = Integer.parseInt(versionNameGroup[1])
        var versionNamePart3 = Integer.parseInt(versionNameGroup[2])
        versionNamePart3 += 1
        if (versionNamePart3 > 9) {
            versionNamePart3 = 0
            versionNamePart2 += 1
            if (versionNamePart2 > 9) {
                versionNamePart2 = 0
                versionNamePart1 += 1
            }
        }
        vname = "$versionNamePart1.$versionNamePart2.$versionNamePart3"
//        ant.propertyfile(file: "../gradle.properties") {
//            entry(key: "VERSION_CODE", value: vcode)
//            entry(key: "VERSION_NAME", value: vname)
//        }
    }

    defaultConfig {
        //todo 修改程序ID
        applicationId = "me.skean.framework.example"
        minSdk = 21
        targetSdk = 28
        versionCode = vcode
        versionName = vname
        multiDexEnabled = true
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
                (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                    "${shortAppId}-${vname}${versionTag}(b${vcode})-${dateStr}.apk"

            }
            //删除自动生成的output.json
//            variant.assemble.doLast {
//                variant.outputs.each { output ->
//                    delete "${output.outputFile.parent}/output-metadata.json"
//                }
//            }
        }
        manifestPlaceholders["rawApplicationId"] = "$applicationId"
        manifestPlaceholders["applicationIcon"] = "@drawable/ic_launcher"
    }
    buildTypes {
        //todo 通用的配置
        val appTag = "\"" + "${defaultConfig.applicationId}".substring("${defaultConfig.applicationId}".lastIndexOf(".") + 1) + "\""
        val useExternalDatabase = true
        getByName("release") {
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
    flavorDimensions("default")
    productFlavors {
        //todo 渠道打包配置配置
        // 发布环境
        create("production") {
            dimension = "default"
            resValue("string", "app_name", "SFW")
            buildConfigField("String", "BUGLY_APPID", "\"ccced6668f\"")
            buildConfigField("boolean", "LOG_TO_FILE", "true")
            buildConfigField("boolean", "IS_INTRANET", "true")
        }
        // 测试环境
        create("beta") {
            dimension = "default"
            applicationId = "${defaultConfig.applicationId}.dev"
            resValue("string", "app_name", "SFW-beta")
            buildConfigField("String", "BUGLY_APPID", "\"ccced6668f\"")
            buildConfigField("boolean", "LOG_TO_FILE", "true")
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
    lintOptions {
        isAbortOnError = false
    }
    viewBinding {
        isEnabled = true
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/NOTICE")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/rxjava.properties")
        exclude("META-INF/androidx.exifinterface_exifinterface.version")
        exclude("META-INF/proguard/androidx-annotations.pro")
    }
}

repositories {
    flatDir {
        dirs("libs")
    }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    mavenLocal()
}


dependencies {
    implementation("com.github.Skeaner:SkeanFrameWork:2.2.0")
//    implementation project(path: ":SkeanFrameWork")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${rootProject.extra["kotlin_version"]}")
    implementation(fileTree("libs") { include("*.jar") })
    kapt("com.github.permissions-dispatcher:permissionsdispatcher-processor:4.8.0")
    kapt("org.greenrobot:eventbus-annotation-processor:3.1.1")
    kapt("com.jakewharton:butterknife-compiler:10.2.0")
    kapt("androidx.room:room-compiler:2.3.0")
}

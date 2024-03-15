// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion = "1.9.20"
    extra.apply {
        set("kotlinVersion",kotlinVersion)
    }
    repositories {
        mavenCentral()
        google()
        maven { url=uri("https://jitpack.io") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.1")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { url=uri("https://jitpack.io") }
        maven { url =uri("https://raw.githubusercontent.com/PGYER/analytics/master/") }
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}

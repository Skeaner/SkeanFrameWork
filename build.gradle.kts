
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion = "2.0.20"
    extra.apply {
        set("kotlinVersion",kotlinVersion)
    }
    repositories {
        mavenCentral()
        google()
        maven { url=uri("https://jitpack.io") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.0.20-1.0.25")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.0")
        classpath ("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { url=uri("https://jitpack.io") }
        maven { url = uri("https://raw.githubusercontent.com/Skeaner/MavenStore/main/") }
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}

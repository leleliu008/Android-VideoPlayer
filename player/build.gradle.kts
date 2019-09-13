plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(28)

    defaultConfig {
//        minSdkVersion(16)
//        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0.0"
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDir("src/main/libs")
            java.srcDirs("src/main/kotlin")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    lintOptions {
        isAbortOnError = false
    }

    compileOptions {
        //使用JAVA8语法解析
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(kotlin("stdlib", rootProject.extra["kotlinVersion"] as String))

    //https://github.com/ReactiveX/RxAndroid
    api("io.reactivex.rxjava2:rxandroid:2.1.0")
    //https://github.com/ReactiveX/RxJava
    api("io.reactivex.rxjava2:rxjava:2.2.4")

    //七牛视频播放器
    //https://github.com/pili-engineering/PLDroidPlayer
    api("com.fpliu:pldroid-player:2.1.6@aar")

    //https://github.com/leleiu008/Android-BaseUI
    api("com.fpliu:Android-BaseUI:2.0.12")
    api("com.fpliu:Android-CustomDimen:1.0.0")
}

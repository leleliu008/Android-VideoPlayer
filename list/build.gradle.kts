plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(28)

    defaultConfig {
//        minSdkVersion(18)
//        targetSdkVersion(22)
        versionCode = 1
        versionName = "1.0.0"
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDir("src/main/libs")
            java.srcDirs("src/main/kotlin")
        }
    }

    lintOptions {
        isCheckReleaseBuilds = false
        isAbortOnError = false
        disable("ContentDescription", "HardcodedText")
    }

    compileOptions {
        //使用JAVA8语法解析
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(project(":player"))

    //图片异步加载和展示框架：https://github.com/bumptech/glide
//    api("com.github.bumptech.glide:glide:4.8.0")
//    kapt("com.github.bumptech.glide:compiler:4.8.0")

    //https://github.com/wasabeef/glide-transformations
    //https://bintray.com/wasabeef/maven/glide-transformations
//    api("jp.wasabeef:glide-transformations:4.0.1")
    // If you want to use the GPU Filters
    //api 'jp.co.cyberagent.android.gpuimage:gpuimage-library:1.4.1'

    //https://github.com/uber/AutoDispose
    //autodispose-android has a ViewScopeProvider for use with Android View classes.
    api("com.uber.autodispose:autodispose-android-ktx:1.1.0")

    //HTTP WebSocket客户端
    //https://github.com/square/okhttp
    api("com.squareup.okhttp3:okhttp:3.12.1")

    //JSON解析：https://github.com/google/gson
    //api("com.google.code.gson:gson:2.8.5")

    //https://github.com/leleliu008/gson
    //使用经过修改的GSON，不使用官方的GSON的原因是：官方的GSON在反序列化的时候不对null进行过滤，或导致Kotlin的NullSafety特性遭到破坏
    //后期会使用Transform API修改，不侵入代码
    api("com.fpliu:gson:2.8.7")

    //https://github.com/square/retrofit/tree/master/retrofit-converters/gson
    api("com.squareup.retrofit2:converter-gson:2.4.0") {
        exclude("com.google.code.gson", "gson")
        //将内置的retrofit2依赖去掉，因为我们要使用自己扩展的retrofit2
        exclude("com.squareup.retrofit2", "retrofit")
    }

    //https://github.com/square/retrofit/tree/master/retrofit-adapters/rxjava2
    api("com.squareup.retrofit2:adapter-rxjava2:2.4.0") {
        //将内置的retrofit2依赖去掉，因为我们要使用自己扩展的retrofit2
        exclude("com.squareup.retrofit2", "retrofit")
    }

    // 下面这些源代码的地址：https://github.com/leleliu008
    // 包托管在jCenter：https://bintray.com/fpliu/newton

    api("com.fpliu:RetrofitHelper:1.0.1") {
        //将内置的retrofit2依赖去掉，因为我们要使用自己扩展的retrofit2
        exclude("com.squareup.retrofit2", "retrofit")
    }
    api("com.fpliu:retrofit:2.4.0")
    api("com.fpliu:Android-BaseUI:2.0.12")
//    api("com.fpliu:Android-CustomDialog:1.0.1")
//    api("com.fpliu:Android-RxCustomDialog:2.0.0")
    api("com.fpliu:Android-CustomDrawable:1.0.0")
    api("com.fpliu:Android-CustomDimen:1.0.0")
//    api("com.fpliu:Android-CustomAnimation:2.0.0")
    api("com.fpliu:Android-EffectTextView:1.0.0")
    api("com.fpliu:Android-RecyclerViewHelper:2.0.2")
    api("com.fpliu:Android-List:2.0.1")
}

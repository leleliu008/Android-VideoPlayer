buildscript {
    var kotlinVersion: String by extra
    kotlinVersion = "1.3.50"

    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        //dl.google.com 在北京海淀区有CND节点，因此不必用镜像
        google()
    }

    dependencies {
        //Android编译插件
        //https://developer.android.com/studio/releases/gradle-plugin
        classpath("com.android.tools.build:gradle:7.0.0")

        //Kotlin编译的插件
        //http://kotlinlang.org/docs/reference/using-gradle.html
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}

allprojects {
    repositories {
        google()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://gitee.com/fpliu/maven-repo/raw/master") }
        //https://repomanage.rdc.aliyun.com
        maven {
            credentials {
                username = "VWlreR"
                password = "IUi3HSNf0L"
            }
            url = uri("https://repo.rdc.aliyun.com/repository/66536-release-dY2ItX/")
        }
        maven { url = uri("https://jitpack.io") }
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}

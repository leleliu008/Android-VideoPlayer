include(":app")
include(":list")
include(":player")

pluginManagement {
    repositories {
        //https://maven.aliyun.com/mvn/view
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    }
}

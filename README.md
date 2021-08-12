# Android-VideoPlayer
A Video Player for Android

![效果1](images/1.png)
![效果2](images/2.png)

![效果3](images/3.png)


### Libraries used by this project
1. [RxJava2](https://github.com/ReactiveX/RxJava)
2. [RxAndroid](https://github.com/ReactiveX/RxAndroid)
3. [RxBinding](https://github.com/JakeWharton/RxBinding)
4. [OKHTTP](https://github.com/square/okhttp)
5. [Retrofit](https://github.com/square/retrofit)
6. [GSON](https://github.com/google/gson)
7. [AutoDispose](https://github.com/uber/AutoDispose)
8. [七牛视频播放器](https://github.com/pili-engineering/PLDroidPlayer)
9. [友盟统计](https://developer.umeng.com/sdk/android)

### tools used by this project
1. [Walle](https://github.com/Meituan-Dianping/walle) 
2. [LeakCanary](https://github.com/square/leakcanary)
3. [AndResGuard](https://github.com/shwenzhang/AndResGuard)
4. [ProGuard](http://blog.fpliu.com/it/software/ProGuard)
5. [ReDex](http://blog.fpliu.com/it/software/ReDex)
6. [360加固保](http://blog.fpliu.com/it/software/360加固保)
7. [libwebp](http://blog.fpliu.com/it/software/libwebp)
8. [7zip](http://blog.fpliu.com/it/software/p7zip)
9. [androidx](https://github.com/leleliu008/androidx)

### print logcat logs
```
adb logcat | grep "VP_"
```

### print Uncaught Exception logs
```
adb shell cat /sdcard/com.fpliu.newton.video.player.sample/log/UncaughtException.log
```

### build release apk using [androidx](https://github.com/leleliu008/androidx)
```
androidx build --project-dir=Android-VideoPlayer --build-type=release --webp --resguard --jiagu --channels=zip
```

### build debug apk using [androidx](https://github.com/leleliu008/androidx)
```
androidx build --project-dir=Android-VideoPlayer --build-type=debug   --webp --resguard --jiagu --channels=zip
```

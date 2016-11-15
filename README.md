#AndroidTemplateApp

This contains a template of a base android app. Below are the modules that are included by default and also recommended, depending on the features of the app.

## Required Android Studio Plugins

- Lombok

To install navigate to `Preferences -> Plugins -> Browse Repositories` and enter Lombok in the search field

##  Set JAVA8_HOME variable

Retrolambda library is included by default (backport of Java 8's lambda syntax), so you must have environment variable JAVA8_HOME set to java 8 home's location. You may need to download Java 8 if you don't already have it.

Add these to your `~/.zshrc` or `~/bash_profile`:

```bash
# setting java home
export JAVA8_HOME="$(/usr/libexec/java_home -v 1.8)"
export JAVA7_HOME="$(/usr/libexec/java_home -v 1.7)"
export JAVA6_HOME="$(/usr/libexec/java_home -v 1.6)"
```

#Gradle Imports Included by Default
-Timber (Logging)

-Retrofit with okhttp (API Client + maps models to API calls)

-Otto (event bus)

-Butterknife (View injection)

-GSON (Object serialization/deserialization)

-Dagger (Object/Dependency injection)

-Crashlytics (part of Fabric, does Crash reporting, beta builds)

-Scalpel & Debug Drawer (Swipe left at intro screen to see configurable options)

#Optional Recommended Gradle Imports
-Edomodo cropper (crops images with decent UI)

compile 'com.edmodo:cropper:1.0.1'

-Makeramen RoundedImageView (Allows you to round edges in imageviews)

compile 'com.makeramen:roundedimageview:2.1.0'

-Facebook/Twitter

compile 'com.facebook.android:facebook-android-sdk:4.4.0'

compile 'org.twitter4j:twitter4j-core:4.0.4'

compile 'org.twitter4j:twitter4j-async:4.0.4’

compile('com.twitter.sdk.android:twitter:1.6.0@aar') {
    transitive = true;
}

-Chrisjenx Calligraphy  (Font management in XML)

compile 'uk.co.chrisjenx:calligraphy:2.1.0'

-Picasso/Android Image Loader (All In One image loader with rich features)

compile 'com.nostra13.universalimageloader:universal-image-loader:+’

compile 'com.squareup.picasso:picasso:2.5.0'

-Rahatarmanahmed CircularProgressView

compile 'com.github.rahatarmanahmed:circularprogressview:2.3.+'

-Pnikosis materialish-progress (Google-esque fullscreen loading view)

compile 'com.pnikosis:materialish-progress:1.4'

-Intercom (Customer service + FAQs)

compile ('io.intercom.android:intercom-sdk:1.0.0@aar') {
    transitive = true
}

-Android Saripaar (Automatic form validation framework)

compile 'com.mobsandgeeks:android-saripaar:2.0-SNAPSHOT'



package com.shiny.raisingmoney

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

/**
 * app / library 모듈이 공통으로 갖는 Android 설정.
 */
internal fun configureAndroidCommon(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk {
            version = release(36) {
                minorApiLevel = 1
            }
        }
        defaultConfig.minSdk = 24
        compileOptions.sourceCompatibility = JavaVersion.VERSION_11
        compileOptions.targetCompatibility = JavaVersion.VERSION_11
    }
}

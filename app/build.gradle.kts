plugins {
    alias(libs.plugins.raisingmoney.android.application.compose)
    alias(libs.plugins.raisingmoney.hilt)
    alias(libs.plugins.module.graph.assert)
}

// 모듈 의존성 규칙. app에서 도달 가능한 모든 모듈 그래프를 검사하며
// `./gradlew check`(assertModuleGraph)에서 위반 시 빌드가 실패한다.
moduleGraphAssert {
    restricted = arrayOf(
        ":core:.* -X> :feature:.*", // core는 feature를 모른다 (역방향 금지)
        ":core:.* -X> :app",
        ":feature:.* -X> :feature:.*", // feature끼리 의존 금지
        ":feature:.* -X> :app",
        ":core:designsystem -X> :.*", // designsystem은 최하단: 어떤 모듈에도 의존하지 않는다
    )
}

android {
    namespace = "com.shiny.raisingmoney"

    defaultConfig {
        applicationId = "com.shiny.raisingmoney"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":feature:transaction"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

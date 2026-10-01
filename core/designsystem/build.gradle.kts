plugins {
    alias(libs.plugins.raisingmoney.android.library.compose)
}

android {
    namespace = "com.shiny.raisingmoney.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material3)
}

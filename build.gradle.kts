import com.diffplug.gradle.spotless.SpotlessExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.spotless) apply false
}

// root 프로젝트 자신의 *.kts(build.gradle.kts, settings.gradle.kts)는
// subprojects{}에 포함되지 않으므로 별도로 적용한다.
apply(plugin = "com.diffplug.spotless")

configure<SpotlessExtension> {
    format("kts") {
        target("*.kts")
        endWithNewline()
    }
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    configure<SpotlessExtension> {
        kotlin {
            target("src/**/*.kt")
            targetExclude("**/build/**/*.kt")
            ktlint(libs.versions.ktlint.get()).editorConfigOverride(
                mapOf(
                    "android" to "true",
                    "ktlint_standard_final-newline" to "disabled",
                ),
            )
        }
        format("kts") {
            target("*.kts")
            endWithNewline()
        }
    }
}

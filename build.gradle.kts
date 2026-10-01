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

val ktlintEditorConfig = mapOf(
    "android" to "true",
    "ktlint_standard_final-newline" to "disabled",
)

// root 프로젝트 자신의 *.kts(build.gradle.kts, settings.gradle.kts)와
// included build인 build-logic은 subprojects{}에 포함되지 않으므로 별도로 적용한다.
apply(plugin = "com.diffplug.spotless")

configure<SpotlessExtension> {
    kotlin {
        target("build-logic/**/src/**/*.kt")
        targetExclude("build-logic/**/build/**")
        ktlint(libs.versions.ktlint.get()).editorConfigOverride(ktlintEditorConfig)
    }
    format("kts") {
        target("*.kts", "build-logic/**/*.kts")
        targetExclude("build-logic/**/build/**")
        endWithNewline()
    }
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    configure<SpotlessExtension> {
        kotlin {
            target("src/**/*.kt")
            targetExclude("**/build/**/*.kt")
            ktlint(libs.versions.ktlint.get()).editorConfigOverride(ktlintEditorConfig)
        }
        format("kts") {
            target("*.kts")
            endWithNewline()
        }
    }
}

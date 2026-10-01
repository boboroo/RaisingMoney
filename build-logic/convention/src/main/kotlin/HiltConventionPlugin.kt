import com.shiny.raisingmoney.library
import com.shiny.raisingmoney.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Hilt 의존성과 KSP 컴파일러를 추가한다.
 * @HiltAndroidApp / @AndroidEntryPoint 바이트코드 변환이 필요한 app 모듈에만 Hilt Gradle 플러그인을 적용한다.
 */
class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.withPlugin("com.android.application") {
                pluginManager.apply("com.google.dagger.hilt.android")
            }

            dependencies {
                add("implementation", libs.library("hilt-android"))
                add("ksp", libs.library("hilt-android-compiler"))
            }
        }
    }
}

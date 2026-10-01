import com.shiny.raisingmoney.library
import com.shiny.raisingmoney.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * feature:* 모듈. Screen + ViewModel(Hilt) 구성에 필요한 설정과 의존성을 한 번에 적용한다.
 * CLAUDE.md 2.2에 따라 feature는 core:designsystem(추후 core:ui)에 의존한다.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("raisingmoney.android.library.compose")
            pluginManager.apply("raisingmoney.hilt")

            dependencies {
                add("implementation", project(":core:designsystem"))

                add("implementation", libs.library("androidx-core-ktx"))
                add("implementation", libs.library("androidx-compose-material3"))
                add("implementation", libs.library("androidx-compose-material-icons-core"))
                add("implementation", libs.library("androidx-lifecycle-viewmodel-compose"))
                add("implementation", libs.library("androidx-lifecycle-runtime-compose"))
                add("implementation", libs.library("androidx-hilt-lifecycle-viewmodel-compose"))
            }
        }
    }
}

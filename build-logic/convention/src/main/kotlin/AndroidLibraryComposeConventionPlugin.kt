import com.android.build.api.dsl.LibraryExtension
import com.shiny.raisingmoney.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * Compose를 사용하는 Android 라이브러리 모듈.
 */
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("raisingmoney.android.library")
            configureAndroidCompose(extensions.getByType<LibraryExtension>())
        }
    }
}

import com.android.build.api.dsl.ApplicationExtension
import com.shiny.raisingmoney.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * Compose를 사용하는 app 모듈.
 */
class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("raisingmoney.android.application")
            configureAndroidCompose(extensions.getByType<ApplicationExtension>())
        }
    }
}

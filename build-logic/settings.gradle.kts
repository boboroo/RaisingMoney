dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    // 메인 빌드와 같은 버전 카탈로그를 사용한다.
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")

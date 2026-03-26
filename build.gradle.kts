plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.sonarqube") version "5.0.0.4638"
}

sonarqube {
    properties {
        property("sonar.projectKey", "Confecciones-Esperanza-Movil")
        property("sonar.projectName", "Confecciones Esperanza MOVIL")
        System.getenv("SONAR_HOST_URL")?.let { property("sonar.host.url", it) }
        System.getenv("SONAR_TOKEN")?.let { property("sonar.token", it) }
        property("sonar.android.lint.report.path", "build/reports/lint-results-debug.xml")
    }
}

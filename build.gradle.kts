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
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.login", "sqp_0c1558a2255a2acddeaf877feaab3dd1c236c2c1")
        property("sonar.android.lint.report.path", "build/reports/lint-results-debug.xml")
    }
}

plugins {
    alias(libs.plugins.kotlinJvm)
    application
}

application {
    mainClass = "org.example.project.ConsoleAppKt"
}

dependencies {
    implementation(projects.shared)
}
plugins {
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "org.flintcore"
version = rootProject.version

javafx {
    version = libs.versions.javafx.get()
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    compileOnly(project(":models"))
    compileOnly(project(":excels-handler"))
    compileOnly(libs.javafx.controls)
    compileOnly(libs.javafx.fxml)

    testImplementation(project(":models"))
    testImplementation(project(":excels-handler"))
    testImplementation(libs.javafx.controls)
    testImplementation(libs.javafx.fxml)
}
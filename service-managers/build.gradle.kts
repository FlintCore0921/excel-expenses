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
    arrayOf(
        project(":models"),
        project(":managers"),
        project(":file-handler"),
        project(":excels-handler"),
        project(":source-configuration")
    ).forEach {
        compileOnly(it)
        testImplementation(it)
    }

    compileOnly(libs.javafx.controls)
    compileOnly(libs.javafx.fxml)

    // For web requests
    implementation(libs.spring.boot.web)

    compileOnly(platform(libs.excelib.bom))
    testImplementation(platform(libs.excelib.bom))
    configureCompileOnlyExcelib(*ExcelibModules.values())

    testCompileOnly(libs.javafx.controls)
    testCompileOnly(libs.javafx.fxml)
}
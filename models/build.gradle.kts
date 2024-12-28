plugins {
    id("org.openjfx.javafxplugin")

}

group = "com.flintcore"
version = rootProject.version

javafx {
    version = libs.versions.javafx.get()
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    compileOnly(libs.javafx.fxml)
    compileOnly(libs.javafx.controls)

    compileOnly(libs.jackson.annotations)


    testCompileOnly(libs.javafx.fxml)
    testCompileOnly(libs.javafx.controls)
    testCompileOnly(libs.jackson.annotations)
}

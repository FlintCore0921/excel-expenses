plugins {
    id("org.openjfx.javafxplugin")

}
group = "com.flintcore"
version = "0.0.1"

javafx {
    version = libs.versions.javafx.get()
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    compileOnly(libs.javafx.fxml)
    compileOnly(libs.javafx.controls)

    testCompileOnly(libs.javafx.fxml)
    testCompileOnly(libs.javafx.controls)
}
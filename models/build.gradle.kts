
group = "com.flintcore"
version = "0.0.1"

javafx {
    version = libs.versions.javafx.get()
    modules = listOf("javafx.graphics")
}

dependencies {
    compileOnly(libs.javafx.graphics)
}
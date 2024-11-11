import org.springframework.boot.gradle.tasks.bundling.BootJar

group = "org.flintcore"
version = rootProject.version

dependencies {
    implementation(project(":models"))

    implementation(platform(libs.excelib.bom))
    testImplementation(platform(libs.excelib.bom))
    configureImplementationExcelib(*ExcelibModules.values())
}
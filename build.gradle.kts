import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "org.flintcore"
version = "0.0.2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

val projectLibs = libs
val javafxVersion = libs.versions.javafx.get()

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls", "javafx.fxml")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        project(":utilities").takeIf { it.name != project.name }?.let {
            println("added into ${project.name}")
            implementation(it)
        }

        implementation(projectLibs.data.utils)
        implementation(projectLibs.apache.commons.text)

        compileOnly(projectLibs.lombok)
        annotationProcessor(projectLibs.lombok)

        implementation(projectLibs.spring.boot.starter)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    dependencies {
        testImplementation(platform(projectLibs.junit.bom))
        testImplementation(projectLibs.junit.jupiter)
        testImplementation(projectLibs.spring.boot.starter.test)

        // Text for javafx
        testImplementation(projectLibs.testfx.core)
        testImplementation(projectLibs.testfx.junit5)
        testRuntimeOnly(projectLibs.testfx.monocle)
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.getByName<Jar>("jar") {
        enabled = true
    }

    tasks.getByName<BootJar>("bootJar") {
        enabled = false
    }
}

dependencies {
//    my modules
    implementation(project(":models"))
    implementation(project(":file-handler"))
    implementation(project(":excels-handler"))
    implementation(project(":managers"))
    implementation(project(":service-managers"))

    implementation(libs.javafx.controls)
    implementation(libs.javafx.fxml)

    implementation(platform(libs.excelib.bom))
    configureImplementationExcelib(*ExcelibModules.values())

    developmentOnly(libs.spring.boot.devtools)
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)

}

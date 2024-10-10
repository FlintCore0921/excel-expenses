import org.gradle.kotlin.dsl.javafx

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
    apply(plugin = "org.openjfx.javafxplugin")

    dependencies {
        testImplementation(platform(projectLibs.junit.bom))
        testImplementation(projectLibs.junit.jupiter)
    }

    tasks.test {
        useJUnitPlatform()
    }
}

dependencies {
//    my modules
    implementation(project(":models"))

    implementation(libs.javafx.controls)
    implementation(libs.javafx.fxml)

    implementation(platform(libs.excelib.bom))
    configureImplementationExcelib(*ExcelibModules.values())

//    implementation(libs.spring.boot.starter)
    developmentOnly(libs.spring.boot.devtools)
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

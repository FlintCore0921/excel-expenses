plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "com.flintcore"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

val javafxVersion = "22"

javafx {
	version = javafxVersion
	modules = listOf("javafx.controls", "javafx.fxml")
}

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	implementation("com.flintCore:data-utils:1.2.2")

	implementation("org.openjfx:javafx-controls:$javafxVersion")
	implementation("org.openjfx:javafx-fxml:$javafxVersion")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	implementation("org.springframework.boot:spring-boot-starter")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

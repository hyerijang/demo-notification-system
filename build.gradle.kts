plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm") version "1.8.0"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}


subprojects { // 모든 하위 모듈들에 이 설정을 적용합니다.
	group = "com.example"
	version = "0.0.1-SNAPSHOT"

	apply(plugin = "java")
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "org.springframework.boot")

	repositories {
		mavenCentral()
	}

	dependencies {
		// 공통 종속성
		implementation("org.springframework.boot:spring-boot-starter-web")
		implementation("org.springframework.boot:spring-boot-starter-data-jpa")
		implementation("org.springframework.boot:spring-boot-starter-webflux")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		compileOnly("org.projectlombok:lombok")
		annotationProcessor("org.projectlombok:lombok")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
		implementation(kotlin("stdlib-jdk8"))

		//mysql
		runtimeOnly("com.mysql:mysql-connector-j")
	}

	tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
		enabled = false
	}
}
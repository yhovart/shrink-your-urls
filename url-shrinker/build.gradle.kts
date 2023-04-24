plugins {
	java
	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.yho.shrinkyoururl"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	// implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("com.h2database:h2")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	// testImplementation("io.projectreactor:reactor-test")
}

// TODO : coverage (fail si pas suffisant)
// detection vulnerabilités (pourrait etre CI)


tasks.withType<Test> {
	useJUnitPlatform()
}

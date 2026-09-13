plugins {
    `java-library`
    `maven-publish`
    id("org.springframework.boot") version "3.5.11" apply false
    id("io.spring.dependency-management") version "1.1.7"
}

group = "work.brodykim"
version = providers.gradleProperty("version").get()

tasks.register("verifyReleaseVersion") {
    doLast {
        val releaseVersion = providers.gradleProperty("releaseVersion").orNull
        check(releaseVersion != null && releaseVersion.matches(Regex("(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)"))) {
            "Release version must be MAJOR.MINOR.PATCH without leading zeroes"
        }
        check(releaseVersion == project.version.toString()) {
            "Release tag version $releaseVersion does not match project version ${project.version}"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

repositories {
    mavenCentral()
    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")
    }
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    // Core library — transitively exposes all OB 3.0 classes to consumers
    api("com.github.brody-0125:signet-core:8a98820f9c")

    // Jackson — needed for CredentialSigner bean creation in AutoConfiguration
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // Auto-configuration support
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Spring Web for HttpStatus, HttpHeaders, MediaType used by OB 3.0 API types
    implementation("org.springframework:spring-web")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.release = 17
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// This is a library, not a bootable application
tasks.named<Jar>("jar") {
    enabled = true
}

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    kotlin("plugin.noarg") version "2.0.0"
    kotlin("plugin.allopen") version "2.0.0"
    kotlin("plugin.jpa") version "2.0.0"
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("$projectDir/config/detekt.yml"))
    baseline = file("$projectDir/config/detekt-baseline.xml")
    autoCorrect = true
}

// detekt must use a compatible version of kotlin
// this version is (mostly) independent of the kotlin version of the project
configurations.matching { it.name == "detekt" }.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion(io.gitlab.arturbosch.detekt.getSupportedKotlinVersion())
        }
    }
}

group = "de.parts_picker"
version = "docker-ready"
java.sourceCompatibility = JavaVersion.VERSION_21
val kotlinCoroutinesVersion by extra { "1.8.1" }

repositories {
    mavenCentral()
}

dependencies {
    val hibernateSearchVersion = "7.1.1.Final"
    val kotestVersion = "5.8.1"
    val kotestSpringExtensionVersion = "1.3.0"
    val testcontainersPostgresVersion = "1.19.8"
    val postgresDriverVersion = "42.7.3"
    val liquibaseVersion = "4.28.0"
    val mockkVersion = "1.13.11"
    val detektVersion = "1.23.6"

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql:$postgresDriverVersion")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    implementation("org.hibernate.search:hibernate-search-mapper-orm:$hibernateSearchVersion")
    implementation("org.hibernate.search:hibernate-search-backend-lucene:$hibernateSearchVersion")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-hateoas")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:$kotestSpringExtensionVersion")

    testImplementation("org.springframework:spring-test")
    testImplementation("org.springframework.boot:spring-boot-test")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure")
    testImplementation("org.skyscreamer:jsonassert")

    testImplementation("org.testcontainers:postgresql:$testcontainersPostgresVersion")

    testImplementation("io.mockk:mockk:$mockkVersion")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
    }
}

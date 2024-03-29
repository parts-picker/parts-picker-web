import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
    kotlin("plugin.noarg") version "1.9.0"
    kotlin("plugin.allopen") version "1.9.0"
    kotlin("plugin.jpa") version "1.9.0"
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

group = "de.parts_picker"
version = "docker-ready"
java.sourceCompatibility = JavaVersion.VERSION_17
extra["kotlin-coroutines.version"] = "1.7.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.0.4")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql:42.6.0")
    implementation("org.liquibase:liquibase-core:4.20.0")
    implementation("org.hibernate.search:hibernate-search-mapper-orm-orm6:6.2.1.Final")
    implementation("org.hibernate.search:hibernate-search-backend-lucene:6.2.1.Final")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.0.7")

    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("io.kotest:kotest-property:5.6.2")
    testImplementation("io.kotest:kotest-framework-datatest:5.6.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")

    testImplementation("org.springframework:spring-test")
    testImplementation("org.springframework.boot:spring-boot-test")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")

    testImplementation("org.testcontainers:postgresql:1.18.3")

    testImplementation("io.mockk:mockk:1.12.3")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
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

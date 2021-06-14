plugins {
    java
    jacoco

    id("com.diffplug.spotless") version "5.9.0"
    id("io.freefair.lombok") version "5.3.3.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.springframework.boot") version "2.5.0"
}

repositories {
    mavenLocal()

    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

dependencies {
    // spring boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // etl
    implementation(platform("org.apache.camel.springboot:camel-spring-boot-bom:3.10.0"))
    implementation("org.apache.camel.springboot:camel-spring-boot-starter")
    implementation("org.apache.camel.springboot:camel-csv-starter")
    implementation("org.apache.camel.springboot:camel-bindy-starter")
    implementation("org.apache.camel.springboot:camel-sql-starter")

    // database
    implementation("org.flywaydb:flyway-core:7.9.2")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.0")
    implementation("org.postgresql:postgresql")

    // spring test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.awaitility:awaitility:4.1.0")

    // annotation processors
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

spotless {
    java {
        googleJavaFormat("1.10.0")
    }
}

group = "com.decent"
version = "0.0.1"
description = "simple-data-warehouse"

val postgresUp by tasks.creating(Exec::class) {
    commandLine = listOf("docker", "run",
            "--name", "simple-data-warehouse-postgres",
            "--env", "POSTGRES_USER=admin",
            "--env", "POSTGRES_PASSWORD=admin",
            "--env", "POSTGRES_DB=warehouse",
            "--publish", "5432:5432",
            "--detach", "postgres:13.3-alpine"
    )
}

val postgresDown by tasks.creating(Exec::class) {
    commandLine = listOf("docker", "rm", "-f", "simple-data-warehouse-postgres")
}

val unitTest = tasks.named("test", Test::class) {
    useJUnitPlatform {
        includeTags("unit")
    }
}

val integrationTest by tasks.creating(Test::class) {
    useJUnitPlatform {
        includeTags("integration")
    }

    dependsOn(postgresUp)
    finalizedBy(postgresDown)
    shouldRunAfter(unitTest)
}

tasks.check {
    dependsOn(unitTest, integrationTest)
}

tasks.jacocoTestReport {
    dependsOn(tasks.check)

    reports {
        html.isEnabled = true
        xml.isEnabled = true
        csv.isEnabled = false
    }

    // including all test groups in calculating test coverage
    executionData(fileTree(buildDir).include("jacoco/*.exec"))
}

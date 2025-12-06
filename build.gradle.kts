import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.21"
    application
}

group = "com.github.asm0dey"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    main {
        kotlin.srcDirs("src")
    }
    test {
        kotlin.srcDirs("test")
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }

}
java {
    toolchain {
        targetCompatibility = JavaVersion.VERSION_24
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_24)
    }
}

tasks.register<JavaExec>("runDay") {
    group = "application"
    description = "Run a specific day's solution (use -Pday=N)"
    classpath = sourceSets["main"].runtimeClasspath
    
    val day = project.findProperty("day") ?: "1"
    mainClass.set("Day${day}Kt")
}
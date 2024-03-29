import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.compose") version "1.3.0-rc01"
}

group = "me.konyaco.lifegame"
version = "1.1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(fileTree("libs") { include("*.jar") })
    testImplementation(kotlin("test-junit5"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "Life Game"
            vendor = "KonYaco"
            windows {
                perUserInstall = true
                shortcut = true
                upgradeUuid = "82d85259-28e3-4d1e-bf53-f55696ef9917"
                menu = true
                menuGroup = "KonYaco"
            }
            linux {
                shortcut = true
                menuGroup = "KonYaco"
                packageName = "me.konyaco.lifegame"
            }
        }
    }
}
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish)
}

kotlin {

    explicitApi()

    iosArm64()
    iosSimulatorArm64()
    iosX64()
    jvm()
    linuxArm64()
    linuxX64()
    macosArm64()
    macosX64()
    mingwX64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()
    watchosX64()

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test.core)
        }
    }
}

tasks.withType<AbstractTestTask> {
    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
        showStackTraces = true
    }
}

mavenPublishing {
    publishToMavenCentral(host = SonatypeHost.S01, automaticRelease = true)
    signAllPublications()
}

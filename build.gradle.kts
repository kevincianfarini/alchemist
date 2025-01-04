import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.kmpmt)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish)
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {

    explicitApi()

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    androidNativeX86()
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    js {
        nodejs {
            testTask {
                useMocha {
                    timeout = "5s"
                }
            }
        }
    }
    jvm()
    linuxArm64()
    linuxX64()
    macosArm64()
    macosX64()
    mingwX64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    wasmJs {
        nodejs {
            testTask {
                useMocha {
                    timeout = "5s"
                }
            }
        }
    }
    wasmWasi { nodejs() }
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

dokka {
    val version = project.findProperty("VERSION_NAME")
    dokkaSourceSets.commonMain {
        includes.from("ModuleDocumentation.md")
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl("https://github.com/kevincianfarini/alchemist/tree/${version}/src")
            remoteLineSuffix.set("#L")
        }
    }
}

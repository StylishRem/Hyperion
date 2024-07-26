@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

subprojects {
    val libs = rootProject.libs

    apply {
        plugin("android-application")
        plugin(libs.plugins.kotlin.android.get().pluginId)
        plugin(libs.plugins.android.application.get().pluginId)
        plugin(libs.plugins.compose.compiler.get().pluginId)
    }

    with(kotlinExtension) {
        sourceSets.all {
            languageSettings {
                enableLanguageFeature(LanguageFeature.ContextReceivers.name)
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
            }
        }

        jvmToolchain(17)
    }

    configure<ApplicationExtension> {
        namespace = "dev.zt64.hyperion"
        compileSdk = 34

        defaultConfig {
            minSdk = 21
            targetSdk = 34
            versionCode = 1
            versionName = version.toString()
        }

        buildTypes {
            release {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    parent!!.file("proguard-rules.pro")
                )
            }

            all {
                versionNameSuffix = "-${project.name}"
            }
        }

        packaging {
            resources {
                // okhttp3 is used by some lib (no cookies so publicsuffixes.gz can be dropped)
                excludes += "/okhttp3/**"
                excludes += "META-INF/DEPENDENCIES"
            }
        }

        dependencies {
            val implementation by configurations

            implementation(rootProject.projects.common)
            ktlintRuleset(libs.ktlint.compose.rules)
        }
    }

    configure<ApplicationAndroidComponentsExtension> {
        onVariants(selector().withBuildType("release")) {
            it.packaging.resources.excludes.addAll(
                "/**/*.version",
                "/kotlin-tooling-metadata.json",
                "/DebugProbesKt.bin"
            )
        }
    }
}

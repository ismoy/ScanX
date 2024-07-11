plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose).apply(false)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {

        commonMain.dependencies {
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material)
            implementation(libs.ui)

        }
        all {
            languageSettings {
                optIn("kotlin.Experimental")
                progressiveMode = true
            }
        }

        androidMain.dependencies {
            implementation(libs.core)
            implementation(libs.zxing.android.embedded)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.core.ktx)
            implementation("androidx.activity:activity-compose:1.4.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidNativeTest.dependencies {

        }
        iosMain.dependencies {

        }
        iosTest.dependencies {

        }
    }
}
android {
    namespace = "com.github.comismoy.scanx"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/androidManifest.xml")
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.animation.android)
}

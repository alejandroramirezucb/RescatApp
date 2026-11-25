import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.spotless)
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint(libs.versions.ktlint.get())
            .setEditorConfigPath(rootProject.file(".editorconfig").absolutePath)
            .editorConfigOverride(
                mapOf(
                    "ktlint_code_style" to "android_studio",
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable"
                )
            )
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
    }
}

val supabase = Properties().apply {
    val archivo = rootProject.file("supabase.local.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}
fun configuracionSupabase(nombre: String, porDefecto: String): String =
    "\"${System.getenv(nombre) ?: supabase.getProperty(nombre) ?: porDefecto}\""

android {
    namespace = "com.rescatapp"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.rescatapp"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField(
            "String",
            "SUPABASE_URL",
            configuracionSupabase("SUPABASE_URL", "https://sin-configurar.invalid")
        )
        buildConfigField(
            "String",
            "SUPABASE_PUBLISHABLE_KEY",
            configuracionSupabase("SUPABASE_PUBLISHABLE_KEY", "")
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.osmdroid.android)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.hilt.compiler)
    ksp(libs.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
}

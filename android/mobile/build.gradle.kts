android {
    defaultConfig {
        multiDexEnabled = true
    }
}

dependencies {
    implementation(libs.compose.m3) 
    implementation("androidx.multidex:multidex:2.0.1")
}

import com.android.build.gradle.internal.fusedlibrary.createTasks

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

apply("publish.gradle")

group = "com.github.zfashion"

android {
    namespace = "com.fusion.flowbuscore"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.fusion.flowbuscore"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.fusion.utils"
            artifactId = "flowbuscore"
            version = "0.0.1"

            artifact("sourcesJar")

//            from(components["release"])
        }
    }
}


// 指定编码
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// 打包源码
tasks.register<Jar>("sourcesJar") {
    from(android.sourceSets.getByName("main").java.srcDirs)
    archiveClassifier.set("sources")
}

tasks.register<Javadoc>("javadoc") {
    isFailOnError = false
    source(android.sourceSets.getByName("main").java.getSourceFiles())

    classpath += project.files(android.bootClasspath.joinToString(File.pathSeparator))
    classpath += configurations["compile"]
}

artifacts {
    archives(tasks.named("sourcesJar"))
}




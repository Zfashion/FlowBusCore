plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
//    id("maven-publish")
}
apply("publish.gradle")

group = "com.github.zfusion"

var publishLocal = true

android {
    namespace = "com.fusion.flowbuslib"
    compileSdk = 31

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    /*publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }*/
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


/*
// 指定编码
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// 打包源码
tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    val sources = android.sourceSets.map { set -> set.java.getSourceFiles() }
    from(sources)
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

afterEvaluate {
    publishing {
        repositories {
            if (publishLocal) {
                maven {
                    val mavenDirPath = file("../maven")
                    url = uri("file://${mavenDirPath.absolutePath}")
                }
            } else {
                maven {
                    // isAllowInsecureProtocol = true // 如果Maven仓库仅支持http协议, 请打开此注释
                    url = uri("https://your-repository") // 请填入你的仓库地址
                    authentication {
                        create<BasicAuthentication>("basic")
                    }
                    credentials {
                        username = "your-username" // 请填入你的用户名
                        password = "your-password" // 请填入你的密码
                    }
                }
            }
        }

        publications {
            create<MavenPublication>("publishLib") {
                from(components["release"])
                groupId = "com.fusion.libs"
                artifactId = "flowbuscore"
                version = "0.0.1"

                */
/*pom {
                    name.set("name") // (可选)为工件取一个名字
                    url.set("https://xxx") // (可选)网站地址
                    developers {
                        developer {
                            name.set("name") // (可选)开发者名称
                            email.set("email") // (可选)开发者邮箱
                        }
                    }
                }*//*

            }
        }
    }
}*/

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        val KOTLIN = "1.3.72"
        classpath("com.android.tools.build:gradle:4.1.0")
        classpath(kotlin("gradle-plugin", version = KOTLIN))
        classpath(kotlin("serialization", version = KOTLIN))
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.28-alpha")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

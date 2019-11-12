plugins {
    kotlin("jvm") version "1.3.50"
    `maven-publish`
}

group = "com.github.bram209.ktor-graphql"

allprojects {
    version = "0.0.3"

    repositories {
        mavenCentral()
        jcenter()
    }
}

configure(subprojects.filter { it.name.startsWith("ktor-graphql") }) {
    apply(plugin = "org.gradle.maven-publish")
    apply(plugin = "java")

    tasks.withType<PublishToMavenLocal> {
        doFirst {
            println("Publishing ${publication.groupId}:${publication.artifactId}:${publication.version} to local maven repo")
        }
    }

    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                from(project.components["java"])
//                artifact("$buildDir/libs/${project.name}-${project.version}.jar")
            }
        }
    }
}

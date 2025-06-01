import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.20"
    `maven-publish`
    signing
}

group = "fr.ftnl.libs"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            signing {
                sign(configurations.archives.get())
                sign(publishing.publications["mavenJava"])
            }
            from(components["java"])
            pom {
                name.set("MathEvals")
                packaging = "jar"

                description.set("A Kotlin library for evaluating mathematical expressions with safety features.")
                url.set("https://github.com/ocelus_ftnl/MathEvals")
                licenses {
                    license {
                        name.set("GNU General Public License, version 2")
                        url.set("https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html")
                    }
                }
                developers {
                    developer {
                        id.set("draluy")
                        name.set("David Raluy")
                        email.set("david@raluy.fr")
                    }
                }
                scm {
                    connection.set("git@github.com:ocelus_ftnl/MathEvals.git")
                    developerConnection.set("git@github.com:ocelus_ftnl/MathEvals.git")
                    url.set("https://github.com/ocelus_ftnl/MathEvals")
                }
            }
        }
    }

    repositories {
        maven {
            name = "myRepo"
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            val ossrhUsername: String by project
            val ossrhPassword: String by project

            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

}

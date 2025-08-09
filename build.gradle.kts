import io.github.gradlenexus.publishplugin.NexusPublishExtension
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

group = "fr.ftnl.tools"
version = "1.0.0-SNAPSHOT"

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

allprojects {
    if (project.name == "test") return@allprojects

    plugins.withId("javaLibrary") {
        // Tâche pour créer le JAR de la Javadoc (KDoc)
        val javadocJar by tasks.registering(Jar::class) {
            dependsOn(tasks.named("dokkaHtml"))
            archiveClassifier.set("javadoc")
            from(tasks.named("dokkaHtml").get().outputs)
        }

        extensions.configure<PublishingExtension> {
            publications.named<MavenPublication>("mavenJava") {
                artifact(javadocJar)
            }
        }
    }


    extensions.configure<SigningExtension> {
        useGpgCmd()
        sign(extensions.getByType<PublishingExtension>().publications)
    }

    extensions.configure<PublishingExtension> {
        publications {
            register<MavenPublication>("mavenJava") {
                artifactId = "${rootProject.name}-${project.name}"

                plugins.withId("java-library") {
                    from(project.components["java"])
                    val sourcesJar by project.tasks.registering(Jar::class) {
                        archiveClassifier.set("sources")
                        val sourceSets = project.extensions.getByType(JavaPluginExtension::class.java).sourceSets
                        from(sourceSets["main"].allSource)
                    }
                    val javadocJar by project.tasks.registering(Jar::class) {
                        archiveClassifier.set("javadoc")
                        dependsOn(project.tasks.named("dokkaHtml", DokkaTask::class))
                        from(project.tasks.named("dokkaHtml", DokkaTask::class).get().outputs)
                    }
                    artifact(sourcesJar)
                    artifact(javadocJar)
                }

                plugins.withId("java-platform") {
                    from(components["javaPlatform"])
                }

                pom {
                    url.set("https://github.com/OcelusPRO/${rootProject.name}")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("oceluspro")
                            name.set("ocelus_ftnl")
                            email.set("contact@ftnl.fr")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/oceluspro/${rootProject.name}.git")
                        developerConnection.set("scm:git:ssh://github.com/oceluspro/${rootProject.name}.git")
                        url.set("https://github.com/oceluspro/${rootProject.name}")
                    }
                }
            }
        }
    }
}

extensions.configure<NexusPublishExtension> {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(System.getenv("OSSRH_USERNAME") ?: findProperty("ossrhUsername")?.toString())
            password.set(System.getenv("OSSRH_PASSWORD") ?: findProperty("ossrhPassword")?.toString())
        }
    }
}


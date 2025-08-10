/**************************************************************************************************
 * MathEvals - MathEvals                                                                          *
 * Copyright (C) 2025 ocelus_ftnl                                                                 *
 *                                                                                                *
 * This program is free software: you can redistribute it and/or modify                           *
 * it under the terms of the GNU Affero General Public License as                                 *
 * published by the Free Software Foundation, either version 3 of the                             *
 * License, or (at your option) any later version.                                                *
 *                                                                                                *
 * This program is distributed in the hope that it will be useful,                                *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                  *
 * GNU Affero General Public License for more details.                                            *
 *                                                                                                *
 * You should have received a copy of the GNU Affero General Public License                       *
 * along with this program. If not, see <https://www.gnu.org/licenses/>.                          *
 **************************************************************************************************/

import io.github.gradlenexus.publishplugin.NexusPublishExtension
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
version = "1.1.1"

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

// 1. Configuration de Java
java {
    // Crée et configure automatiquement le JAR des sources
    withSourcesJar()
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

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.named("dokkaHtml"))
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaHtml").get().outputs)
}


publishing {
    publications {
        // CORRECTION : On utilise "register" pour CRÉER la publication
        register<MavenPublication>("mavenJava") {
            artifactId = rootProject.name

            // "from(components["java"])" inclut le JAR principal et le JAR des sources
            from(components["java"])

            // On ajoute manuellement l'artefact Javadoc
            artifact(javadocJar)

            // Configuration du fichier POM
            pom {
                name.set("MathEvals")
                description.set("A Kotlin library for secure mathematical expression evaluation.")
                url.set("https://github.com/oceluspro/${rootProject.name}")
                licenses {
                    license {
                        name.set("Free License (GNU AGPLv3)")
                        url.set("https://github.com/OcelusPRO/MathEvals/blob/master/LICENCES/agpl-3.0.md")
                    }
                    license {
                        name.set("Commercial licence")
                        url.set("https://github.com/OcelusPRO/MathEvals/blob/master/LICENCES/commercial.md")
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

// 4. Configuration de la signature (qui trouvera maintenant la publication)
signing {
    useGpgCmd()
    sign(publishing.publications)
}

// 5. Configuration de Nexus
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
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
version = "1.0.0"

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
plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "br.com.getronics"
version = "1.1_Beta"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

javafx {
    version = "25"
    modules("javafx.controls", "javafx.fxml")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Log4j2:
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.25.4"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl")
    implementation("com.lmax:disruptor:4.0.0")

    // Ikonli, POI and Jackson:
    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome5-pack:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome6-pack:12.4.0")
    implementation("org.apache.poi:poi-ooxml:5.5.1")

    implementation("tools.jackson.core:jackson-databind:3.1.2") {
        exclude(group = "org.jspecify", module = "jspecify")
    }
}

application {
    mainClass.set("br.com.getronics.Main") // Mantém o seu ponto de entrada correto
    applicationDefaultJvmArgs = listOf(
        "-Djavafx.animation.fullspeed=true",
        "-Dprism.text=native",
        "-Dprism.lcdtext=true",
        "--enable-native-access=javafx.graphics"
    )
}

tasks.register<Copy>("copyDependencies") {
    group = "build"
    description = "Copia as dependências do runtimeClasspath para a pasta build/libs para o jpackage."

    dependsOn(tasks.jar)

    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("libs"))
}

tasks.named<Jar>("jar") {
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}
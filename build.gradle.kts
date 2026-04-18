plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "br.com.getronics"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

javafx {
    version = "25.0.2"
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
    implementation(platform(notation = "org.apache.logging.log4j:log4j-bom:2.25.4"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl")
    // Log4j2 Asynchronous:
    implementation("com.lmax:disruptor:4.0.0")
    // Ikonli:
    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome5-pack:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome6-pack:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-materialdesign-pack:12.4.0")
    // POI:
    implementation("org.apache.poi:poi-ooxml:5.5.1")
    // FasterXML:
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}

tasks.test {
    useJUnitPlatform()
}

application {
    // Enable the "application" gradle command
    mainClass = "Main"
    applicationDefaultJvmArgs = listOf(
        "--enable-native-access=javafx.graphics",
        // To activate the Log4j2 asynchronously:
        "-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
    )
}
plugins {
    kotlin("jvm") version "2.0.20"
    id("com.google.protobuf") version "0.9.4"
}

group = "org.pos"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    api("io.grpc:grpc-kotlin-stub:1.4.1")

    // https://mvnrepository.com/artifact/io.grpc/grpc-kotlin-stub
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    // https://mvnrepository.com/artifact/io.grpc/grpc-protobuf
    implementation("io.grpc:grpc-protobuf:1.68.1")
    implementation("io.grpc:grpc-netty-shaded:1.68.1")
    // https://mvnrepository.com/artifact/com.google.protobuf/protobuf-kotlin
    implementation("com.google.protobuf:protobuf-kotlin:4.28.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // https://mvnrepository.com/artifact/org.ktorm/ktorm-core
    implementation("org.ktorm:ktorm-core:4.1.1")
    // https://mvnrepository.com/artifact/org.ktorm/ktorm-support-mysql
    implementation("org.ktorm:ktorm-support-mysql:4.1.1")
    // https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    implementation("com.mysql:mysql-connector-j:9.1.0")
    // https://mvnrepository.com/artifact/com.auth0/java-jwt
    implementation("com.auth0:java-jwt:4.4.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")
    testImplementation("io.grpc:grpc-testing:1.68.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.28.3"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.68.1"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}
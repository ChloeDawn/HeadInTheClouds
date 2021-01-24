import net.minecraftforge.gradle.common.task.SignJar
import net.minecraftforge.gradle.common.util.RunConfig
import org.gradle.util.GradleVersion
import java.time.Instant

plugins {
  id("net.minecraftforge.gradle") version "4.0.9"
  id("org.spongepowered.mixin") version "0.7-SNAPSHOT"
  id("net.nemerosa.versioning") version "2.8.2"
  id("signing")
}

group = "dev.sapphic"
version = "1.0.0"

val modId: String = "headintheclouds"
val mixinRefmap: String = "refmap.$modId.json"
val mixinConfig: String = "mixins.$modId.json"

java {
  withSourcesJar()
}

minecraft {
  mappings("snapshot", "20201028-1.16.3")
  runs {
    fun RunConfig.configured() {
      workingDirectory = file("run").canonicalPath
      mods.create(modId).source(sourceSets["main"])
      mapOf(
        "forge.logging.console.level" to "debug",
        "mixin.env.disableRefMap" to true,
        "mixin.debug.export" to true,
        "mixin.debug.export.decompile" to false,
        "mixin.debug.verbose" to true,
        "mixin.debug.dumpTargetOnFailure" to true,
        "mixin.checks" to true,
        "mixin.hotSwap" to true
      ).forEach { (k, v) -> property(k, "$v") }
      arg("-mixin.config=$mixinConfig")
    }

    create("client").configured()
  }
}

mixin {
  add("main", mixinRefmap)
}

dependencies {
  minecraft("net.minecraftforge:forge:1.16.5-36.0.1")
  implementation("org.checkerframework:checker-qual:3.9.0")
  annotationProcessor("org.spongepowered:mixin:0.8.2:processor")
}

tasks {
  compileJava {
    with(options) {
      release.set(8)
      isFork = true
      isDeprecation = true
      encoding = "UTF-8"
      listOf(
        "-Xlint:all",
        "-parameters",
        "-XprintProcessorInfo",
        "-XprintRounds"
      ).let(compilerArgs::addAll)
    }
  }

  processResources {
    from(mixinConfig) {
      expand("refmap" to mixinRefmap)
    }
  }

  jar {
    from("/LICENSE")

    manifest.attributes(
      "Build-Timestamp" to Instant.now(),
      "Build-Revision" to versioning.info.commit,
      "Build-Jvm" to "${
        System.getProperty("java.version")
      } (${
        System.getProperty("java.vendor")
      } ${
        System.getProperty("java.vm.version")
      })",
      "Built-By" to GradleVersion.current(),

      "Implementation-Title" to project.name,
      "Implementation-Version" to project.version,
      "Implementation-Vendor" to project.group,

      "Specification-Title" to "ForgeMod",
      "Specification-Version" to "1.0.0",
      "Specification-Vendor" to project.group,

      "MixinConfigs" to mixinConfig
    )

    finalizedBy(reobf)
  }

  if (project.hasProperty("signing.mods.keyalias")) {
    val keyalias = project.property("signing.mods.keyalias")
    val keystore = project.property("signing.mods.keystore")
    val password = project.property("signing.mods.password")

    val signJar by creating(SignJar::class) {
      dependsOn(reobf)

      setAlias(keyalias)
      setKeyStore(keystore)
      setKeyPass(password)
      setStorePass(password)
      setInputFile(jar.get().archiveFile)
      setOutputFile(inputFile)

      doLast {
        signing.sign(inputFile)
      }
    }

    val signSourcesJar by creating(SignJar::class) {
      val sourcesJar by getting(Jar::class) // No type-safe accessor

      dependsOn(sourcesJar)

      setAlias(keyalias)
      setKeyStore(keystore)
      setKeyPass(password)
      setStorePass(password)
      setInputFile(sourcesJar.archiveFile)
      setOutputFile(inputFile)

      doLast {
        signing.sign(inputFile)
      }
    }

    assemble {
      dependsOn(signJar, signSourcesJar)
    }
  }
}

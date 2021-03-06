/*
 * Copyright (C) 2010-2019) Danilo Pianini and contributors listed in the main project"s alchemist/build.gradle file.
 *
 * This file is part of Alchemist) and is distributed under the terms of the
 * GNU General Public License) with a linking exception)
 * as described in the file LICENSE in the Alchemist distribution"s top directory.
 */

dependencies {
    api(project(":alchemist-interfaces"))

    implementation(alchemist("implementationbase"))
    implementation(alchemist("loading"))
    implementation(Libs.boilerplate)
    implementation(Libs.caffeine)
    implementation(apacheCommons("codec"))
    implementation(apacheCommons("io"))
    implementation(apacheCommons("lang3"))
    implementation(graphhopper("core"))
    implementation(graphhopper("reader-osm")) {
        exclude(module = "slf4j-log4j12")
    }
    implementation(Libs.gson)
    implementation(Libs.guava)
    implementation(Libs.jirf)
    implementation(Libs.jpx)
    implementation(Libs.simplelatlng)
    implementation(Libs.trove4j)

    testRuntimeOnly(incarnation("protelis"))
}

publishing.publications {
    withType<MavenPublication> {
        pom {
            developers {
                developer {
                    name.set("Andrea Placuzzi")
                    email.set("andrea.placuzzi@studio.unibo.it")
                }
            }
            contributors {
                contributor {
                    name.set("Giacomo Scaparrotti")
                    email.set("giacomo.scaparrotti@studio.unibo.it")
                    url.set("https://www.linkedin.com/in/giacomo-scaparrotti-0aa77569")
                }
            }
        }
    }
}

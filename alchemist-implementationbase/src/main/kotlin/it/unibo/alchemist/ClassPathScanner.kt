/*
 * Copyright (C) 2010-2019, Danilo Pianini and contributors listed in the main project's alchemist/build.gradle file.
 *
 * This file is part of Alchemist, and is distributed under the terms of the
 * GNU General Public License, with a linking exception,
 * as described in the file LICENSE in the Alchemist distribution's top directory.
 */

package it.unibo.alchemist

import com.github.benmanes.caffeine.cache.Caffeine
import io.github.classgraph.ClassGraph
import java.io.InputStream
import java.net.URL
import java.util.regex.Pattern

private typealias ScanData = Pair<Class<*>, String?>
private val ScanData.inPackage get() = second
private val ScanData.superClass get() = first

/**
 * An utility class providing support for loading arbitrary subclasses available in the classpath.
 */
object ClassPathScanner {

    private val loader = Caffeine.newBuilder().build<ScanData, List<Class<*>>> { scanData ->
        classGraphForPackage(scanData.inPackage)
            .enableClassInfo()
            .scan()
            .let { scanResult ->
                if (scanData.superClass.isInterface) {
                    scanResult.getClassesImplementing(scanData.superClass.name)
                } else {
                    scanResult.getSubclasses(scanData.superClass.name)
                }
            }
            .filter { !it.isAbstract }.loadClasses()
    }

    private fun classGraphForPackage(inPackage: String?): ClassGraph = ClassGraph()
        .apply {
            if (inPackage != null) {
                // WHITELIST package
                acceptPackages(inPackage)
                // BLACKLIST package
                rejectPackages("org.gradle")
            }
        }

    /**
     * This function loads all subtypes of the provided Java class that can be discovered on the current classpath.
     *
     * This function cannot use `reified` and `inline` (as it should have) due to Java being unaware of the required
     * transformation to use them.
     */
    @JvmStatic
    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <T> subTypesOf(superClass: Class<T>, inPackage: String? = null): List<Class<out T>> =
        loader[ScanData(superClass, inPackage)] as List<Class<out T>>

    inline fun <reified T> subTypesOf(inPackage: String? = null): List<Class<out T>> =
        subTypesOf(T::class.java, inPackage)

    /**
     * This function returns a list of all the resources in a certain (optional) package matching a regular expression.
     *
     * This function cannot use `reified` and `inline` (as it should have) due to Java being unaware of the required
     * transformation to use them.
     */
    @JvmStatic
    @JvmOverloads
    fun resourcesMatching(regex: String, inPackage: String? = null): List<URL> = classGraphForPackage(inPackage)
        .scan().getResourcesMatchingPattern(Pattern.compile(regex))
        .urLs

    @JvmStatic
    @JvmOverloads
    fun resourcesMatchingAsStream(regex: String, inPackage: String? = null): List<InputStream> =
        resourcesMatching(regex, inPackage).map { it.openStream() }
}

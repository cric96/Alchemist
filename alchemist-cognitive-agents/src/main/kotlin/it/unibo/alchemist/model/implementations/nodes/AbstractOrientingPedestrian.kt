/*
 * Copyright (C) 2010-2020, Danilo Pianini and contributors
 * listed in the main project's alchemist/build.gradle.kts file.
 *
 * This file is part of Alchemist, and is distributed under the terms of the
 * GNU General Public License, with a linking exception,
 * as described in the file LICENSE in the Alchemist distribution's top directory.
 */

package it.unibo.alchemist.model.implementations.nodes

import it.unibo.alchemist.model.implementations.actions.takePercentage
import it.unibo.alchemist.model.implementations.geometry.euclidean2d.graph.UndirectedNavigationGraph
import it.unibo.alchemist.model.implementations.geometry.euclidean2d.graph.pathExists
import it.unibo.alchemist.model.interfaces.Position
import it.unibo.alchemist.model.interfaces.PedestrianGroup
import it.unibo.alchemist.model.interfaces.OrientingPedestrian
import it.unibo.alchemist.model.interfaces.environments.PhysicsEnvironmentWithGraph
import it.unibo.alchemist.model.interfaces.geometry.ConvexGeometricShape
import it.unibo.alchemist.model.interfaces.geometry.GeometricShapeFactory
import it.unibo.alchemist.model.interfaces.geometry.GeometricTransformation
import it.unibo.alchemist.model.interfaces.geometry.Vector
import it.unibo.alchemist.model.interfaces.geometry.euclidean2d.graph.NavigationGraph
import it.unibo.alchemist.shuffled
import org.apache.commons.math3.random.RandomGenerator
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree
import org.jgrapht.graph.AsWeightedGraph
import org.jgrapht.graph.DefaultEdge

/**
 * An abstract [OrientingPedestrian], contains an algorithm for the generation of a pseudo-random [cognitiveMap]. The
 * creation of landmarks is left to subclasses via factory method (see [createLandmarkIn]).
 *
 * @param T the concentration type.
 * @param P the [Position] type and [Vector] type for the space this pedestrian is inside.
 * @param A the transformations supported by the shapes in this space.
 * @param L the type of landmarks in the pedestrian's [cognitiveMap].
 * @param N the type of nodes of the navigation graph provided by the environment.
 * @param E the type of edges of the navigation graph provided by the environment.
 * @param F the type of the shape factory provided by the environment.
 */
abstract class AbstractOrientingPedestrian<T, P, A, L, N, E, F>(
    final override val knowledgeDegree: Double,
    /**
     * The random generator to use in order to preserve reproducibility.
     */
    protected val randomGenerator: RandomGenerator,
    /**
     * The environment this pedestrian is into.
     */
    environment: PhysicsEnvironmentWithGraph<*, T, P, A, N, E, F>,
    group: PedestrianGroup<T, P, A>? = null,
    /**
     * Environment's areas whose diameter is smaller than ([minArea] * the diameter of this pedestrian) will be
     * regarded as too small and discarded when generating the cognitive map (i.e. no landmark will be placed inside
     * them).
     */
    private val minArea: Double = 10.0
) : OrientingPedestrian<T, P, A, L, DefaultEdge>,
    AbstractHomogeneousPedestrian<T, P, A, F>(environment, randomGenerator, group)
    where P : Position<P>, P : Vector<P>,
          A : GeometricTransformation<P>,
          L : ConvexGeometricShape<P, A>,
          N : ConvexGeometricShape<P, A>,
          F : GeometricShapeFactory<P, A> {

    init {
        require(knowledgeDegree in 0.0..1.0) { "knowledge degree must be in [0,1]" }
    }

    override val volatileMemory: MutableMap<ConvexGeometricShape<P, A>, Int> = HashMap()

    /**
     * The cognitive map of the pedestrian. This is generated from the [environment]'s graph as follows: we randomly
     * select a % of environment's areas equal to the knowledge degree of the pedestrian, we then create a landmark
     * in each of them. Those landmarks will be the nodes of the cognitive map. Concerning the connections between
     * them, we produce a graph in which each generated landmark is connected to any other landmark reachable from it,
     * with an edge whose weight depends on the number of areas that need to be traversed. Finally, the cognitive map
     * is a minimum spanning tree of the described full connected graph.
     * Note that edges are plain [DefaultEdge]s, which means no extra info regarding the connection between landmarks
     * is stored in the cognitive map. If two landmarks are connected, the pedestrian knows there's a path between them
     * (this may be simple or not, i.e. representable as a single segment, but the pedestrian doesn't know it). If two
     * landmarks are not connected, the pedestrian doesn't have info regarding any path between them, which may
     * anyway exist.
     */
    override val cognitiveMap: NavigationGraph<P, A, L, DefaultEdge> by lazy {
        val environmentGraph = environment.graph
        /*
         * The rooms in which landmarks will be placed.
         */
        val rooms = environmentGraph.vertexSet()
            .filter { it.diameter > shape.diameter * minArea }
            .shuffled(randomGenerator)
            .toList()
            .takePercentage(knowledgeDegree)
            .toMutableList()
        /*
         * landmarks[i] will contain the landmark generated in rooms[i].
         */
        val landmarks = rooms.map { createLandmarkIn(it) }
        val fullGraph = UndirectedNavigationGraph<P, A, L, DefaultEdge>(DefaultEdge::class.java)
        landmarks.forEach { fullGraph.addVertex(it) }
        rooms.indices.forEach { i ->
            rooms.indices.forEach { j ->
                if (i != j && environmentGraph.pathExists(rooms[i], rooms[j])) {
                    fullGraph.addEdge(landmarks[i], landmarks[j])
                }
            }
        }
        /*
         * The environment's graph is unweighted, but edges' weights defaults to 1.0
         */
        val dijkstra = DijkstraShortestPath(environmentGraph)
        val weightFunction: (DefaultEdge) -> Double = { edge ->
            val tail = fullGraph.getEdgeSource(edge)
            val head = fullGraph.getEdgeTarget(edge)
            /*
             * The weight of the shortest path between two rooms (tail, head) is the number
             * of rooms that need to be traversed to go from tail to head.
             */
            dijkstra.getPathWeight(rooms[landmarks.indexOf(tail)], rooms[landmarks.indexOf(head)])
        }
        val fullGraphWeighted = AsWeightedGraph(fullGraph, weightFunction, false, false)
        /*
         * Only the edges in the spanning tree are maintained.
         */
        fullGraph.removeAllEdges(
            fullGraph.edgeSet() - PrimMinimumSpanningTree(fullGraphWeighted).spanningTree.edges
        )
        fullGraph
    }

    /**
     * Creates a landmark entirely contained in the given area. If such area contains one or more destinations, the
     * returned landmark must contain at least one of them.
     */
    protected abstract fun createLandmarkIn(area: N): L
}

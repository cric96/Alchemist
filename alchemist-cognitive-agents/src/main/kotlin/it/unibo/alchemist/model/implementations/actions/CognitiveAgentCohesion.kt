package it.unibo.alchemist.model.implementations.actions

import it.unibo.alchemist.model.interfaces.Environment
import it.unibo.alchemist.model.interfaces.Pedestrian
import it.unibo.alchemist.model.interfaces.Position
import it.unibo.alchemist.model.interfaces.Reaction
import it.unibo.alchemist.model.interfaces.geometry.GeometricTransformation
import it.unibo.alchemist.model.interfaces.geometry.Vector

/**
 * Move the agent towards the other members of his group.
 *
 * @param env
 *          the environment inside which the pedestrian moves.
 * @param reaction
 *          the reaction which executes this action.
 * @param pedestrian
 *          the owner of this action.
 */
class CognitiveAgentCohesion<T, P, A>(
    env: Environment<T, P>,
    reaction: Reaction<T>,
    pedestrian: Pedestrian<T, P, A>
) : AbstractGroupSteeringAction<T, P, A>(env, reaction, pedestrian)
    where P : Position<P>, P : Vector<P>,
          A : GeometricTransformation<P> {

    override fun cloneAction(n: Pedestrian<T, P, A>, r: Reaction<T>) = CognitiveAgentCohesion(env, r, n)

    override fun nextPosition(): P = (centroid() - currentPosition).coerceAtMost(maxWalk)

    override fun group(): List<Pedestrian<T, P, *>> = pedestrian.membershipGroup.members
}

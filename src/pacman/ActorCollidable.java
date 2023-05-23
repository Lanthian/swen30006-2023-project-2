// ActorCollidable.java interface
package pacman;

import ch.aplu.jgamegrid.Actor;

public interface ActorCollidable {
    void checkAndCollide(Actor actor, ActorType type);
}

// Collidable.java interface
package pacman;

public interface Collidable {
    boolean hasCollided(PacActor pacActor);

    void enactCollision(PacActor pacActor);
}

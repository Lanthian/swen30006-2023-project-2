// Monster.java
// Used for PacMan
package pacman;

import ch.aplu.jgamegrid.*;
import java.awt.Color;
import java.util.*;

public abstract class Monster extends Actor implements Collidable
{
  protected Game game;
  protected MonsterType type;
  protected ArrayList<Location> visitedList = new ArrayList<Location>();
  protected final int listLength = 10;
  protected boolean stopMoving = false;
  protected int seed = 0;
  protected Random randomiser = new Random(0);

  public Monster(Game game, MonsterType type)
  {
    super("sprites/" + type.getImageName());
    this.game = game;
    this.type = type;
  }

  public void stopMoving(int seconds) {
    this.stopMoving = true;
    Timer timer = new Timer(); // Instantiate Timer Object
    int SECOND_TO_MILLISECONDS = 1000;
    final Monster monster = this;
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        monster.stopMoving = false;
      }
    }, seconds * SECOND_TO_MILLISECONDS);
  }

  public void setSeed(int seed) {
    this.seed = seed;
      randomiser.setSeed(seed);
  }

  public void setStopMoving(boolean stopMoving) {
    this.stopMoving = stopMoving;
  }

  public void act()
  {
    if (stopMoving) {
      return;
    }
    walkApproach();
    if (getDirection() > 150 && getDirection() < 210)
      setHorzMirror(false);
    else
      setHorzMirror(true);
  }

  protected abstract void walkApproach();

  public MonsterType getType() {
    return type;
  }

  protected void addVisitedList(Location location)
  {
    visitedList.add(location);
    if (visitedList.size() == listLength)
      visitedList.remove(0);
  }

  protected boolean isVisited(Location location)
  {
    for (Location loc : visitedList)
      if (loc.equals(location))
        return true;
    return false;
  }

  // Lanthian 23.04.07 : Might be worth abstracting this and implementing elsewhere if it changes
  protected boolean canMove(Location location)
  {
    Color c = getBackground().getColor(location);
    if (c.equals(Color.gray) || location.getX() >= game.getNumHorzCells()
            || location.getX() < 0 || location.getY() >= game.getNumVertCells() || location.getY() < 0)
      return false;
    else
      return true;
  }

  // Collidable interface functions
  @Override
  public boolean hasCollided(PacActor pacActor) {
    return this.getLocation().equals(pacActor.getLocation());
  }

  @Override
  public void enactCollision(PacActor pacActor) {
    pacActor.loseGame();
  }
}

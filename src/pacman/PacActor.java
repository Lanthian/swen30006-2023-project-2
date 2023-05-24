// PacActor.java
// Used for PacMan
package pacman;

import ch.aplu.jgamegrid.*;
import jdk.jshell.execution.LoaderDelegate;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.*;

public class PacActor extends Actor implements GGKeyRepeatListener
{
  // Singleton instance
  private static PacActor singleton = null;

  private GameState gameState;

  private static final int nbSprites = 4;
  private int idSprite = 0;
  private int nbPills = 0;
  private static int score = 0;
  private Game game;
  private ArrayList<Location> visitedList = new ArrayList<Location>();
  private List<String> propertyMoves = new ArrayList<>();
  private int propertyMoveIndex = 0;
  private final int listLength = 10;
  private int seed;
  private Random randomiser = new Random();

  private static final Location.CompassDirection[] ANGLES = {Location.NORTH,
          Location.EAST,Location.SOUTH,Location.WEST};

  // Constructor
  private PacActor() {
    super(true, "sprites/pacpix.gif", nbSprites);   // Rotatable
  }

  // Singleton retriever
  public static PacActor getPacActor(Game game)
  {
    if (singleton == null) {
      singleton = new PacActor();
    }
    singleton.setGame(game);
    return singleton;
  }

  private boolean isAuto = false;

  public void setAuto(boolean auto) {
    isAuto = auto;
  }


  public void setSeed(int seed) {
    this.seed = seed;
    randomiser.setSeed(seed);
  }

  public void setPropertyMoves(String propertyMoveString) {
    if (propertyMoveString != null) {
      this.propertyMoves = Arrays.asList(propertyMoveString.split(","));
    }
  }

  public void keyRepeated(int keyCode)
  {
    if (isAuto) {
      return;
    }
    if (isRemoved())  // Already removed
      return;
    Location next = null;
    switch (keyCode)
    {
      case KeyEvent.VK_LEFT:
        next = getLocation().getNeighbourLocation(Location.WEST);
        setDirection(Location.WEST);
        break;
      case KeyEvent.VK_UP:
        next = getLocation().getNeighbourLocation(Location.NORTH);
        setDirection(Location.NORTH);
        break;
      case KeyEvent.VK_RIGHT:
        next = getLocation().getNeighbourLocation(Location.EAST);
        setDirection(Location.EAST);
        break;
      case KeyEvent.VK_DOWN:
        next = getLocation().getNeighbourLocation(Location.SOUTH);
        setDirection(Location.SOUTH);
        break;
    }
    if (next != null && canMove(next))
    {
      setLocation(next);
      game.updateActor(this, ActorType.Player);
    }
  }

  public void act()
  {
    show(idSprite);
    idSprite++;
    if (idSprite == nbSprites)
      idSprite = 0;

    if (isAuto) {
//      moveInAutoMode();
      System.out.println("Pacman auto: Implement this.");
    }
    this.game.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
  }

  private Location closestPillLocation() {
    int currentDistance = 1000;
    Location currentLocation = null;
    List<Location> lootLocations = game.getRemainingLoot();
    for (Location location: lootLocations) {
      int distanceToPill = location.getDistanceTo(getLocation());
      if (distanceToPill < currentDistance) {
        currentLocation = location;
        currentDistance = distanceToPill;
      }
    }

    return currentLocation;
  }

//  private void moveInAutoMode() {
//    if (propertyMoves.size() > propertyMoveIndex) {
//      followPropertyMoves();
//      return;
//    }
//    Location closestPill = closestPillLocation();
//    double oldDirection = getDirection();
//
//    Location.CompassDirection compassDir =
//            getLocation().get4CompassDirectionTo(closestPill);
//    Location next = getLocation().getNeighbourLocation(compassDir);
//    setDirection(compassDir);
//    if (!isVisited(next) && canMove(next)) {
//      setLocation(next);
//    } else {
//      // normal movement
//      int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
//      setDirection(oldDirection);
//      turn(sign * 90);  // Try to turn left/right
//      next = getNextMoveLocation();
//      if (canMove(next)) {
//        setLocation(next);
//      } else {
//        setDirection(oldDirection);
//        next = getNextMoveLocation();
//        if (canMove(next)) // Try to move forward
//        {
//          setLocation(next);
//        } else {
//          setDirection(oldDirection);
//          turn(-sign * 90);  // Try to turn right/left
//          next = getNextMoveLocation();
//          if (canMove(next)) {
//            setLocation(next);
//          } else {
//            setDirection(oldDirection);
//            turn(180);  // Turn backward
//            next = getNextMoveLocation();
//            setLocation(next);
//          }
//        }
//      }
//    }
//    game.updateActor(this, ActorType.Player);
//    addVisitedList(next);
//  }

  private void addVisitedList(Location location)
  {
    visitedList.add(location);
    if (visitedList.size() == listLength)
      visitedList.remove(0);
  }

  private boolean isVisited(Location location)
  {
    for (Location loc : visitedList)
      if (loc.equals(location))
        return true;
    return false;
  }

  private boolean canMove(Location location)
  {
    Color c = getBackground().getColor(location);
    if ( c.equals(Color.gray) || location.getX() >= game.getNumHorzCells()
            || location.getX() < 0 || location.getY() >= game.getNumVertCells() || location.getY() < 0)
      return false;
    else
      return true;
  }

  // Helper function to find valid moves from a location    // todo - javadoc comment
  // note the random order
  public ArrayList<Location> getValidMoves(Location loc) {
    ArrayList<Location> validLocations = new ArrayList<>();

    List<Location.CompassDirection> directions = new ArrayList<>(Arrays.asList(ANGLES));
    Collections.shuffle(directions);
    for (Location.CompassDirection dir : directions) {
      Location step = loc.getNeighbourLocation(dir);
      if (canMove(step)) validLocations.add(step);
    }

    return validLocations;
  }

  // --- Getters & Setters ---
  public int getNbPills() {
    return nbPills;
  }

  public void addScore(int i) {
    score += i;
  }

  public void incrementNbPills() {
    this.nbPills ++;
  }

  private void setGame(Game game) {
    singleton.game = game;
    singleton.gameState = GameState.Active;
  }

  public GameState getGameState() {
    return singleton.gameState;
  }

  public void loseGame() {
    singleton.gameState = GameState.Lose;
  }

  public int getScore() {
    return score;
  }
}

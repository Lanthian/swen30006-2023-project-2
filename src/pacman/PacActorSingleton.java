// PacActorSingleton.java
// Used for PacMan
package pacman;

import constants.Constants;

import ch.aplu.jgamegrid.*;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.*;


public class PacActorSingleton extends Actor implements GGKeyRepeatListener
{
  // Singleton instance
  private static PacActorSingleton singleton = null;

  private GameState gameState;
  private static final int nbSprites = 4;
  private int idSprite = 0;
  private int nbPills = 0;
  private int score = 0;
  private Game game;

  // Below variables and associated methods are never used, but useful to have for later extension
  private final ArrayList<Location> visitedList = new ArrayList<Location>();
  private final List<String> propertyMoves = new ArrayList<>();
  private int propertyMoveIndex = 0;
  private final int listLength = 10;
  private int seed;
  private Random randomiser = new Random();

  private static final Location.CompassDirection[] ANGLES = {Location.NORTH,
          Location.EAST,Location.SOUTH,Location.WEST};
  private ArrayList<Location> queuedMoves = new ArrayList<>();

  // Constructor
  private PacActorSingleton() {
    super(true, Constants.SPRITE_PATH+"pacpix.gif", nbSprites);   // Rotatable
  }

  // Singleton retriever
  public static PacActorSingleton getInstance(Game game)
  {
    if (singleton == null) {
      singleton = new PacActorSingleton();
    }
    singleton.setGame(game);                  // Set current game
    singleton.gameState = GameState.Active;   // Reset pacman state
    singleton.score = 0;                      // Reset score if not 0.
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
      moveInAutoMode();
    }
    this.game.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
  }

  private void moveInAutoMode() {
    // Requeue a set of moves if previous queue done
    if (this.queuedMoves.size() == 0) {
      PathFinder closestLoot = new PathFinder(game.getRemainingLoot(), game.getPortals());
      queuedMoves = closestLoot.findNextLoc(this);

      // Check for no remaining moves -- game should end soon (all items collected)...
      if (this.queuedMoves.size() == 0) return;
    }

    Location next = queuedMoves.remove(0);
    assert(next != null);     // pointless assertion but better safe than sorry

    // Set direction and make move to next location
    setDirection(getLocation().get4CompassDirectionTo(next));
    setLocation(next);

    game.updateActor(this, ActorType.Player);
    addVisitedList(next);
  }

  private void addVisitedList(Location location)
  {
    visitedList.add(location);
    if (visitedList.size() == listLength)
      visitedList.remove(0);
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

  /**
   * Helper function to find valid moves from a location. Randomises order returned.
   * @param loc Starting location of surrounding search
   * @return ArrayList of valid neighbouring locations.
   */
  public ArrayList<Location> getValidMoves(Location loc) {
    ArrayList<Location> validLocations = new ArrayList<>();

    List<Location.CompassDirection> directions = new ArrayList<>(Arrays.asList(ANGLES));
    Collections.shuffle(directions);
    for (Location.CompassDirection dir : directions) {
      Location step = loc.getNeighbourLocation(dir);
      if (this.canMove(step)) validLocations.add(step);
    }

    return validLocations;
  }

  // --- Getters & Setters ---
  public int getNbPills() {
    return nbPills;
  }

  public void addScore(int i) {
    this.score += i;
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
  public void winGame() {
    singleton.gameState = GameState.Win;
  }

  public void loseGame() {
    singleton.gameState = GameState.Lose;
  }

  public int getScore() {
    return score;
  }
}

package pacman;

import ch.aplu.jgamegrid.*;
import pacman.utility.GameCallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;


public class Game extends GameGrid
{
  // === Actors ===
  // Retrieve singleton pacActor, setting current game to this instance
  protected PacActorSingleton pacActorSingleton;
  private final ArrayList<ActorCollidable> collidables = new ArrayList<>();

  private final ArrayList<Monster> monsters = new ArrayList<>();
  private final ArrayList<Item> items = new ArrayList<>();
  private final ArrayList<Item> loot = new ArrayList<>();       // Subset of items
  private final ArrayList<PortalPair> portalPairs = new ArrayList<>();

  private final GameCallback gameCallback;
  private int seed;

  public Game(GameMap map, GameCallback gameCallback, Properties properties)
  {
    //Setup game
    super(map.getWidth(), map.getHeight(), 20, false);
    this.gameCallback = gameCallback;
    this.seed = Integer.parseInt(properties.getProperty("seed"));
    setSimulationPeriod(100);
    setTitle("[PacMan in the TorusVerse]");

    // === Draw initial board ===
    GGBackground bg = getBg();
    bg.clear(Color.lightGray);

    // === Draw loaded walls ===
    for (Location loc : map.getWalls()) {
      bg.fillCell(loc, Color.gray);
    }

    // === Load portals ===
    // Iterate through portaltype, locationlist pairs
    for (Map.Entry<PortalType, ArrayList<Location>> entry : map.getPortals().entrySet()) {
      // If portal set does not exist, skip
      if (entry.getValue().size() == 0) continue;

      // Iterate through location pairs and add portals to the game
      PortalPair portal = new PortalPair(this, entry.getKey());
      // Already asserted map valid, so guaranteed 2 portals
      portal.placePortal(entry.getValue().get(0));
      portal.placePortal(entry.getValue().get(1));
      this.portalPairs.add(portal);
    }
    this.collidables.addAll(portalPairs);

    // === Load pacActor ===
    pacActorSingleton = PacActorSingleton.getInstance(this);
    pacActorSingleton.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
    addActor(pacActorSingleton, map.getPacLocation());
    pacActorSingleton.setSeed(seed);
    pacActorSingleton.setSlowDown(3);

    // === Load items ===
    // Item array and factory to reduce redundant code later
    ItemFactorySingleton iFactory = ItemFactorySingleton.getFactory();
    // Iterate through itemtype, locationlist pairs
    for (Map.Entry<ItemType, ArrayList<Location>> entry : map.getItems().entrySet()) {
      // Iterate through locations and add monsters to the game
      for (Location loc : entry.getValue()) {
        Item item = iFactory.makeItem(entry.getKey(), this, loc);
        this.items.add(item);

        // Store and track loot items in an additional array
        if (entry.getKey().isLoot()) this.loot.add(item);
      }
    }
    this.collidables.addAll(items);

    // === Load monsters ===
    // Monster array and factory to reduce redundant code later
    MonsterFactorySingleton mFactory = MonsterFactorySingleton.getFactory();
    // Iterate through monstertype, locationlist pairs
    for (Map.Entry<MonsterType, ArrayList<Location>> entry : map.getMonsters().entrySet()) {
      // Iterate through locations and add monsters to the game
      for (Location loc : entry.getValue()) {
        Monster monster = mFactory.makeMonster(this, entry.getKey());
        this.monsters.add(monster);
        addActor(monster, loc);
      }
    }
    this.collidables.addAll(monsters);



    // Set pacActor movement and other monster uniques
    addKeyRepeatListener(pacActorSingleton);
    setKeyRepeatPeriod(150);


    //Run the game
    doRun();
    show();
    // Loop to look for collision in the application thread
    // This makes it improbable that we miss a hit
    boolean hasPacmanEatAllPills;
    int maxPillsAndItems = countPillsAndItems();

    do {
      // Collision is checked each time pacActor or Monster moves (not done here)
      // Game winning (eat all pills) is checked too (not done here)
      delay(10);
    } while(pacActorSingleton.getGameState() == GameState.Active);
    delay(120);

    for (Monster monster : monsters) {
      monster.setStopMoving(true);
    }
    Location endLocation = pacActorSingleton.getLocation();
    pacActorSingleton.removeSelf();


    String title = "";
    if (pacActorSingleton.getGameState() == GameState.Lose) {
      bg.setPaintColor(Color.red);
      title = "GAME OVER";
      addActor(new Actor("sprites/explosion3.gif"), endLocation);
    } else if (pacActorSingleton.getGameState() == GameState.Win) {
      bg.setPaintColor(Color.yellow);
      title = "YOU WIN";
    }
    setTitle(title);
    gameCallback.endOfGame(title);

    doPause();
    // todo - find a way to terminate gamegrid
  }

  private int countPillsAndItems() {
    return getRemainingLoot().size();
  }

  public void updateActor(Actor actor, ActorType type) {
    // Attempt collision with all collidables
    for (ActorCollidable element : collidables) {
      element.checkAndCollide(actor, type);
    }

    // Update game title if player
    if (type == ActorType.Player) {
      String title = "[PacMan in the TorusVerse] Current score: " + pacActorSingleton.getScore();
      setTitle(title);

      // Check if Pacman has won
      if (getRemainingLoot().size() == 0) {
        pacActorSingleton.winGame();
      }
    }

    // Check for player collision if monster
    else if (type == ActorType.Monster) {
      ((Monster)actor).checkAndCollide(pacActorSingleton, ActorType.Player);
    }
  }

  // Closes the game. Use with caution.
  public void endGame() {
    this.getFrame().dispose();
  }

  // -- Getters --
  public GameCallback getGameCallback() {
    return gameCallback;
  }
  public int getNumHorzCells(){
    return this.nbHorzCells;
  }
  public int getNumVertCells(){
    return this.nbVertCells;
  }
  public int getSeed() {
    return this.seed;
  }
  public ArrayList<PortalPair> getPortals() {
    return this.portalPairs;
  }
  public GameState getGameState() {
    return (pacActorSingleton.getGameState());
  }

  /**
   * @return An ArrayList of the Locations of the remaining loot still in a
   *          game and visible.
   */
  public ArrayList<Location> getRemainingLoot() {
    ArrayList<Location> locations = new ArrayList<>();
    for (Item item : loot) {
      if (!item.getEaten()) {
        locations.add(item.getLocation());
      }
    }
    return locations;
  }

}

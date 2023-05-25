package pacman;

import ch.aplu.jgamegrid.*;
import pacman.utility.GameCallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;


public class Game extends GameGrid
{
  protected PacManGameGrid grid = new PacManGameGrid(nbHorzCells, nbVertCells);
  // === Actors ===
  // Retrieve singleton pacActor, setting current game to this instance
  protected PacActorSingleton pacActorSingleton = PacActorSingleton.getInstance(this);
  private final ArrayList<ActorCollidable> collidables = new ArrayList<>();

  private ArrayList<Monster> monsters = new ArrayList<>();
  private ArrayList<Item> items = new ArrayList<>();
  private ArrayList<Item> loot = new ArrayList<>();       // Subset of items
  private ArrayList<PortalPair> portalPairs = new ArrayList<>();

  private GameCallback gameCallback;
  private int seed;
  private ArrayList<Location> propertyPillLocations = new ArrayList<>();
  private static ArrayList<Location> propertyGoldLocations = new ArrayList<>();

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
    pacActorSingleton.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
    addActor(pacActorSingleton, map.getPacLocation());

    // === Load items ===
    for (Location loc : map.getGold()) {
      Gold gold = new Gold(this, bg, loc);
      this.items.add(gold);
      this.loot.add(gold);
    }
    for (Location loc : map.getPills()) {
      Pill pill = new Pill(this, bg, loc);
      this.items.add(pill);
      this.loot.add(pill);
    }
    for (Location loc : map.getIce()) {
      Ice ice = new Ice(this, bg, loc);
      this.items.add(ice);
    }
    this.collidables.addAll(items);

    // === Load monsters ===
    // Monster array and factory to reduce redundant code later
    MonsterFactorySingleton mFactory = MonsterFactorySingleton.getMonsterFactory();
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

    // Setup Random seeds and speed for pacActor
    this.seed = Integer.parseInt(properties.getProperty("seed"));

    pacActorSingleton.setSeed(seed);
    pacActorSingleton.setSlowDown(3);

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

      hasPacmanEatAllPills = pacActorSingleton.getNbPills() >= maxPillsAndItems;
      // todo - replace with pacman gamestate some how
      delay(10);
    } while(pacActorSingleton.getGameState() != GameState.Lose && !hasPacmanEatAllPills);
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
    } else if (hasPacmanEatAllPills) {
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
      String title = "[PacMan in the Multiverse] Current score: " + pacActorSingleton.getScore();
      setTitle(title);
    }

    // Check for player collision if monster
    else if (type == ActorType.Monster) {
      ((Monster)actor).checkAndCollide(pacActorSingleton, ActorType.Player);
    }
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

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

  // Retrieve singleton pacActor, setting current game to this instance
  protected PacActor pacActor = PacActor.getPacActor(this);
  private ArrayList<ActorCollidable> collidables = new ArrayList<>();

  private ArrayList<Monster> monsters = new ArrayList<>();
  private ArrayList<Item> items = new ArrayList<>();
  private ArrayList<PortalPair> portalPairs = new ArrayList<>();

  private ArrayList<Location> pillAndItemLocations = new ArrayList<Location>();
  private ArrayList<Ice> iceCubes = new ArrayList<Ice>();
  private ArrayList<Gold> goldPieces = new ArrayList<Gold>();
  private ArrayList<Pill> pills = new ArrayList<Pill>();

  private GameCallback gameCallback;
  private Properties properties;
  private int seed;
  private ArrayList<Location> propertyPillLocations = new ArrayList<>();
  private static ArrayList<Location> propertyGoldLocations = new ArrayList<>();

  public Game(GameMap map, GameCallback gameCallback, Properties properties)
  {
    //Setup game
    super(map.getWidth(), map.getHeight(), 20, false);
    this.gameCallback = gameCallback;
    this.properties = properties;
    setSimulationPeriod(100);
    setTitle("[PacMan in the TorusVerse]");

    // Draw initial board
    GGBackground bg = getBg();
    drawGrid(bg);


    // === Load walls ===
    for (Location loc : map.getWalls()) {
      bg.fillCell(loc, Color.gray);
    }

    // === Load portals ===
    // Iterate through portaltype, locationlist pairs
    for (Map.Entry<PortalType, ArrayList<Location>> entry : map.getPortals().entrySet()) {
      // Iterate through location pairs and add portals to the game
      PortalPair portal = new PortalPair(this, entry.getKey());
      // Already asserted map valid, so guaranteed 2 portals
      portal.placePortal(entry.getValue().get(0));
      portal.placePortal(entry.getValue().get(1));
      this.portalPairs.add(portal);
    }
    this.collidables.addAll(portalPairs);

    // === Load pacActor ===
    pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
    addActor(pacActor, map.getPacLocation());

    // === Load items ===
    for (Location loc : map.getGold()) {
      Gold gold = new Gold(this, bg, loc);
      this.items.add(gold);
    }
    for (Location loc : map.getPills()) {
      Pill pill = new Pill(this, bg, loc);
      this.items.add(pill);
    }
    for (Location loc : map.getIce()) {
      Ice ice = new Ice(this, bg, loc);
      this.items.add(ice);
    }
    this.collidables.addAll(items);

    // === Load monsters ===
    // Monster array and factory to reduce redundant code later
    MonsterFactory mFactory = MonsterFactory.getMonsterFactory();
    // Iterate through monstertype, locationlist pairs
    for (Map.Entry<MonsterType, ArrayList<Location>> entry : map.getMonsters().entrySet()) {
      // Iterate through locations and add monsters to the game
      for (Location loc : entry.getValue()) {
        Monster monster = mFactory.makeMonster(this, entry.getKey());
        this.monsters.add(monster);
        addActor(monster, loc);
      }
    }


    loadPillAndItemsLocations();

    // Setup Random seeds and speed for pacActor
    this.seed = Integer.parseInt(properties.getProperty("seed"));

    pacActor.setSeed(seed);
    pacActor.setSlowDown(3);

    // Set pacActor movement and other monster uniques
    addKeyRepeatListener(pacActor);
    setKeyRepeatPeriod(150);

    setupActorLocations();

//    collidables.addAll(pills);
//    collidables.addAll(goldPieces);
//    collidables.addAll(iceCubes);


    //Run the game
    doRun();
    show();
    // Loop to look for collision in the application thread
    // This makes it improbable that we miss a hit
    boolean hasPacmanEatAllPills;
    setupPillAndItemsLocations();
    int maxPillsAndItems = countPillsAndItems();

    do {
      Location pacLocation = pacActor.getLocation();
      // Iterate through monster list to check if collision has occurred
      for (Monster monster : monsters) {
        monster.checkAndCollide(pacActor, ActorType.Player);
      }
      hasPacmanEatAllPills = pacActor.getNbPills() >= maxPillsAndItems;
      delay(10);
    } while(pacActor.getGameState() != GameState.Lose && !hasPacmanEatAllPills);
    delay(120);

    Location loc = pacActor.getLocation();
    for (Monster monster : monsters) {
      monster.setStopMoving(true);
    }
    pacActor.removeSelf();

    String title = "";
    if (pacActor.getGameState() == GameState.Lose) {
      bg.setPaintColor(Color.red);
      title = "GAME OVER";
      addActor(new Actor("sprites/explosion3.gif"), loc);
    } else if (hasPacmanEatAllPills) {
      bg.setPaintColor(Color.yellow);
      title = "YOU WIN";
    }
    setTitle(title);
    gameCallback.endOfGame(title);

    doPause();
  }

  private void setupActorLocations() {
    // Read in location of PacMan actor

//    // Read in locations of all monsters, even if not used
//    ArrayList<int[]> locationTuples = new ArrayList<>();
//    for (int i=0; i<MonsterType.values().length; i++) {
//      String[] locations = this.properties.getProperty(MonsterType.values()[i].name()+".location").split(",");
//      locationTuples.add(new int[]{Integer.parseInt(locations[0]), Integer.parseInt(locations[1])});
//    }

//    int X = 0, Y = 1;
//    for (int i=0; i<this.monsters.size(); i++) {
//      addActor(this.monsters.get(i), new Location(locationTuples.get(i)[X], locationTuples.get(i)[Y]), Location.NORTH);
//    }
  }

  private int countPillsAndItems() {
    int pillsAndItemsCount = 0;
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a == 1 && propertyPillLocations.size() == 0) { // Pill
          pillsAndItemsCount++;
        } else if (a == 3 && propertyGoldLocations.size() == 0) { // Gold
          pillsAndItemsCount++;
        }
      }
    }
    if (propertyPillLocations.size() != 0) {
      pillsAndItemsCount += propertyPillLocations.size();
    }

    if (propertyGoldLocations.size() != 0) {
      pillsAndItemsCount += propertyGoldLocations.size();
    }

    return pillsAndItemsCount;
  }

  private void loadPillAndItemsLocations() {
    String pillsLocationString = properties.getProperty("Pills.location");
    if (pillsLocationString != null) {
      String[] singlePillLocationStrings = pillsLocationString.split(";");
      for (String singlePillLocationString: singlePillLocationStrings) {
        String[] locationStrings = singlePillLocationString.split(",");
        propertyPillLocations.add(new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1])));
      }
    }

    String goldLocationString = properties.getProperty("Gold.location");
    if (goldLocationString != null) {
      String[] singleGoldLocationStrings = goldLocationString.split(";");
      for (String singleGoldLocationString: singleGoldLocationStrings) {
        String[] locationStrings = singleGoldLocationString.split(",");
        propertyGoldLocations.add(new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1])));
      }
    }
  }

  private void setupPillAndItemsLocations() {
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a == 1 && propertyPillLocations.size() == 0) {
          pillAndItemLocations.add(location);
        }
        if (a == 3 &&  propertyGoldLocations.size() == 0) {
          pillAndItemLocations.add(location);
        }
        if (a == 4) {
          pillAndItemLocations.add(location);
        }
      }
    }


    if (propertyPillLocations.size() > 0) {
      for (Location location : propertyPillLocations) {
        pillAndItemLocations.add(location);
      }
    }
    if (propertyGoldLocations.size() > 0) {
      for (Location location : propertyGoldLocations) {
        pillAndItemLocations.add(location);
      }
    }
  }

  private void drawGrid(GGBackground bg)
  {

    bg.clear(Color.lightGray);
    bg.setPaintColor(Color.gray);
//    for (int y = 0; y < nbVertCells; y++)
//    {
//      for (int x = 0; x < nbHorzCells; x++)
//      {
//        Location location = new Location(x, y);
//        int a = grid.getCell(location);
//        if (a > 0)
//          bg.fillCell(location, Color.lightGray);
//        if (a == 1 && propertyPillLocations.size() == 0) { // Pill
//          this.pills.add(new Pill(this, bg, location));
//        } else if (a == 3 && propertyGoldLocations.size() == 0) { // Gold
//          this.goldPieces.add(new Gold(this, bg, location));
//        } else if (a == 4) {
//          this.iceCubes.add(new Ice(this, bg, location));
//        }
//      }
//    }
//
//    for (Location location : propertyPillLocations) {
//      this.pills.add(new Pill(this, bg, location));
//    }
//
//    for (Location location : propertyGoldLocations) {
//      this.goldPieces.add(new Gold(this, bg, location));
//    }
  }

  public void updateActor(Actor actor, ActorType type) {
    // Attempt collision with all collidables
    for (ActorCollidable element : collidables) {
      element.checkAndCollide(actor, type);
    }

    // Update game title if player
    if (type == ActorType.Player) {
      String title = "[PacMan in the Multiverse] Current score: " + pacActor.getScore();
      setTitle(title);
    }
  }

  // -- Getters --
  public GameCallback getGameCallback() {
    return gameCallback;
  }
  public ArrayList<Location> getPillAndItemLocations() {
    return pillAndItemLocations;
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

}

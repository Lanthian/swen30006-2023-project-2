package pacman;

import ch.aplu.jgamegrid.*;
import pacman.utility.GameCallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.Properties;

public class Game extends GameGrid
{
  private final static int nbHorzCells = 20;
  private final static int nbVertCells = 11;
  protected PacManGameGrid grid = new PacManGameGrid(nbHorzCells, nbVertCells);

  protected PacActor pacActor = PacActor.getPacActor(this);
  private ArrayList<Monster> monsters = new ArrayList<>();

  private ArrayList<Location> pillAndItemLocations = new ArrayList<Location>();
  private ArrayList<Ice> iceCubes = new ArrayList<Ice>();
  public ArrayList<Gold> getGoldPieces() {
    return goldPieces;
  }
  private ArrayList<Gold> goldPieces = new ArrayList<Gold>();
  private ArrayList<Pill> pills = new ArrayList<Pill>();

  private GameCallback gameCallback;
  private Properties properties;
  private int seed = 30006;
  private ArrayList<Location> propertyPillLocations = new ArrayList<>();
  private static ArrayList<Location> propertyGoldLocations = new ArrayList<>();

  public Game(GameCallback gameCallback, Properties properties)
  {
    //Setup game
    super(nbHorzCells, nbVertCells, 20, false);
    this.gameCallback = gameCallback;
    this.properties = properties;
    setSimulationPeriod(100);
    setTitle("[PacMan in the TorusVerse]");

    //Setup for auto test
    pacActor.setPropertyMoves(properties.getProperty("PacMan.move"));
    pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
    loadPillAndItemsLocations();

    GGBackground bg = getBg();
    drawGrid(bg);

    // Setup Random seeds and speed for pacActor
    seed = Integer.parseInt(properties.getProperty("seed"));
    pacActor.setSeed(seed);
    pacActor.setSlowDown(3);

    // Monster array just to reduce redundant code below
    MonsterFactory mFactory = MonsterFactory.getMonsterFactory();
    this.monsters.add(mFactory.makeMonster(this, MonsterType.TX5));
    this.monsters.add(mFactory.makeMonster(this, MonsterType.Troll));

    // Set pacActor movement and other monster uniques
    addKeyRepeatListener(pacActor);
    setKeyRepeatPeriod(150);

    setupActorLocations();


    //Run the game
    doRun();
    show();
    // Loop to look for collision in the application thread
    // This makes it improbable that we miss a hit
    boolean hasPacmanBeenHit = false;
    boolean hasPacmanEatAllPills;
    setupPillAndItemsLocations();
    int maxPillsAndItems = countPillsAndItems();

    do {
      Location pacLocation = pacActor.getLocation();
      // Iterate through monster list to check if collision has occurred
      for (Monster monster : monsters) {
        if (monster.getLocation().equals(pacLocation)) hasPacmanBeenHit = true;
      }
      hasPacmanEatAllPills = pacActor.getNbPills() >= maxPillsAndItems;
      delay(10);
    } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
    delay(120);

    Location loc = pacActor.getLocation();
    for (Monster monster : monsters) {
      monster.setStopMoving(true);
    }
    pacActor.removeSelf();

    String title = "";
    if (hasPacmanBeenHit) {
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
    String[] pacManLocations = this.properties.getProperty("PacMan.location").split(",");
    int pacManX = Integer.parseInt(pacManLocations[0]);
    int pacManY = Integer.parseInt(pacManLocations[1]);

    // Read in locations of all monsters, even if not used
    ArrayList<int[]> locationTuples = new ArrayList<>();
    for (int i=0; i<MonsterType.values().length; i++) {
      String[] locations = this.properties.getProperty(MonsterType.values()[i].name()+".location").split(",");
      locationTuples.add(new int[]{Integer.parseInt(locations[0]), Integer.parseInt(locations[1])});
    }

    // Add pacActor and desired monsters
    addActor(pacActor, new Location(pacManX, pacManY));
    int X = 0, Y = 1;
    for (int i=0; i<this.monsters.size(); i++) {
      addActor(this.monsters.get(i), new Location(locationTuples.get(i)[X], locationTuples.get(i)[Y]), Location.NORTH);
    }

    // Add portals
//    PortalPair golds = new PortalPair(this, PortalType.DarkGold);
//    golds.placePortal(new Location(2, 2));
//    golds.placePortal(new Location(4, 4));
//    addActor(golds.portal1, golds.portal1.getLocation());
//    addActor(golds.portal2, golds.portal2.getLocation());
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
    bg.clear(Color.gray);
    bg.setPaintColor(Color.white);
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a > 0)
          bg.fillCell(location, Color.lightGray);
        if (a == 1 && propertyPillLocations.size() == 0) { // Pill
          this.pills.add(new Pill(this, bg, location));
        } else if (a == 3 && propertyGoldLocations.size() == 0) { // Gold
          this.goldPieces.add(new Gold(this, bg, location));
        } else if (a == 4) {
          this.iceCubes.add(new Ice(this, bg, location));
        }
      }
    }

    for (Location location : propertyPillLocations) {
      this.pills.add(new Pill(this, bg, location));
    }

    for (Location location : propertyGoldLocations) {
      this.goldPieces.add(new Gold(this, bg, location));
    }
  }


  /**
   * Searches a location for a specific kind of item.
   * @param type A string of type {pills, gold, ice} that determines the item
   *             type searched for.
   * @param location The Location searched for desired item.
   * @return null if not found, desired Item if found.
   */
  public Item findItem(String type,Location location) {
    ArrayList<Item> items = new ArrayList<>();
    switch (type) {
      case "pills":
        items.addAll(this.pills);
        break;
      case "gold":
        items.addAll(this.goldPieces);
        break;
      case "ice":
        items.addAll(this.iceCubes);
        break;
      default:
        // Desired type unknown
        return null;
    }

    // Search for item of type at given location
    for (Item item : items) {
      if (location.equals(item.getLocation())) {
        return item;
      }
    }

    // Otherwise, item not found -- return null
    return null;
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

package pacman;

public enum PortalType {
    DarkGold,
    DarkGray,
    White,
    Yellow;

    public String getImageName() {
        switch (this) {
            case DarkGold: return "portalDarkGoldTile.png";
            case DarkGray: return "portalDarkGrayTile.png";
            case White: return "portalWhiteTile.png";
            case Yellow: return "portalYellowTile.png";
            default: {
                assert false;
            }
        }
        return null;
    }
}

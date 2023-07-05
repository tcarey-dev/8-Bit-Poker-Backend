package learn.poker.models;

public class Room {

    private int roomId;
    private double stake;
    private int seats;
    private int playerCount;
    private String deckId;
    private Game game;

    public Room(int roomId, double stake, int seats, String deckId, Game game) {
        this.roomId = roomId;
        this.stake = stake;
        this.seats = seats;
        this.deckId = deckId;
        this.game = game;
    }

    public Room(){}

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public double getStake() {
        return stake;
    }

    public void setStake(double stake) {
        this.stake = stake;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room that = (Room) o;
        return roomId == that.roomId;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

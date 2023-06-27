package learn.poker.models;

import java.util.List;

public class Game {

    private int gameId;
    private int pot;
    private String winner;
    private Room room;
    private Board board;
    private List<Player> players;

    public Game(int gameId, int pot, String winner, Room room, Board board, List<Player> players) {
        this.gameId = gameId;
        this.pot = pot;
        this.winner = winner;
        this.room = room;
        this.board = board;
        this.players = players;
    }

    public Game(){}

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getPot() {
        return pot;
    }

    public void setPot(int pot) {
        this.pot = pot;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}

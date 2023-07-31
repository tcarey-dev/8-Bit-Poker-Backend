package learn.poker.models;

import java.util.List;
import java.util.Objects;

public class Game {

    private int gameId;
    private double pot;
    private String winner;
    private double betAmount;
    private Action lastAction;
    private Board board;
    private List<Player> players;

    public Game(int gameId, double pot, String winner, double betAmount, Action lastAction, Board board, List<Player> players) {
        this.gameId = gameId;
        this.pot = pot;
        this.winner = winner;
        this.betAmount = betAmount;
        this.lastAction = lastAction;
        this.board = board;
        this.players = players;
    }

    public Game(int gameId, double pot, String winner, Board board, List<Player> players) {
        this.gameId = gameId;
        this.pot = pot;
        this.winner = winner;
        this.board = board;
        this.players = players;
    }

    public Game(double pot, String winner, Board board, List<Player> players) {
        this.pot = pot;
        this.winner = winner;
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

    public double getPot() {
        return pot;
    }

    public void setPot(double pot) {
        this.pot = pot;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
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

    public Action getLastAction() {
        return lastAction;
    }

    public void setLastAction(Action lastAction) {
        this.lastAction = lastAction;
    }

    public double getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(double betAmount) {
        this.betAmount = betAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gameId == game.gameId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId);
    }
}

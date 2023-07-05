package learn.poker.models;

import java.util.ArrayList;

public class PokerApiWinnerResponse {
    public ArrayList<Winner> winners;
    public ArrayList<PokerApiPlayer> players;

    public PokerApiWinnerResponse(ArrayList<Winner> winners, ArrayList<PokerApiPlayer> players) {
        this.winners = winners;
        this.players = players;
    }

    public PokerApiWinnerResponse() {
    }

    public ArrayList<Winner> getWinners() {
        return winners;
    }

    public void setWinners(ArrayList<Winner> winners) {
        this.winners = winners;
    }

    public ArrayList<PokerApiPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<PokerApiPlayer> players) {
        this.players = players;
    }
}

package learn.poker.models;

import java.util.List;

public class Board {

    private int boardId;
    private List<Card> flop;
    private Card turn;
    private Card river;

    public Board(int boardId, List<Card> flop, Card turn, Card river) {
        this.boardId = boardId;
        this.flop = flop;
        this.turn = turn;
        this.river = river;
    }

    public Board(){}

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public List<Card> getFlop() {
        return flop;
    }

    public void setFlop(List<Card> flop) {
        this.flop = flop;
    }

    public Card getTurn() {
        return turn;
    }

    public void setTurn(Card turn) {
        this.turn = turn;
    }

    public Card getRiver() {
        return river;
    }

    public void setRiver(Card river) {
        this.river = river;
    }
}

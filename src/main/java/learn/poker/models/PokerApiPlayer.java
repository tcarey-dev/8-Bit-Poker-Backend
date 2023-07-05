package learn.poker.models;

public class PokerApiPlayer {
    public String cards;
    public String hand;
    public String result;

    public PokerApiPlayer(String cards, String hand, String result) {
        this.cards = cards;
        this.hand = hand;
        this.result = result;
    }

    public PokerApiPlayer() {
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public String getHand() {
        return hand;
    }

    public void setHand(String hand) {
        this.hand = hand;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

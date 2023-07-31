package learn.poker.models;

import java.util.Objects;

public class Winner {
    public String cards;
    public String hand;
    public String result;

    public Winner(String cards, String hand, String result) {
        this.cards = cards;
        this.hand = hand;
        this.result = result;
    }

    public Winner() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Winner winner = (Winner) o;
        return Objects.equals(cards, winner.cards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cards);
    }
}

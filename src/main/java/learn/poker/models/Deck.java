package learn.poker.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Deck {
    @JsonProperty("success")
    public boolean getSuccess() {
        return this.success; }
    public void setSuccess(boolean success) {
        this.success = success; }
    boolean success;
    @JsonProperty("deck_id")
    public String getDeck_id() {
        return this.deck_id; }
    public void setDeck_id(String deck_id) {
        this.deck_id = deck_id; }
    String deck_id;
    @JsonProperty("cards")
    public ArrayList<PokerApiCard> getCards() {
        return this.cards; }
    public void setCards(ArrayList<PokerApiCard> cards) {
        this.cards = cards; }
    ArrayList<PokerApiCard> cards;
    @JsonProperty("remaining")
    public int getRemaining() {
        return this.remaining; }
    public void setRemaining(int remaining) {
        this.remaining = remaining; }
    int remaining;
}

package learn.poker.domain;

import learn.poker.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeckService {
    @Autowired
    private WebClient.Builder client;
    private static final String api_url = "https://deckofcardsapi.com/api/deck";

    public List<Card> drawCards(int numCards, Room room) {
        String deckId = room.getDeckId();
        Deck deck = client.build().get()
                .uri(String.format("%s/%s/draw/?count=%s", api_url, deckId, numCards)).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Deck.class)
                .block();

        if (deck == null) {
            return null;
        }

        List<PokerApiCard> playerCards = deck.getCards();

        List<Card> cards = playerCards.stream().map(pokerApiCard -> {
            String code = pokerApiCard.getCode();
            return Card.getCardFromAbbreviation(code);
        }).toList();

        List<Card> existing = new ArrayList<>();
        if (room.getGame().getBoard() != null){
            Board board = room.getGame().getBoard();
            if (board.getFlop() != null){
                existing.addAll(board.getFlop());
            }
            if (board.getTurn() != null){
                existing.add(board.getTurn());
            }
            if (board.getRiver() != null){
                existing.add(board.getRiver());
            }
        }
        if (room.getGame().getPlayers() != null) {
            List<Player> players = room.getGame().getPlayers();
            if (players.get(0) != null && players.get(0).getHoleCards() != null){
                existing.addAll(players.get(0).getHoleCards());
            }
            if (players.get(1) != null && players.get(1).getHoleCards() != null){
                existing.addAll(players.get(1).getHoleCards());
            }
        }

        Card duplicate = cards.stream().filter(existing::contains).findFirst().orElse(Card.EMPTY);

        if (duplicate == Card.EMPTY) {
            return cards;
        } else {
            return drawCards(numCards, room);
        }
    }

    public Deck shuffle(String deckId) {
        return client.build().get()
                .uri(String.format("%s/%s/shuffle/", api_url, deckId)).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Deck.class)
                .block();
    }

    public String getDeckId() {
        return client.build().get()
                .uri(api_url + "/new/shuffle/?deck_count=1").accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Deck.class)
                .block()
                .getDeck_id();
    }

}

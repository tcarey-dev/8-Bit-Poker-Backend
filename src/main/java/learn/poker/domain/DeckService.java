package learn.poker.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import learn.poker.models.Deck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DeckService {
    @Autowired    private WebClient.Builder client;
    private static String api_url = "https://deckofcardsapi.com/api/deck";

    public Deck drawCards(int cards, String deckId) {
        return client.build().get()
                .uri(String.format("%s/%s/draw/?count=%s", api_url, deckId, cards)).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Deck.class)
                .block();
    }

    public Deck shuffle(String deckId) {
        return client.build().get()
                .uri(String.format("/%s/shuffle/", api_url, deckId)).accept(MediaType.APPLICATION_JSON)
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

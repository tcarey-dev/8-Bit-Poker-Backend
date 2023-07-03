package learn.poker.domain;

import learn.poker.models.Player;
import learn.poker.models.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WinnerService {
    @Autowired
    private WebClient.Builder client;
    private static String api_url = "https://api.pokerapi.dev/v1/winner/texas_holdem";

    public String determineWinner(Room room) {

        return client.build().get()
                .uri(String.format("%s?cc=%s,%s,%s,%s,%s&pc[]=%s,%s&pc=[]%s%s", api_url,
                        room.getGame().getBoard().getFlop().get(0)),
                        room.getGame().getBoard().getFlop().get(1),
                        room.getGame().getBoard().getFlop().get(2),
                        room.getGame().getBoard().getTurn(),
                        room.getGame().getBoard().getRiver(),
                        room.getGame().getPlayers().get(0).getHoleCards().get(0),
                        room.getGame().getPlayers().get(0).getHoleCards().get(1),
                        room.getGame().getPlayers().get(1).getHoleCards().get(0),
                        room.getGame().getPlayers().get(1).getHoleCards().get(1))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}

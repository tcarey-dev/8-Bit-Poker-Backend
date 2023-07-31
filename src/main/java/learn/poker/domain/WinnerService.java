package learn.poker.domain;

import learn.poker.models.Card;
import learn.poker.models.Player;
import learn.poker.models.PokerApiWinnerResponse;
import learn.poker.models.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WinnerService {
    @Autowired
    private WebClient.Builder client;
    private static String api_url = "https://api.pokerapi.dev/v1/winner/texas_holdem";

    public Player determineWinner(Room room) {
        PokerApiWinnerResponse result = getWinnerFromApi(room);
        String winningHandCode = result.winners.get(0).getCards();
        String winningHand = Arrays.stream(winningHandCode.split(",")).map(c -> {
                if (c.substring(0, 2).equals("10")) {
                    c = c.substring(2);
                    c = "0" + c;
                }
                return c;
            }).collect(Collectors.joining(","));

        for (Player player : room.getGame().getPlayers()) {
            List<Card> holeCards = player.getHoleCards();
            String hand = holeCards.stream().map(Card::getAbbr)
                    .collect(Collectors.joining(","));

            if (hand.equalsIgnoreCase(winningHand)){
                return player;
            }
        }

        return null;
    }

    public PokerApiWinnerResponse getWinnerFromApi(Room room) {
        String flop1 = room.getGame().getBoard().getFlop().get(0).getAbbr();
        String flop2 = room.getGame().getBoard().getFlop().get(1).getAbbr();
        String flop3 = room.getGame().getBoard().getFlop().get(2).getAbbr();
        String turn = room.getGame().getBoard().getTurn().getAbbr();
        String river = room.getGame().getBoard().getRiver().getAbbr();
        String holeCard1 = room.getGame().getPlayers().get(0).getHoleCards().get(0).getAbbr();
        String holeCard2 = room.getGame().getPlayers().get(0).getHoleCards().get(1).getAbbr();
        String holeCard3 = room.getGame().getPlayers().get(1).getHoleCards().get(0).getAbbr();
        String holeCard4 = room.getGame().getPlayers().get(1).getHoleCards().get(1).getAbbr();

        List<String> codes = new ArrayList<>(List.of(
                flop1,
                flop2,
                flop3,
                turn,
                river,
                holeCard1,
                holeCard2,
                holeCard3,
                holeCard4
        ));

        List<String> cards = codes.stream().map(c -> {
            if (c.substring(0, 1).equals("0")) {
                c = "1" + c;
            }
            return c;
        }).toList();

        String preparedUri = String.format("%s?cc=%s,%s,%s,%s,%s&pc[]=%s,%s&pc[]=%s,%s", api_url,
                cards.get(0),
                cards.get(1),
                cards.get(2),
                cards.get(3),
                cards.get(4),
                cards.get(5),
                cards.get(6),
                cards.get(7),
                cards.get(8));
        System.out.println(preparedUri);

        return client.build().get()
                .uri(String.format("%s?cc=%s,%s,%s,%s,%s&pc[]=%s,%s&pc[]=%s,%s", api_url,
                        cards.get(0),
                        cards.get(1),
                        cards.get(2),
                        cards.get(3),
                        cards.get(4),
                        cards.get(5),
                        cards.get(6),
                        cards.get(7),
                        cards.get(8)))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PokerApiWinnerResponse.class)
                .retry(3)
                .block();

    }
}

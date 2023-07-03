package learn.poker.domain;

import learn.poker.models.Player;
import learn.poker.models.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WinnerService {
    @Autowired
    private WebClient.Builder client;
    private static String api_url = "https://api.pokerapi.dev/v1/winner/texas_holdem?";

    public String determineWinner(Room room) {
//        String.format("%?cc=%s,%s,%s,%s,%s&pc[]=%s,%s&pc=[]%s%s",   )
        return null;
    }



}

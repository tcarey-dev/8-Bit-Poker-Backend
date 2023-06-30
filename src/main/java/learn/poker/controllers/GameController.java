package learn.poker.controllers;

import learn.poker.domain.GameService;
import learn.poker.domain.Result;
import learn.poker.domain.RoomService;
import learn.poker.models.Greeting;
import learn.poker.models.HelloMessage;
import learn.poker.models.Player;
import learn.poker.models.Room;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.util.HtmlUtils;

@Controller
@CrossOrigin(origins = {"http://localhost:3000"})
public class GameController {

    private final GameService gameService;
    private final RoomService roomService;

    public GameController(GameService gameService, RoomService roomService) {
        this.gameService = gameService;
        this.roomService = roomService;
    }

    // TODO: this endpoint is for testing websockets only, to be deleted
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @MessageMapping("/bet")
    @SendTo("/topic/game")
    public Room bet(Room room) {
        return roomService.findById(room.getRoomId());
    }

    // TODO add error messages for unhappy paths

    @MessageMapping("/init")
    @SendTo("/topic/game")
    public Room init(Room room) {
        Result<Room> result = gameService.init(room);
        if (result.isSuccess()) {
            return result.getPayload();
        } else {
            return null;
        }
    }

    @MessageMapping("/add-players")
    @SendTo("/topic/game")
    public Room addPlayer(Room room) {
        Result<Room> result = roomService.update(room);
        if (result.isSuccess()) {
            return result.getPayload();
        } else {
            return null;
        }
    }


}

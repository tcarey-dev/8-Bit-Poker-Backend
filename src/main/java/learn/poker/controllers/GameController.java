package learn.poker.controllers;

import learn.poker.domain.GameService;
import learn.poker.domain.Result;
import learn.poker.domain.RoomService;
import learn.poker.models.*;
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

    @MessageMapping("/init")
    @SendTo("/topic/game")
    public Room init(Room room) {
        Result<Room> result = gameService.init(room);
        if (result.isSuccess()) {
            return result.getPayload();
        } else {
            return null;// TODO
        }
    }

    @MessageMapping("/add-players")
    @SendTo("/topic/game")
    public Room addPlayer(Room room) {
        Game game = room.getGame();
        Result<Game> gameResult = gameService.update(game);
        if (gameResult.isSuccess()) {
            return roomService.findById(room.getRoomId());
        } else {
            return null;// TODO
        }
    }

    @MessageMapping("/start-game")
    @SendTo("/topic/game")
    public Room startGame(Room room) {
        Result<Room> roomResult = gameService.start(room);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            return null; // TODO
        }
    }

    @MessageMapping("/bet")
    @SendTo("/topic/game")
    public Room bet(Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.BET);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            return null; // TODO
        }
    }

//    @MessageMapping("/check")
//    @SendTo("/topic/game")
//    public Room check(Room room) {
//        Result<Room> roomResult = gameService.handleAction(room, Action.CHECK);
//        if (roomResult.isSuccess()) {
//            return roomResult.getPayload();
//        } else {
//            return null; // TODO
//        }
//    }
//
//    @MessageMapping("/raise")
//    @SendTo("/topic/game")
//    public Room raise(Room room) {
//        Result<Room> roomResult = gameService.handleAction(room, Action.RAISE);
//        if (roomResult.isSuccess()) {
//            return roomResult.getPayload();
//        } else {
//            return null; // TODO
//        }
//    }
//
    @MessageMapping("/fold")
    @SendTo("/topic/game")
    public Room fold(Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.FOLD);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            return null; // TODO
        }
    }
//
//    @MessageMapping("/end-game")
//    @SendTo("/topic/game")
//    public Room endGame(Room room) {
//        Result<Room> roomResult = gameService.end(room);
//        if (roomResult.isSuccess()) {
//            return roomResult.getPayload();
//        } else {
//            return null; // TODO
//        }
//    }



}

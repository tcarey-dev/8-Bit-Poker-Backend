package learn.poker.controllers;

import learn.poker.domain.GameService;
import learn.poker.domain.Result;
import learn.poker.domain.RoomService;
import learn.poker.models.*;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
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

    @MessageExceptionHandler
    @SendTo("/topic/{roomId}/errors")
    public String handleException(Throwable exception) {
        return "server exception: " + exception.getMessage();
    }

    @MessageMapping("/init")
    @SendTo("/topic/game")
    public Room init(Room room) {
        Result<Room> result = gameService.init(room);
        if (result.isSuccess()) {
            return result.getPayload();
        } else {
            throw new RuntimeException("Unable to initialize the game.");
        }
    }

    @MessageMapping("/add-players")
    @SendTo("/topic/game")
    public Room addPlayer(Room room) {
        Result<Room> roomResult = gameService.addPlayer(room);
        if (roomResult.isSuccess()) {
            return room;
        } else {
            throw new RuntimeException("Unable to add player the game.");
        }
    }

    @MessageMapping("/start-game")
    @SendTo("/topic/game")
    public Room startGame(Room room) {
        Result<Room> roomResult = gameService.start(room);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            throw new RuntimeException("Unable to start the game.");
        }
    }

    @MessageMapping("/bet")
    @SendTo("/topic/game")
    public Room bet(Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.BET);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            throw new RuntimeException("Bet failed.");
        }
    }

    @MessageMapping("/check")
    @SendTo("/topic/game")
    public Room check(Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.CHECK);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            return null; // TODO
        }
    }

    @MessageMapping("/raise")
    @SendTo("/topic/game")
    public Room raise(Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.RAISE);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            return null; // TODO
        }
    }

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

    @MessageMapping("/call")
    @SendTo("/topic/game")
    public Room call(Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.CALL);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            return null; // TODO
        }
    }


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

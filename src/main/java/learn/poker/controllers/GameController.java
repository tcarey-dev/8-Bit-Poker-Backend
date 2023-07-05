package learn.poker.controllers;

import learn.poker.domain.GameService;
import learn.poker.domain.Result;
import learn.poker.domain.RoomService;
import learn.poker.models.*;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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
    @SendTo("/topic/errors")
    public String handleException(Throwable exception) {
        return "server exception: " + exception.getMessage();
    }

    @MessageMapping("/init/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public Room init(@DestinationVariable int roomId, Room room) {
        Result<Room> result = gameService.init(room);
        if (result.isSuccess()) {
            return result.getPayload();
        } else {
            throw new RuntimeException("Unable to initialize the game.");
        }
    }

    @MessageMapping("/add-players/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public Room addPlayer(@DestinationVariable int roomId, Room room) {
        Result<Room> roomResult = gameService.addPlayer(room);
        if (roomResult.isSuccess()) {
            return room;
        } else {
            throw new RuntimeException("Unable to add player the game.");
        }
    }

    @MessageMapping("/start-game/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public Room startGame(@DestinationVariable int roomId, Room room) {
        Result<Room> roomResult = gameService.start(room);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            throw new RuntimeException("Unable to start the game.");
        }
    }

    @MessageMapping("/bet/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public Room bet(@DestinationVariable int roomId, Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.BET);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            throw new RuntimeException("Bet failed.");
        }
    }

    @MessageMapping("/check/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public Room check(@DestinationVariable int roomId, Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.CHECK);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            return null; // TODO
        }
    }

    @MessageMapping("/raise/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public Room raise(@DestinationVariable int roomId, Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.RAISE);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            return null; // TODO
        }
    }

    @MessageMapping("/fold/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public Room fold(@DestinationVariable int roomId, Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.FOLD);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            return null; // TODO
        }
    }

    @MessageMapping("/call/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public Room call(@DestinationVariable int roomId, Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.CALL);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            return null; // TODO
        }
    }


//    @MessageMapping("/leave-game/{roomId}")
//    @SendTo("/topic/game/{roomId}")
//    public Room leaveGame(@DestinationVariable int roomId, Room room) {
//        Result<Room> roomResult = gameService.leaveGame(room);
//        if (roomResult.isSuccess()) {
//            return roomResult.getPayload();
//        } else {
//            return null; // TODO
//        }
//    }



}

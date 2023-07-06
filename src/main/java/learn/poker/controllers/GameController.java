package learn.poker.controllers;

import learn.poker.domain.GameService;
import learn.poker.domain.Result;
import learn.poker.domain.RoomService;
import learn.poker.models.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.util.HtmlUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
@CrossOrigin(origins = {"http://localhost:3000"})
public class GameController {

    @Autowired
    private SimpMessagingTemplate template;
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
            Room updatedRoom = roomResult.getPayload();
            return updatedRoom;
        } else {
            throw new RuntimeException("Unable to add player the game.");
        }
    }

//    private void notifyPlayersOfPlayerJoined(int roomId, Room room) {
//        // Create a message indicating that a new player has joined
////        Message message = new Message();
////        message.setType(MessageType.PLAYER_JOINED);
////        message.setContent("A new player has joined the game.");
//
//        // Broadcast the message to all players in the room
//        template.convertAndSend("/topic/game/" + roomId, room);
//    }

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
            throw new RuntimeException("Check failed.");
        }
    }

    @MessageMapping("/raise/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public Room raise(@DestinationVariable int roomId, Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.RAISE);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            throw new RuntimeException("Raise failed.");
        }
    }

    @MessageMapping("/fold/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public Room fold(@DestinationVariable int roomId, Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.FOLD);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            throw new RuntimeException("Fold failed.");
        }
    }

    @MessageMapping("/call/{roomId}")
    @SendTo("/topic/game/{roomId}")
    public Room call(@DestinationVariable int roomId, Room room) {
        Result<Room> roomResult = gameService.handleAction(room, Action.CALL);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            throw new RuntimeException("Call failed.");
        }
    }


    @MessageMapping("/leave-game/{roomId}/{username}")
    @SendTo("/topic/game/{roomId}")
    public Room leaveGame(@DestinationVariable int roomId, Room room, @DestinationVariable String username) {
        Result<Room> roomResult = gameService.leaveGame(room, username);
        if (roomResult.isSuccess()) {
            return roomResult.getPayload();
        } else {
            throw new RuntimeException("Something went wrong, unable to leave game.");
        }
    }



}

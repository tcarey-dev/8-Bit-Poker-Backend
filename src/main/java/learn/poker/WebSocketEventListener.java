package learn.poker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import learn.poker.domain.GameService;
import learn.poker.domain.PlayerService;
import learn.poker.domain.Result;
import learn.poker.domain.RoomService;
import learn.poker.models.Game;
import learn.poker.models.Player;
import learn.poker.models.Room;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messagingTemplate;
    private final RoomService roomService;
    private final GameService gameService;
    private final PlayerService playerService;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate, RoomService roomService, GameService gameService, PlayerService playerService) {
        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
        this.gameService = gameService;
        this.playerService = playerService;
    }

    @EventListener
    public void handleWebSocketSubscribeListener(
            SessionSubscribeEvent event
    ) throws JsonProcessingException {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String userJson = headerAccessor.getFirstNativeHeader("username");

        if (userJson != null
                && destination != null
                && destination.matches("/topic/game/[a-zA-Z0-9]+")
        ) {
            String username = new ObjectMapper().readValue(userJson, String.class);
            int roomId = extractRoomIdFromDestination(destination);
            Room room = roomService.findById(roomId);

            // initialize game if necessary
            Result<Room> result = new Result<>();
            if (room.getGame() == null) {
                result = gameService.init(room);
                if (!result.isSuccess()) {
                    // TODO handle error scenario here
                } else {
                    room = result.getPayload();
                }
            }

            // add player to game
            Game game = room.getGame();
            Player player = playerService.loadUserByUsername(username);

            List<Player> playerList = new ArrayList<>();
            if(game.getPlayers() != null){
                playerList = game.getPlayers();
            }

            if (!playerList.contains(player)) {
                playerList.add(player);
                game.setPlayers(playerList);
                room.setGame(game);

                result = roomService.update(room);

                if (result.isSuccess()) {
                    Room updatedRoom = result.getPayload();
                    messagingTemplate.convertAndSend(destination, updatedRoom);
                }
            }
        }
    }

    private int extractRoomIdFromDestination(String destination) {
        String[] parts = destination.split("/");
        return Integer.parseInt(parts[3]);
    }
}

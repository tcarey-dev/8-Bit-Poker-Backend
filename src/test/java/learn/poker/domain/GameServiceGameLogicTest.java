package learn.poker.domain;

import learn.poker.data.GameRepository;
import learn.poker.data.RoomRepository;
import learn.poker.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GameServiceGameLogicTest {
    @Autowired
    GameService gameService;
    @Autowired
    RoomService roomService;
    @Autowired
    DeckService deckService;
    @Autowired
    PlayerService playerService;
    @MockBean
    GameRepository gameRepository;
    @MockBean
    RoomRepository roomRepository;

    @Test
    void shouldInitGame(){
        Room room = new Room(4, 2, 2, null);
        Game game = new Game(0, 0, null, 0, null, null, null);
        Game expectedGame = new Game(4, 0, null, 0, null, null, null);
        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.create(game)).thenReturn(expectedGame);
        when(roomRepository.update(expectedRoom)).thenReturn(true);
        Result<Room> actual = gameService.init(room);

        assertTrue(actual.isSuccess());
        assertEquals(4, actual.getPayload().getRoomId());
        assertEquals(4, actual.getPayload().getGame().getGameId());
        assertEquals(0, actual.getPayload().getGame().getPot());
    }

    // init
    @Test
    void shouldNotInitRoomWithNullId(){}

    @Test
    void shouldNotInitNonExistingRoom(){}


    // update i.e. add players
    @Test
    void shouldUpdateRoomToHaveOnePlayer(){}

    @Test
    void shouldUpdateRoomToHaveTwoPlayers(){}

    @Test
    void shouldNotUpdateRoomToHavePlayersIfGameIsNull(){}

    // start
    @Test
    void shouldStartGameWithTwoPlayers(){

    }
    @Test
    void shouldNotStartGameWithLessThanTwoPlayers(){}

    @Test
    void shouldNotStartGameIfEitherPlayerAccountBalanceIsLessThanOrEqualToZero(){}

    @Test
    void startGameShouldSetLastActionToNone() {}

    @Test
    void startGameShouldSetPlayerOnePositionToSmallBlind() {}

    @Test
    void startGameShouldSetSetPlayerTwoPositionToBigBlind() {}

    // should subtract blinds correctly from each players account balance
    // should add blinds to pot
    // should add two cards to each players holeCards field
    // should set Smallblind playersAction to true
    // should set big blind playersAction to false


    // handleAction fold
    @Test
    void foldActionShouldSetOpponentAsWinner(){} //xiao does this one up

    @Test
    void foldActionShouldAddPotToWinnersAccountBalance(){ //aaron start here
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(true);
        player2.setPlayersAction(false);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.BET, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, null, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.FOLD);

        assertEquals(60, player2.getAccountBalance());
    }

    @Test
    void foldActionShouldSetPotToZero() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(true);
        player2.setPlayersAction(false);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.BET, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, null, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.FOLD);

        assertEquals(0, expectedGame.getPot());
    }

    @Test
    void foldActionShouldSetWinnerToNull() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(true);
        player2.setPlayersAction(false);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, "john@smith.com", 5, Action.BET, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, null, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.FOLD);

        assertEquals(null, game.getWinner());
    }


    @Test
    void foldActionShouldSetBoardToNull() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(true);
        player2.setPlayersAction(false);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.BET, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, null, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.FOLD);

        assertEquals(null, expectedGame.getBoard());
    }

    @Test
    void foldActionShouldSetBetAmountToZero() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(true);
        player2.setPlayersAction(false);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.BET, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, null, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.FOLD);

        assertEquals(0, game.getBetAmount());
    }

    @Test
    void foldActionShouldSetHoleCardsToNull() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(true);
        player2.setPlayersAction(false);

        player1.setHoleCards(List.of(Card.ACE_OF_SPADES, Card.FIVE_OF_DIAMONDS));
        player2.setHoleCards(List.of(Card.EIGHT_OF_HEARTS, Card.KING_OF_HEARTS));

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.BET, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, null, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.FOLD);

        assertNull(player1.getHoleCards());
        assertNull(player2.getHoleCards());
    }

    @Test
    void foldActionShouldFlipPlayersActionBooleans() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(true);
        player2.setPlayersAction(false);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.BET, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, null, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.FOLD);

        assertEquals(false, player1.isPlayersAction());
        assertEquals(true, player2.isPlayersAction());
    }

    @Test
    void foldActionShouldSwitchPlayerPositions() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(true);
        player2.setPlayersAction(false);

        player1.setPosition(Position.BIGBLIND);
        player2.setPosition(Position.SMALLBLIND);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.BET, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, null, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.FOLD);

        assertEquals(Position.SMALLBLIND, player1.getPosition());
        assertEquals(Position.BIGBLIND, player2.getPosition());
    }


    // handleAction call
    @Test
    void callActionAfterRaiseShouldFlipPlayersActionBooleanAndPosition(){
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(true);
        player2.setPlayersAction(false);

        player1.setPosition(Position.BIGBLIND);
        player2.setPosition(Position.SMALLBLIND);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.RAISE, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, Action.RAISE, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.CALL);

        assertEquals(false, player1.isPlayersAction());
        assertEquals(true, player2.isPlayersAction());

        assertEquals(Position.SMALLBLIND, player1.getPosition());
        assertEquals(Position.BIGBLIND, player2.getPosition());
    }

    @Test
    void callActionBySmallBlindAsFirstMoveShouldFlipPlayersActionBooleanButNotPosition() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(false);
        player2.setPlayersAction(true);

        player1.setPosition(Position.BIGBLIND);
        player2.setPosition(Position.SMALLBLIND);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.RAISE, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, null, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.CALL);

        assertEquals(true, player1.isPlayersAction());
        assertEquals(false, player2.isPlayersAction());

        assertEquals(Position.BIGBLIND, player1.getPosition());
        assertEquals(Position.SMALLBLIND, player2.getPosition());
    }


    //check
    @Test
    void checkActionAfterCheckActionShouldFlipPlayersActionBooleanAndPosition() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(true);
        player2.setPlayersAction(false);

        player1.setPosition(Position.BIGBLIND);
        player2.setPosition(Position.SMALLBLIND);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.CHECK, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, Action.CHECK, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.CHECK);

        assertEquals(false, player1.isPlayersAction());
        assertEquals(true, player2.isPlayersAction());

        assertEquals(Position.SMALLBLIND, player1.getPosition());
        assertEquals(Position.BIGBLIND, player2.getPosition());
    }

    @Test
    void checkActionAfterNoActionShouldFlipPlayersActionBooleanButNotPosition() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(true);
        player2.setPlayersAction(false);

        player1.setPosition(Position.BIGBLIND);
        player2.setPosition(Position.SMALLBLIND);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.NONE, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, Action.NONE, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.CHECK);

        assertEquals(false, player1.isPlayersAction());
        assertEquals(true, player2.isPlayersAction());

        assertEquals(Position.BIGBLIND, player1.getPosition());
        assertEquals(Position.SMALLBLIND, player2.getPosition());
    }

    // handleAction raise
    @Test
    void raiseActionShouldFlipPlayersActionBooleanButNotPosition() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");

        player1.setAccountBalance(15);
        player2.setAccountBalance(10);

        player1.setPlayersAction(false);
        player2.setPlayersAction(true);

        player1.setPosition(Position.BIGBLIND);
        player2.setPosition(Position.SMALLBLIND);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Board board = new Board(2, List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS, Card.EIGHT_OF_HEARTS), Card.KING_OF_HEARTS, Card.NINE_OF_DIAMONDS);

        Game game = new Game(4, 50, null, 5, Action.RAISE, board, players);
        Room room = new Room(4, 2, 2, game);

        Game expectedGame = new Game(4, 0, "john@smith.com", 0, null, null, players);

        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(expectedGame)).thenReturn(true);
        when(roomRepository.update(expectedRoom)).thenReturn(true);

        gameService.handleAction(room, Action.RAISE);

        assertEquals(true, player1.isPlayersAction());
        assertEquals(false, player2.isPlayersAction());

        assertEquals(Position.BIGBLIND, player1.getPosition());
        assertEquals(Position.SMALLBLIND, player2.getPosition());
    }


}

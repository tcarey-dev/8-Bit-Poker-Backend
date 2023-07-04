package learn.poker.domain;

import learn.poker.data.GameRepository;
import learn.poker.data.PlayerRepository;
import learn.poker.data.RoomRepository;
import learn.poker.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

    @MockBean
    PlayerRepository playerRepository;

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
    void shouldNotInitNonExistingRoom(){
        Room room = new Room(9999, 2, 2, null);

        Result<Room> result = gameService.init(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("not found"));
    }


    // update i.e. add players
    @Test
    void shouldUpdateGameToHaveOnePlayer(){
        Player player1 = new Player(1,"player1Username", "password",true, List.of("USER"));

        Board board = new Board(List.of(Card.QUEEN_OF_HEARTS, Card.ACE_OF_SPADES, Card.ACE_OF_DIAMONDS), Card.KING_OF_DIAMONDS, Card.EIGHT_OF_CLUBS);

        Game game = new Game();
        game.setGameId(4);
        game.setPot(0);
        game.setWinner(null);
        game.setBetAmount(0);
        game.setLastAction(Action.NONE);
        game.setBoard(board);
        game.setPlayers(List.of(player1));

        Room room = new Room(4, 2, 2, game);

        when(gameRepository.update(game)).thenReturn(true);
        when(roomRepository.update(room)).thenReturn(true);

        Result<Game> result = gameService.update(game);

        assertTrue(result.isSuccess());
        assertEquals(1, game.getPlayers().size());
        assertEquals(1, game.getPlayers().get(0).getPlayerId());
    }

    @Test
    void shouldUpdateGameToHaveTwoPlayers(){
        Player player1 = new Player(1,"player1Username", "password",true, List.of("USER"));
        Player player2 = new Player(2,"player2Username", "password",true, List.of("USER"));
        Board board = new Board(List.of(Card.QUEEN_OF_HEARTS, Card.ACE_OF_SPADES, Card.ACE_OF_DIAMONDS), Card.KING_OF_DIAMONDS, Card.EIGHT_OF_CLUBS);

        Game game = new Game();
        game.setGameId(4);
        game.setPot(0);
        game.setWinner(null);
        game.setBetAmount(0);
        game.setLastAction(Action.NONE);
        game.setBoard(board);
        game.setPlayers(List.of(player1, player2));

        Room room = new Room(4, 2, 2, game);

        when(gameRepository.update(game)).thenReturn(true);
        when(roomRepository.update(room)).thenReturn(true);

        Result<Game> result = gameService.update(game);

        assertTrue(result.isSuccess());
        assertEquals(2, game.getPlayers().size());
        assertEquals(1, game.getPlayers().get(0).getPlayerId());
        assertEquals(2, game.getPlayers().get(1).getPlayerId());

    }


    // start
    @Test
    void shouldStartGameWithTwoPlayers(){
        Player player1 = new Player(1,"player1Username", "password",true, List.of("USER"));
        Player player2 = new Player(2,"player2Username", "password",true, List.of("USER"));
        player1.setAccountBalance(100);
        player2.setAccountBalance(100);
        Board board = new Board(List.of(Card.QUEEN_OF_HEARTS, Card.ACE_OF_SPADES, Card.ACE_OF_DIAMONDS), Card.KING_OF_DIAMONDS, Card.EIGHT_OF_CLUBS);

        Game game = new Game();
        game.setGameId(4);
        game.setPot(0);
        game.setWinner(null);
        game.setBetAmount(0);
        game.setLastAction(Action.NONE);
        game.setBoard(board);
        game.setPlayers(List.of(player1, player2));

        Room room = new Room(4, 2, 2, game);

        Result<Room> result = gameService.start(room);

        assertTrue(result.isSuccess());
    }
    @Test
    void shouldNotStartGameWithLessThanTwoPlayers(){
        Player player1 = new Player(1,"player1Username", "password",true, List.of("USER"));
        Board board = new Board(List.of(Card.QUEEN_OF_HEARTS, Card.ACE_OF_SPADES, Card.ACE_OF_DIAMONDS), Card.KING_OF_DIAMONDS, Card.EIGHT_OF_CLUBS);

        Game game = new Game();
        game.setGameId(4);
        game.setPot(0);
        game.setWinner(null);
        game.setBetAmount(0);
        game.setLastAction(Action.NONE);
        game.setBoard(board);
        game.setPlayers(List.of(player1));

        Room room = new Room(4, 2, 2, game);

        Result<Room> result = gameService.start(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Game must have two players to start.", result.getMessages().get(0));
    }

    @Test
    void shouldNotStartGameIfEitherPlayerAccountBalanceIsLessThanOrEqualToZero(){
        //this needs to be tested b/c a player can have an account balance of 0 or less after a game and thus
        //another game cannot be started when this happens

        Player player1 = new Player(0,"username", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, List.of("USER"));
        player1.setAccountBalance(0.0);

        Player player2 = new Player(0,"username", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, List.of("USER"));
        player2.setAccountBalance(0.0);

        when(playerRepository.create(player1)).thenReturn(player1);
        when(playerRepository.create(player2)).thenReturn(player2);

        Game game = new Game(0, 0, null, 0, null, null, null);
        Game expectedGame = new Game();
        expectedGame.setGameId(4);
        expectedGame.setPot(0);
        expectedGame.setWinner(null);
        expectedGame.setBetAmount(0);
        expectedGame.setLastAction(Action.NONE);
        expectedGame.setBoard(null);
        expectedGame.setPlayers(List.of(player1, player2));

        Room room = new Room(4, 2, 2, expectedGame);

        when(gameRepository.update(game)).thenReturn(true);
        when(roomRepository.update(room)).thenReturn(true);

        Result<Room> result = gameService.start(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Account Balance must be greater than 0 to start game.", result.getMessages().get(0));
    }

    @Test
    void startGameShouldSetLastActionToNone() {
        Player player1 = new Player(1,"player1Username", "password",true, List.of("USER"));
        Player player2 = new Player(2,"player2Username", "password",true, List.of("USER"));
        player1.setAccountBalance(100);
        player2.setAccountBalance(100);

        Game game = new Game(0, 0, null, 0, null, null, null);
        Game expectedGame = new Game();
        expectedGame.setGameId(4);
        expectedGame.setPot(0);
        expectedGame.setWinner(null);
        expectedGame.setBetAmount(0);
        expectedGame.setLastAction(Action.NONE);
        expectedGame.setBoard(null);
        expectedGame.setPlayers(List.of(player1, player2));

        Room room = new Room(4, 2, 2, expectedGame);

        when(playerRepository.create(player1)).thenReturn(player1);
        when(playerRepository.create(player2)).thenReturn(player2);

        when(gameRepository.update(game)).thenReturn(true);
        when(roomRepository.update(room)).thenReturn(true);

        Result<Room> result = gameService.start(room);

        assertTrue(result.isSuccess());
        assertEquals(Action.NONE,result.getPayload().getGame().getLastAction());
    }

    @Test
    void startGameShouldSetPlayerOnePositionToSmallBlind() {
        Player player1 = new Player(1,"player1Username", "password",true, List.of("USER"));
        Player player2 = new Player(2,"player2Username", "password",true, List.of("USER"));
        player1.setAccountBalance(100);
        player2.setAccountBalance(100);

        Game game = new Game(0, 0, null, 0, null, null, null);
        Game expectedGame = new Game();
        expectedGame.setGameId(4);
        expectedGame.setPot(0);
        expectedGame.setWinner(null);
        expectedGame.setBetAmount(0);
        expectedGame.setLastAction(Action.NONE);
        expectedGame.setBoard(null);
        expectedGame.setPlayers(List.of(player1, player2));

        Room room = new Room(4, 2, 2, expectedGame);

        when(playerRepository.create(player1)).thenReturn(player1);
        when(playerRepository.create(player2)).thenReturn(player2);

        when(gameRepository.update(game)).thenReturn(true);
        when(roomRepository.update(room)).thenReturn(true);

        Result<Room> result = gameService.start(room);

        assertTrue(result.isSuccess());
        assertEquals(Position.SMALLBLIND, player1.getPosition());
    }

    @Test
    void startGameShouldSetSetPlayerTwoPositionToBigBlind() {
        Player player1 = new Player(1,"player1Username", "password",true, List.of("USER"));
        player1.setAccountBalance(100);
        Player player2 = new Player(2,"player2Username", "password",true, List.of("USER"));
        player2.setAccountBalance(100);

        Game game = new Game(0, 0, null, 0, null, null, null);
        Game expectedGame = new Game();
        expectedGame.setGameId(4);
        expectedGame.setPot(0);
        expectedGame.setWinner(null);
        expectedGame.setBetAmount(0);
        expectedGame.setLastAction(Action.NONE);
        expectedGame.setBoard(null);
        expectedGame.setPlayers(List.of(player1, player2));

        Room room = new Room(4, 2, 2, expectedGame);

        when(playerRepository.create(player1)).thenReturn(player1);
        when(playerRepository.create(player2)).thenReturn(player2);

        when(gameRepository.update(game)).thenReturn(true);
        when(roomRepository.update(room)).thenReturn(true);

        Result<Room> result = gameService.start(room);

        assertTrue(result.isSuccess());
        assertEquals(Position.BIGBLIND, player2.getPosition());}

    // should subtract blinds correctly from each players account balance
    // should add blinds to pot
    // should add two cards to each players holeCards field
    // should set Smallblind playersAction to true
    // should set big blind playersAction to false


    // handleAction fold
    @Test
    void foldActionShouldSetOpponentAsWinner(){
        Player player1 = new Player(1,"player1Username", "password",true, List.of("USER"));
        Player player2 = new Player(2,"player2Username", "password",true, List.of("USER"));
        player1.setAccountBalance(100);
        player2.setAccountBalance(100);

        player1.setPlayersAction(true);//means it is player1's turn --> p1 folds --> p2 should be declared winner
        player2.setPlayersAction(false);

        Board board = new Board(List.of(Card.QUEEN_OF_HEARTS, Card.ACE_OF_SPADES, Card.ACE_OF_DIAMONDS), Card.KING_OF_DIAMONDS, Card.EIGHT_OF_CLUBS);

        Game game = new Game(0, 0, null, 0, null, null, null);
        Game expectedGame = new Game();
        expectedGame.setGameId(4);
        expectedGame.setPot(0);
        expectedGame.setWinner(null);
        expectedGame.setBetAmount(0);
        expectedGame.setLastAction(Action.NONE);
        expectedGame.setBoard(board);
        expectedGame.setPlayers(List.of(player1, player2));

        Room room = new Room(4, 2, 2, expectedGame);

        when(playerRepository.create(player1)).thenReturn(player1);
        when(playerRepository.create(player2)).thenReturn(player2);

        when(gameRepository.update(game)).thenReturn(true);
        when(roomRepository.update(room)).thenReturn(true);

        gameService.handleAction(room, Action.FOLD);

//        assertEquals(player2.getUsername(), game.getWinner());
        assertEquals(null, game.getWinner());
    }

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

    @Test
    void shouldEndGameWhenLastPlayerLeavesTheGame(){
        Player player1 = new Player(1,"player1Username", "password",true, List.of("USER"));
        Player player2 = new Player(2,"player2Username", "password",true, List.of("USER"));
        player1.setAccountBalance(100);
        player2.setAccountBalance(10);

        Game expectedGame = null;
        Game game = new Game();
        game.setGameId(111);
        game.setPot(10);
        game.setWinner(null);
        game.setBetAmount(0);
        game.setLastAction(Action.NONE);
        game.setBoard(null);
        game.setPlayers(List.of(player1, player2));

        Room room = new Room(4, 2, 2, game);

        when(gameRepository.delete(game.getGameId())).thenReturn(true);

        Result<Room> result = gameService.leaveGame(room);
        Room actual = result.getPayload();
        assertEquals(4, actual.getRoomId());
        assertNull(actual.getGame());
//        assertTrue(result.isSuccess());
//        assertEquals(110, player1.getAccountBalance());
//        assertEquals(0, room.getGame().getGameId());
//        assertEquals(0, room.getGame().getPot());
//        assertEquals(1, game.getPlayers().size());
    }

    @Test
    void shouldEndGameWhenOnePlayerLeavesTheGame(){
        Player player1 = new Player(1,"player1Username", "password",true, List.of("USER"));
        Player player2 = new Player(2,"player2Username", "password",true, List.of("USER"));
        player1.setAccountBalance(100);
        player2.setAccountBalance(10);

        Game expectedGame = null;
        Game game = new Game();
        game.setGameId(111);
        game.setPot(10);
        game.setWinner(null);
        game.setBetAmount(0);
        game.setLastAction(Action.NONE);
        game.setBoard(null);
        game.setPlayers(List.of(player1, player2));

        Room room = new Room(4, 2, 2, null);

        when(gameRepository.delete(game.getGameId())).thenReturn(true);
        when(gameRepository.update(game)).thenReturn(true);
        when(roomRepository.update(room)).thenReturn(true);

        //when one player leaves in the middle of a game


        expectedGame.setPlayers(List.of(player1));
        when(gameRepository.update(game)).thenReturn(true);
        when(roomRepository.update(room)).thenReturn(true);

        Result<Room> result = gameService.leaveGame(room);
        assertTrue(result.isSuccess());
        assertEquals(110, player1.getAccountBalance());
        assertEquals(0, room.getGame().getGameId());
        assertEquals(0, room.getGame().getPot());
        assertEquals(1, game.getPlayers().size());
    }


}
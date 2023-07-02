package learn.poker.domain;

import learn.poker.data.GameRepository;
import learn.poker.data.RoomRepository;
import learn.poker.models.Game;
import learn.poker.models.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GameServiceGameLogicTest {
    @Autowired
    GameService service;
    @Autowired
    RoomService roomService;
    @Autowired
    DeckService deckService;
    @Autowired
    PlayerService playerService;
    @MockBean
    GameRepository repository;
    @MockBean
    RoomRepository roomRepository;

    @Test
    void shouldInitGame(){
        Room room = new Room(4, 2, 2, null);
        Game game = new Game(0, 0, null, 0, null, null, null);
        Game expectedGame = new Game(4, 0, null, 0, null, null, null);
        Room expectedRoom = new Room(4, 2, 2, expectedGame);

        when(repository.create(game)).thenReturn(expectedGame);
        when(roomRepository.update(expectedRoom)).thenReturn(true);
        Result<Room> actual = service.init(room);

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
    void foldActionShouldSetOpponentAsWinner(){}

    @Test
    void foldActionShouldAddPotToWinnersAccountBalance(){}

    @Test
    void foldActionShouldSetPotToZero() {}

    @Test
    void foldActionShouldSetWinnerToNull() {}

    @Test
    void foldActionShouldSetBoardToNull() {}

    @Test
    void foldActionShouldSetBetAmountToZero() {}

    @Test
    void foldActionShouldSetHoleCardsToNull() {}

    @Test
    void foldActionShouldFlipPlayersActionBooleans() {}

    @Test
    void foldActionShouldSwitchPlayerPositions() {}


    // handleAction call
    @Test
    void callActionAfterRaiseShouldFlipPlayersActionBooleanAndPosition(){}

    @Test
    void callActionBySmallBlindAsFirstMoveShouldFlipPlayersActionBooleanButNotPosition() {}


    //check
    @Test
    void checkActionAfterCheckActionShouldFlipPlayersActionBooleanAndPosition() {}

    @Test
    void checkActionAfterNoActionShouldFlipPlayersActionBooleanButNotPosition() {}

    // handleAction raise
    @Test
    void raiseActionShouldFlipPlayersActionBooleanButNotPosition() {}


}

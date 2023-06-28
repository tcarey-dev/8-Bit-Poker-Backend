package learn.poker.data;

import learn.poker.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class GameJdbcTemplateRepositoryTest {

    @Autowired
    GameJdbcTemplateRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindById() {
        Game actual = repository.findById(1);
        assertNotNull(actual);
        assertEquals(200, actual.getPot());
        assertEquals("sally@jones.com", actual.getWinner());
    }

    @Test
    void shouldNotFindUnknownId() {
        Game actual = repository.findById(999);
        assertNull(actual);
    }

    @Test
    void shouldCreate() {
        Board board = new Board(
                1,
                List.of(Card.ACE_OF_CLUBS,
                        Card.ACE_OF_DIAMONDS,
                        Card.ACE_OF_SPADES),
                Card.EIGHT_OF_CLUBS,
                Card.ACE_OF_HEARTS);

        Player player = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player.setPlayerId(0);
        player.setAuthorities(List.of());
        player.setEnabled(true);
        player.setPlayersAction(true);
        player.setPosition(Position.SMALL_BLIND);
        player.setAccountBalance(50);
        player.setDisplayName("theDude");
        player.setHoleCards(List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS));

        Game game = new Game(
            1, 350,"lisa@simpson.com", board, List.of(player));

        Game actual = repository.create(game);
        assertEquals(1, actual.getGameId());
        assertEquals(350, actual.getPot());
        assertEquals("lisa@simpson.com", actual.getWinner());
        assertEquals(board, actual.getBoard());
        assertEquals(List.of(player), actual.getPlayers());
    }

    @Test
    void shouldUpdate() {
        Game game = repository.findById(1);
        game.setPot(350);
        game.setWinner("lisa@simpson.com");

        boolean result = repository.update(game);
        assertTrue(result);

        assertNotNull(game);

        assertEquals(1, game.getGameId());
        assertEquals(350, game.getPot());
        assertEquals("lisa@simpson.com", game.getWinner());

    }

    @Test
    void shouldNotUpdateUnknownId() {
        Board board = new Board(
                1,
                List.of(Card.ACE_OF_CLUBS,
                        Card.ACE_OF_DIAMONDS,
                        Card.ACE_OF_SPADES),
                Card.EIGHT_OF_CLUBS,
                Card.ACE_OF_HEARTS);

        Player player = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player.setPlayerId(0);
        player.setAuthorities(List.of());
        player.setEnabled(true);
        player.setPlayersAction(true);
        player.setPosition(Position.SMALL_BLIND);
        player.setAccountBalance(50);
        player.setDisplayName("theDude");
        player.setHoleCards(List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS));

        Game game = new Game(99999, 100, "", board, List.of(player));

        boolean result = repository.update(game);
        assertFalse(result);



    }

    @Test
    void shouldDelete() {
        assertTrue(repository.delete(1));
    }

    @Test
    void shouldNotDeleteUnknownId() {
        assertFalse(repository.delete(999));
    }

}
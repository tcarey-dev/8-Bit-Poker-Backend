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
                List.of(Card.ACE_OF_CLUBS,
                        Card.ACE_OF_DIAMONDS,
                        Card.ACE_OF_SPADES),
                Card.EIGHT_OF_CLUBS,
                Card.ACE_OF_HEARTS);

        Player player1 = new Player("keanu@reeves.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player1.setAuthorities(List.of());
        player1.setEnabled(true);
        player1.setPlayersAction(true);
        player1.setPosition(Position.SMALLBLIND);
        player1.setAccountBalance(50);
        player1.setDisplayName("theDude");
        player1.setHoleCards(List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS));

        Player player2 = new Player("matt@damon.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player2.setAuthorities(List.of());
        player2.setEnabled(true);
        player2.setPlayersAction(false);
        player2.setPosition(Position.BIGBLIND);
        player2.setAccountBalance(75);
        player2.setDisplayName("choppyChad");
        player2.setHoleCards(List.of(Card.KING_OF_DIAMONDS, Card.JACK_OF_HEARTS));

        Game game = new Game(350,"matt@damon.com", board, List.of(player1, player2));

        Game actual = repository.create(game);
        assertEquals(4, actual.getGameId());
        assertEquals(350, actual.getPot());
        assertEquals("matt@damon.com", actual.getWinner());
        assertEquals(board, actual.getBoard());
        assertEquals(List.of(player1, player2), actual.getPlayers());
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
                List.of(Card.ACE_OF_CLUBS,
                        Card.ACE_OF_DIAMONDS,
                        Card.ACE_OF_SPADES),
                Card.EIGHT_OF_CLUBS,
                Card.ACE_OF_HEARTS);

        Player player1 = new Player("keanu@reeves.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player1.setAuthorities(List.of());
        player1.setEnabled(true);
        player1.setPlayersAction(true);
        player1.setPosition(Position.SMALLBLIND);
        player1.setAccountBalance(50);
        player1.setDisplayName("theDude");
        player1.setHoleCards(List.of(Card.ACE_OF_CLUBS, Card.FIVE_OF_DIAMONDS));

        Player player2 = new Player("matt@damon.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player2.setAuthorities(List.of());
        player2.setEnabled(true);
        player2.setPlayersAction(false);
        player2.setPosition(Position.BIGBLIND);
        player2.setAccountBalance(75);
        player2.setDisplayName("choppyChad");
        player2.setHoleCards(List.of(Card.KING_OF_DIAMONDS, Card.JACK_OF_HEARTS));

        Game game = new Game(99999, 100, "", board, List.of(player1, player2));

        boolean result = repository.update(game);
        assertFalse(result);



    }

    @Test
    void shouldDelete() {
        assertTrue(repository.delete(2));
    }

//    @Test
//    void shouldNotDeleteUnknownId() {
//        assertFalse(repository.delete(999));
//    }

}
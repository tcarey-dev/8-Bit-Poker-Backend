package learn.poker.data;

import learn.poker.models.Board;
import learn.poker.models.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

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
package learn.poker.domain;


import learn.poker.data.GameRepository;
import learn.poker.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GameServiceTest {

    @Autowired
    GameService service;

    @MockBean
    GameRepository repository;

    @Test
    void shouldFindById() {
        when(repository.findById(3)).thenReturn(new Game());
        Game game = service.findById(3);
        assertNotNull(game);
    }

    @Test
    void shouldNotFindUnknownId() {
        Game game = service.findById(999);
        assertNull(game);
    }

    @Test
    void shouldGetWinner() {

    }

    @Test
    void shouldAddGame() {

        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player1.setPlayerId(0);


        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player2.setPlayerId(1);


        Game game = new Game();
        game.setPot(350);
        game.setPlayers(List.of(player1, player2));

        Game expectedGame = new Game();
        game.setPot(350);
        game.setPlayers(List.of(player1, player2));

        when(repository.create(game)).thenReturn(expectedGame);
        Result<Game> actual = service.add(game);
        assertTrue(actual.isSuccess());
        assertEquals(expectedGame, actual.getPayload());

    }

    @Test
    void shouldNotAddGameWithInvalidPot() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player1.setPlayerId(0);


        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player2.setPlayerId(1);

        Game game = new Game();
        game.setPot(-10);
        game.setPlayers(List.of(player1, player2));

        Result<Game> result = service.add(game);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("pot must be greater than or equal to zero", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddGameWithLessThanTwoPlayers() {

    }

    @Test
    void shouldUpdateGame() {

    }

    @Test
    void shouldNotUpdateGameWithInvalidPot() {

    }

    @Test
    void shouldNotUpdateGameWithLessThanTwoPlayers() {

    }

    @Test
    void shouldDeleteById() {

    }

    @Test
    void shouldNotDeleteByUnknownId() {

    }

}

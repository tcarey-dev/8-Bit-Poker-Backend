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
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals("pot must be greater than or equal to zero", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddGameWithLessThanTwoPlayers() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player1.setPlayerId(0);

        Game game = new Game();
        game.setPot(250);
        game.setPlayers(List.of(player1));

        Result<Game> result = service.add(game);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals("two players are required to start a game", result.getMessages().get(0));
    }

    @Test
    void shouldUpdateGame() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player1.setPlayerId(0);


        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player2.setPlayerId(1);

        Board board = new Board(
                1,
                List.of(Card.ACE_OF_CLUBS,
                        Card.ACE_OF_DIAMONDS,
                        Card.ACE_OF_SPADES),
                Card.EIGHT_OF_CLUBS,
                Card.ACE_OF_HEARTS);

        Game toUpdate = new Game(1, 350,"lisa@simpson.com", board, List.of(player1, player2));

        toUpdate.setPot(200);

        when(repository.update(toUpdate)).thenReturn(true);

        Result<Game> actual = service.update(toUpdate);
        assertTrue(actual.isSuccess());
        assertEquals(0, actual.getMessages().size());
    }

    @Test
    void shouldNotUpdateGameWithInvalidPot() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player1.setPlayerId(0);


        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player2.setPlayerId(1);


        Game game = new Game();
        game.setPot(350);
        game.setPlayers(List.of(player1, player2));

        game.setPot(-99);

        Result<Game> actual = service.update(game);

        assertFalse(actual.isSuccess());
        assertEquals(1, actual.getMessages().size());
        assertEquals(ResultType.INVALID, actual.getType());
        assertEquals("pot must be greater than or equal to zero", actual.getMessages().get(0));
    }

    @Test
    void shouldNotUpdateGameWithLessThanTwoPlayers() {
        Player player1 = new Player("fun@testing.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player1.setPlayerId(0);


        Player player2 = new Player("john@smith.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        player2.setPlayerId(1);


        Board board = new Board(
                1,
                List.of(Card.ACE_OF_CLUBS,
                        Card.ACE_OF_DIAMONDS,
                        Card.ACE_OF_SPADES),
                Card.EIGHT_OF_CLUBS,
                Card.ACE_OF_HEARTS);

        Game game = new Game(1, 350,"lisa@simpson.com", board, List.of(player1, player2));

        game.setPlayers(List.of(player1));

        Result<Game> result = service.update(game);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals("two players are required to start a game", result.getMessages().get(0));
    }

    @Test
    void shouldDeleteById() {
        when(repository.delete(1)).thenReturn(true);
        assertTrue(service.deleteById(1));
    }

    @Test
    void shouldNotDeleteByUnknownId() {
        assertFalse(service.deleteById(99999));
    }

}

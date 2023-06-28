package learn.poker.domain;


import learn.poker.data.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GameServiceTest {

    @Autowired
    GameService service;

    @MockBean
    GameRepository repository;

    @Test
    void shouldFindById() {

    }

    @Test
    void shouldNotFindUnknownId() {

    }

    @Test
    void shouldGetWinner() {

    }

    @Test
    void shouldAddGame() {

    }

    @Test
    void shouldNotAddGameWithInvalidPot() {

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

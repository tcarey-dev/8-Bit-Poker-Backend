package learn.poker.domain;

import learn.poker.data.PlayerRepository;
import learn.poker.models.Card;
import learn.poker.models.Player;
import learn.poker.models.Position;
import learn.poker.models.Room;
import learn.poker.security.Credential;
import org.apache.tomcat.jni.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class PlayerServiceTest {

    @MockBean
    PlayerRepository repository;

    @Autowired
    PlayerService service;

    //happy path -- create
    @Test
    void shouldCreate(){
        Credential credential = new Credential();
        credential.setUsername("username");
        credential.setPassword("P@ssw0rd!");

        Player player = new Player(0,"username", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true);

        when(repository.create(player)).thenReturn(player);
        Result<Player> result = service.create(credential);

        assertTrue(result.isSuccess());
        assertEquals(player, result.getPayload());
    }

    //unhappy path -- create
    @Test
    void shouldNotCreateWithNullUserName(){
        Credential credential = new Credential();
        credential.setUsername(null);
        credential.setPassword("P@ssw0rd!");

        Result<Player> result = service.create(credential);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("username is required", result.getMessages().get(0));
    }

    @Test
    void shouldNotCreateWithBlankUserName(){
        Credential credential = new Credential();
        credential.setUsername("");
        credential.setPassword("P@ssw0rd!");

        Result<Player> result = service.create(credential);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("username is required", result.getMessages().get(0));
    }

    @Test
    void shouldNotCreateWithUserNameMoreThan50Characters(){
        Credential credential = new Credential();
        credential.setUsername("isUserNameisUserNameisUserNameisUserNameisUserNameisUserName");
        credential.setPassword("P@ssw0rd!");

        Result<Player> result = service.create(credential);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("username must be less than 50 characters", result.getMessages().get(0));
    }

    @Test
    void shouldNotCreateWithNullPassword(){
        Credential credential = new Credential();
        credential.setUsername("username");
        credential.setPassword(null);

        Result<Player> result = service.create(credential);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("password is required", result.getMessages().get(0));
    }

    @Test
    void shouldNotCreateWithPasswordLessThan8Characters(){
        Credential credential = new Credential();
        credential.setUsername("username");
        credential.setPassword("");

        Result<Player> result = service.create(credential);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("password must be at least 8 character and contain a digit," +
                " a letter, and a non-digit/non-letter", result.getMessages().get(0));
    }

    @Test
    void shouldNotCreateWithPasswordThatDoesNotContainDigits(){
        Credential credential = new Credential();
        credential.setUsername("username");
        credential.setPassword("password!");

        Result<Player> result = service.create(credential);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("password must be at least 8 character and contain a digit," +
                " a letter, and a non-digit/non-letter", result.getMessages().get(0));
    }

    @Test
    void shouldNotCreateWithPasswordThatDoesNotContainSpecialCharacter(){
        Credential credential = new Credential();
        credential.setUsername("username");
        credential.setPassword("passw0rd");

        Result<Player> result = service.create(credential);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("password must be at least 8 character and contain a digit," +
                " a letter, and a non-digit/non-letter", result.getMessages().get(0));
    }

    //happy path -- update
    @Test
    void shouldUpdate(){
        Player player = new Player("username", "P@ssw0rd!");
        player.setPlayerId(1);
        player.setEnabled(true);
        player.setAuthorities(List.of("User", "Admin"));
        player.setDisplayName("displayUsername");
        player.setAccountBalance(500);
        player.setHoleCards(List.of(Card.QUEEN_OF_HEARTS, Card.ACE_OF_SPADES));
        player.setPlayersAction(false);
        player.setPosition(Position.SMALL_BLIND);

        when(repository.update(player)).thenReturn(true);

        Result<Player> result = service.update(player);

        assertEquals(ResultType.SUCCESS, result.getType());
    }

    //unhappy path -- update
    @Test
    void shouldNotUpdateWithNullPlayer() {
        Result<Player> result = service.update(null);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Player cannot be null.", result.getMessages().get(0));
    }

    @Test
    void shouldNotUpdatePlayerIdLessThanEqualToZero(){
        Player player = new Player("username", "P@ssw0rd!");
        player.setPlayerId(0);
        player.setEnabled(true);
        player.setAuthorities(List.of("User"));
        player.setDisplayName("displayUsername");
        player.setAccountBalance(500);
        player.setHoleCards(List.of(Card.QUEEN_OF_HEARTS, Card.ACE_OF_SPADES));
        player.setPlayersAction(false);
        player.setPosition(Position.SMALL_BLIND);

        Result<Player> result = service.update(player);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("not found."));
    }

    @Test
    void shouldNotUpdatePlayerWithNegativeAccountBalance(){
        Player player = new Player("username", "P@ssw0rd!");
        player.setPlayerId(1);
        player.setEnabled(true);
        player.setAuthorities(List.of("User"));
        player.setDisplayName("displayUsername");
        player.setAccountBalance(-500);
        player.setHoleCards(List.of(Card.QUEEN_OF_HEARTS, Card.ACE_OF_SPADES));
        player.setPlayersAction(false);
        player.setPosition(Position.SMALL_BLIND);

        Result<Player> result = service.update(player);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Account balance cannot be negative", result.getMessages().get(0));
    }

    @Test
    void shouldNotUpdatePlayerThatIsNeitherUserNorAdmin(){
        Player player = new Player("username", "P@ssw0rd!");
        player.setPlayerId(1);
        player.setEnabled(true);
        player.setAuthorities(List.of());
        player.setDisplayName("displayUsername");
        player.setAccountBalance(500);
        player.setHoleCards(List.of(Card.QUEEN_OF_HEARTS, Card.ACE_OF_SPADES));
        player.setPlayersAction(false);
        player.setPosition(Position.SMALL_BLIND);

        Result<Player> result = service.update(player);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Player must be either User and/or Admin", result.getMessages().get(0));
    }

    @Test
    void shouldNotUpdatePlayerDoesNotHaveExactlyZeroOrTwoHoleCards(){
        Player player = new Player("username", "P@ssw0rd!");
        player.setPlayerId(1);
        player.setEnabled(true);
        player.setAuthorities(List.of("User"));
        player.setDisplayName("displayUsername");
        player.setAccountBalance(500);
        player.setHoleCards(List.of(Card.QUEEN_OF_HEARTS));
        player.setPlayersAction(false);
        player.setPosition(Position.SMALL_BLIND);

        Result<Player> result = service.update(player);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Player must have either zero or exactly two hole cards", result.getMessages().get(0));
    }

   @Test
    void shouldLoadUserByUserName(){
        Credential credential = new Credential();
        credential.setUsername("username");
        credential.setPassword("P@ssw0rd!");

        Player player = new Player(0,"username", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true);
        when(repository.findByUsername("username")).thenReturn(player);

       UserDetails userDetails = service.loadUserByUsername(credential.getUsername());
       assertNotNull(userDetails);
   }

}

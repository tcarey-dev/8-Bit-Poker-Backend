package learn.poker.domain;

import learn.poker.data.PlayerRepository;
import learn.poker.models.Player;
import learn.poker.models.Room;
import learn.poker.security.Credential;
import org.apache.tomcat.jni.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

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

//    //happy path -- update
//    @Test
//    void shouldUpdate(){
//        Credential credential = new Credential();
//        credential.setUsername("username");
//        credential.setPassword("P@ssw0rd!");
//
//        Player player = new Player(0,"username", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true);
//
//        when(repository.update(player)).thenReturn(true);
//        Result<Player> result = service.update(credential);
//
//        assertTrue(result.isSuccess());
//        assertEquals(ResultType.SUCCESS, result.getType());
//    }
//
//    //unhappy path -- update
//    @Test
//    void shouldNotUpdateWithNullUserName(){
//        Credential credential = new Credential();
//        credential.setUsername(null);
//        credential.setPassword("P@ssw0rd!");
//
//        Result<Player> result = service.update(credential);
//
//        assertFalse(result.isSuccess());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("username is required", result.getMessages().get(0));
//    }
//
//    @Test
//    void shouldNotUpdateWithBlankUserName(){
//        Credential credential = new Credential();
//        credential.setUsername("");
//        credential.setPassword("P@ssw0rd!");
//
//        Result<Player> result = service.update(credential);
//
//        assertFalse(result.isSuccess());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("username is required", result.getMessages().get(0));
//    }
//
//    @Test
//    void shouldNotUpdateWithUserNameMoreThan50Characters(){
//        Credential credential = new Credential();
//        credential.setUsername("isUserNameisUserNameisUserNameisUserNameisUserNameisUserName");
//        credential.setPassword("P@ssw0rd!");
//
//        Result<Player> result = service.update(credential);
//
//        assertFalse(result.isSuccess());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("username must be less than 50 characters", result.getMessages().get(0));
//    }
//
//    @Test
//    void shouldNotUpdateWithNullPassword(){
//        Credential credential = new Credential();
//        credential.setUsername("username");
//        credential.setPassword(null);
//
//        Result<Player> result = service.update(credential);
//
//        assertFalse(result.isSuccess());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("password is required", result.getMessages().get(0));
//    }
//
//    @Test
//    void shouldNotUpdateWithPasswordLessThan8Characters(){
//        Credential credential = new Credential();
//        credential.setUsername("username");
//        credential.setPassword("");
//
//        Result<Player> result = service.update(credential);
//
//        assertFalse(result.isSuccess());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("password must be at least 8 character and contain a digit," +
//                " a letter, and a non-digit/non-letter", result.getMessages().get(0));
//    }
//
//    @Test
//    void shouldNotUpdateWithPasswordThatDoesNotContainDigits(){
//        Credential credential = new Credential();
//        credential.setUsername("username");
//        credential.setPassword("password!");
//
//        Result<Player> result = service.update(credential);
//
//        assertFalse(result.isSuccess());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("password must be at least 8 character and contain a digit," +
//                " a letter, and a non-digit/non-letter", result.getMessages().get(0));
//    }
//
//    @Test
//    void shouldNotUpdateWithPasswordThatDoesNotContainSpecialCharacter(){
//        Credential credential = new Credential();
//        credential.setUsername("username");
//        credential.setPassword("passw0rd");
//
//        Result<Player> result = service.update(credential);
//
//        assertFalse(result.isSuccess());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("password must be at least 8 character and contain a digit," +
//                " a letter, and a non-digit/non-letter", result.getMessages().get(0));
//    }
//
//    //happy path -- delete
//    @Test
//    void shouldDelete(){
//        when(respository.deleteById(1)).thenReturn(true);
//        assertTrue(service.deleteById(1));
//    }
//
//    //unhappy path -- delete
//    @Test
//    void shouldNotDeleteNonExistentRoom(){
//        assertFalse(service.deleteById(999));
//    }

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
